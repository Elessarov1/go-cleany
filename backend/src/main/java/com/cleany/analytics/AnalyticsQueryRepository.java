package com.cleany.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.reminder.CustomerReminderType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnalyticsQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AnalyticsBusinessHealthMetrics businessHealth(AnalyticsTimeWindow window) {
        return jdbcTemplate.queryForObject("""
                with completed_tasks as (
                    select cleaning.customer_id,
                           'CLEANING' service,
                           cleaning.completed_at
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :lifetimeFromInclusive
                       and cleaning.completed_at < :toExclusive
                    union all
                    select rental.customer_id,
                           'RENTAL' service,
                           rental.completed_at
                      from rental_booking rental
                     where rental.status = 'COMPLETED'
                       and rental.completed_at >= :lifetimeFromInclusive
                       and rental.completed_at < :toExclusive
                    union all
                    select transfer.customer_id,
                           'TRANSFER' service,
                           transfer.completed_at
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :lifetimeFromInclusive
                       and transfer.completed_at < :toExclusive
                ),
                period_tasks as (
                    select customer_id
                      from completed_tasks
                     where completed_at >= :fromInclusive
                       and (:service = 'ALL' or service = :service)
                ),
                active_customers as (
                    select distinct customer_id
                      from period_tasks
                ),
                lifetime_summary as (
                    select task.customer_id,
                           count(*) completed_tasks,
                           count(distinct task.service) services_used
                      from completed_tasks task
                      join active_customers active on active.customer_id = task.customer_id
                     group by task.customer_id
                )
                select (select count(*) from period_tasks) completed_tasks,
                       (select count(*) from active_customers) active_customers,
                       (select count(*) from lifetime_summary where completed_tasks >= 2) customers_with_two_plus,
                       (select count(*) from lifetime_summary where services_used >= 2) customers_with_two_plus_services
                """, parameters(window), (resultSet, rowNumber) -> {
            long completedTasks = resultSet.getLong("completed_tasks");
            long activeCustomers = resultSet.getLong("active_customers");
            long multiServiceCustomers = resultSet.getLong("customers_with_two_plus_services");
            return new AnalyticsBusinessHealthMetrics(
                    completedTasks,
                    activeCustomers,
                    ratio(completedTasks, activeCustomers),
                    resultSet.getLong("customers_with_two_plus"),
                    multiServiceCustomers,
                    ratio(multiServiceCustomers, activeCustomers)
            );
        });
    }

    public AnalyticsRetentionMetrics retention(AnalyticsTimeWindow window) {
        return jdbcTemplate.queryForObject("""
                with completed_tasks as (
                    select cleaning.id task_id,
                           cleaning.customer_id,
                           'CLEANING' service,
                           cleaning.completed_at
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :lifetimeFromInclusive
                       and cleaning.completed_at < :toExclusive
                    union all
                    select rental.id task_id,
                           rental.customer_id,
                           'RENTAL' service,
                           rental.completed_at
                      from rental_booking rental
                     where rental.status = 'COMPLETED'
                       and rental.completed_at >= :lifetimeFromInclusive
                       and rental.completed_at < :toExclusive
                    union all
                    select transfer.id task_id,
                           transfer.customer_id,
                           'TRANSFER' service,
                           transfer.completed_at
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :lifetimeFromInclusive
                       and transfer.completed_at < :toExclusive
                ),
                ordered_tasks as (
                    select task.*,
                           row_number() over (
                               partition by task.customer_id
                               order by task.completed_at, task.service, task.task_id
                           ) sequence_number,
                           lead(task.completed_at) over (
                               partition by task.customer_id
                               order by task.completed_at, task.service, task.task_id
                           ) second_completed_at
                      from completed_tasks task
                ),
                first_tasks as (
                    select customer_id,
                           service first_service,
                           completed_at first_completed_at,
                           second_completed_at
                      from ordered_tasks
                     where sequence_number = 1
                       and (:service = 'ALL' or service = :service)
                ),
                period_cohort as (
                    select *
                      from first_tasks
                     where first_completed_at >= :fromInclusive
                       and first_completed_at < :toExclusive
                ),
                cumulative_mature_90 as (
                    select *
                      from first_tasks
                     where first_completed_at + interval '90 days' <= :toExclusive
                )
                select count(*) filter (
                           where first_completed_at + interval '30 days' <= :toExclusive
                       ) cohort_30,
                       count(*) filter (
                           where first_completed_at + interval '30 days' <= :toExclusive
                             and second_completed_at <= first_completed_at + interval '30 days'
                       ) repeated_30,
                       count(*) filter (
                           where first_completed_at + interval '90 days' <= :toExclusive
                       ) cohort_90,
                       count(*) filter (
                           where first_completed_at + interval '90 days' <= :toExclusive
                             and second_completed_at <= first_completed_at + interval '90 days'
                       ) repeated_90,
                       (select count(*) from cumulative_mature_90) second_order_cohort,
                       (select count(*)
                          from cumulative_mature_90
                         where second_completed_at <= first_completed_at + interval '90 days'
                       ) second_order_converted,
                       (select percentile_cont(0.5) within group (
                                   order by extract(epoch from (second_completed_at - first_completed_at)) / 86400.0
                               )
                          from cumulative_mature_90
                         where second_completed_at <= first_completed_at + interval '90 days'
                       ) median_days_to_second
                  from period_cohort
                """, parameters(window), (resultSet, rowNumber) -> {
            long cohort30 = resultSet.getLong("cohort_30");
            long repeated30 = resultSet.getLong("repeated_30");
            long cohort90 = resultSet.getLong("cohort_90");
            long repeated90 = resultSet.getLong("repeated_90");
            long secondOrderCohort = resultSet.getLong("second_order_cohort");
            long secondOrderConverted = resultSet.getLong("second_order_converted");
            BigDecimal medianDays = resultSet.getBigDecimal("median_days_to_second");
            return new AnalyticsRetentionMetrics(
                    cohort(cohort30, repeated30),
                    cohort(cohort90, repeated90),
                    cohort(secondOrderCohort, secondOrderConverted),
                    medianDays == null ? null : medianDays.setScale(1, RoundingMode.HALF_UP)
            );
        });
    }

    public List<AnalyticsTransitionMetric> transitions(AnalyticsTimeWindow window) {
        return jdbcTemplate.query("""
                with completed_tasks as (
                    select cleaning.id task_id,
                           cleaning.customer_id,
                           'CLEANING' service,
                           cleaning.completed_at
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :lifetimeFromInclusive
                       and cleaning.completed_at < :toExclusive
                    union all
                    select rental.id task_id,
                           rental.customer_id,
                           'RENTAL' service,
                           rental.completed_at
                      from rental_booking rental
                     where rental.status = 'COMPLETED'
                       and rental.completed_at >= :lifetimeFromInclusive
                       and rental.completed_at < :toExclusive
                    union all
                    select transfer.id task_id,
                           transfer.customer_id,
                           'TRANSFER' service,
                           transfer.completed_at
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :lifetimeFromInclusive
                       and transfer.completed_at < :toExclusive
                ),
                ordered_tasks as (
                    select task.*,
                           row_number() over (
                               partition by task.customer_id
                               order by task.completed_at, task.service, task.task_id
                           ) sequence_number,
                           lead(task.service) over (
                               partition by task.customer_id
                               order by task.completed_at, task.service, task.task_id
                           ) next_service
                      from completed_tasks task
                ),
                first_tasks as (
                    select customer_id,
                           service first_service,
                           completed_at first_completed_at,
                           next_service
                      from ordered_tasks
                     where sequence_number = 1
                       and completed_at >= :fromInclusive
                       and completed_at < :toExclusive
                ),
                transition_definitions(from_service, to_service, sort_order) as (
                    values ('CLEANING', 'CLEANING', 1),
                           ('RENTAL', 'TRANSFER', 2),
                           ('RENTAL', 'CLEANING', 3),
                           ('TRANSFER', 'TRANSFER', 4)
                )
                select definition.from_service,
                       definition.to_service,
                       count(first_task.customer_id) cohort_customers,
                       count(first_task.customer_id) filter (
                           where first_task.next_service = definition.to_service
                       ) converted_customers
                  from transition_definitions definition
                  left join first_tasks first_task
                    on first_task.first_service = definition.from_service
                 where :service = 'ALL' or definition.from_service = :service
                 group by definition.from_service, definition.to_service, definition.sort_order
                 order by definition.sort_order
                """, parameters(window), (resultSet, rowNumber) -> {
            long cohortCustomers = resultSet.getLong("cohort_customers");
            long convertedCustomers = resultSet.getLong("converted_customers");
            return new AnalyticsTransitionMetric(
                    PlatformService.valueOf(resultSet.getString("from_service")),
                    PlatformService.valueOf(resultSet.getString("to_service")),
                    cohortCustomers,
                    convertedCustomers,
                    nullableRatio(convertedCustomers, cohortCustomers)
            );
        });
    }

    public AnalyticsRentalTransferMetrics rentalToTransfer(AnalyticsTimeWindow window) {
        List<RentalTransferMetricRow> rows = jdbcTemplate.query("""
                with shown_contexts as (
                    select event.customer_id,
                           event.rental_booking_id,
                           event.context_type,
                           min(event.occurred_at) shown_at
                      from rental_transfer_action_event event
                     where event.event_type = 'CTA_SHOWN'
                       and event.occurred_at >= :fromInclusive
                       and event.occurred_at < :toExclusive
                       and :service in ('ALL', 'RENTAL')
                     group by event.customer_id, event.rental_booking_id, event.context_type
                ),
                context_metrics as (
                    select shown.*,
                           exists (
                               select 1
                                 from rental_transfer_action_event started
                                where started.customer_id = shown.customer_id
                                  and started.rental_booking_id = shown.rental_booking_id
                                  and started.context_type = shown.context_type
                                  and started.event_type = 'PREFILL_STARTED'
                                  and started.occurred_at < :toExclusive
                           ) started,
                           (select min(transfer.created_at)
                              from transfer_booking transfer
                             where transfer.customer_id = shown.customer_id
                               and transfer.source_rental_booking_id = shown.rental_booking_id
                               and transfer.rental_context_type = shown.context_type
                               and transfer.created_at < :toExclusive
                           ) first_created_at,
                           exists (
                               select 1
                                 from transfer_booking transfer
                                where transfer.customer_id = shown.customer_id
                                  and transfer.source_rental_booking_id = shown.rental_booking_id
                                  and transfer.rental_context_type = shown.context_type
                                  and transfer.status = 'COMPLETED'
                                  and transfer.completed_at < :toExclusive
                           ) completed
                      from shown_contexts shown
                )
                select context_type,
                       grouping(context_type) total,
                       count(*) shown_sources,
                       count(*) filter (where started) started_sources,
                       count(*) filter (where first_created_at is not null) created_sources,
                       count(*) filter (where completed) completed_sources,
                       percentile_cont(0.5) within group (
                           order by extract(epoch from (first_created_at - shown_at)) / 3600.0
                       ) filter (where first_created_at is not null) median_hours
                  from context_metrics
                 group by grouping sets ((context_type), ())
                 order by grouping(context_type) desc, context_type
                """, parameters(window), (resultSet, rowNumber) -> {
            long shown = resultSet.getLong("shown_sources");
            long started = resultSet.getLong("started_sources");
            long created = resultSet.getLong("created_sources");
            long completed = resultSet.getLong("completed_sources");
            BigDecimal median = resultSet.getBigDecimal("median_hours");
            return new RentalTransferMetricRow(
                    resultSet.getInt("total") == 1,
                    resultSet.getString("context_type") == null
                            ? null
                            : RentalTransferContextType.valueOf(resultSet.getString("context_type")),
                    new AnalyticsActionFunnelMetric(
                            shown,
                            started,
                            created,
                            completed,
                            nullableRatio(started, shown),
                            nullableRatio(created, shown),
                            nullableRatio(completed, created),
                            median == null ? null : median.setScale(1, RoundingMode.HALF_UP)
                    )
            );
        });
        AnalyticsActionFunnelMetric total = rows.stream()
                .filter(RentalTransferMetricRow::total)
                .map(RentalTransferMetricRow::funnel)
                .findFirst()
                .orElseGet(AnalyticsQueryRepository::emptyActionFunnel);
        List<AnalyticsRentalTransferContextMetric> byContext = rows.stream()
                .filter(row -> !row.total())
                .map(row -> new AnalyticsRentalTransferContextMetric(row.context(), row.funnel()))
                .toList();
        return new AnalyticsRentalTransferMetrics(
                total,
                byContext.isEmpty() ? Collections.emptyList() : byContext
        );
    }

    public AnalyticsRentalTransferBenefitMetrics rentalTransferBenefit(AnalyticsTimeWindow window) {
        List<RentalTransferBenefitFunnelRow> funnels = jdbcTemplate.query("""
                with shown_contexts as (
                    select event.customer_id,
                           event.rental_booking_id,
                           event.context_type,
                           min(event.occurred_at) shown_at
                      from rental_transfer_action_event event
                     where event.event_type = 'BENEFIT_SHOWN'
                       and event.occurred_at >= :fromInclusive
                       and event.occurred_at < :toExclusive
                       and :service in ('ALL', 'RENTAL')
                     group by event.customer_id, event.rental_booking_id, event.context_type
                ),
                shown_sources as (
                    select customer_id,
                           rental_booking_id,
                           min(shown_at) shown_at
                      from shown_contexts
                     group by customer_id, rental_booking_id
                ),
                source_metrics as (
                    select shown.*,
                           exists (
                               select 1
                                 from rental_transfer_action_event started
                                where started.customer_id = shown.customer_id
                                  and started.rental_booking_id = shown.rental_booking_id
                                  and started.event_type = 'BENEFIT_PREFILL_STARTED'
                                  and started.occurred_at >= shown.shown_at
                                  and started.occurred_at < :toExclusive
                           ) started,
                           (select min(transfer.created_at)
                              from transfer_booking transfer
                             where transfer.customer_id = shown.customer_id
                               and transfer.source_rental_booking_id = shown.rental_booking_id
                               and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                               and transfer.created_at >= shown.shown_at
                               and transfer.created_at < :toExclusive
                           ) first_created_at,
                           exists (
                               select 1
                                 from transfer_booking transfer
                                where transfer.customer_id = shown.customer_id
                                  and transfer.source_rental_booking_id = shown.rental_booking_id
                                  and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                                  and transfer.created_at >= shown.shown_at
                                  and transfer.status = 'COMPLETED'
                                  and transfer.completed_at < :toExclusive
                           ) completed
                      from shown_sources shown
                ),
                context_metrics as (
                    select shown.*,
                           exists (
                               select 1
                                 from rental_transfer_action_event started
                                where started.customer_id = shown.customer_id
                                  and started.rental_booking_id = shown.rental_booking_id
                                  and started.context_type = shown.context_type
                                  and started.event_type = 'BENEFIT_PREFILL_STARTED'
                                  and started.occurred_at >= shown.shown_at
                                  and started.occurred_at < :toExclusive
                           ) started,
                           (select min(transfer.created_at)
                              from transfer_booking transfer
                             where transfer.customer_id = shown.customer_id
                               and transfer.source_rental_booking_id = shown.rental_booking_id
                               and transfer.rental_context_type = shown.context_type
                               and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                               and transfer.created_at >= shown.shown_at
                               and transfer.created_at < :toExclusive
                           ) first_created_at,
                           exists (
                               select 1
                                 from transfer_booking transfer
                                where transfer.customer_id = shown.customer_id
                                  and transfer.source_rental_booking_id = shown.rental_booking_id
                                  and transfer.rental_context_type = shown.context_type
                                  and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                                  and transfer.created_at >= shown.shown_at
                                  and transfer.status = 'COMPLETED'
                                  and transfer.completed_at < :toExclusive
                           ) completed
                      from shown_contexts shown
                ),
                metric_rows as (
                    select true total,
                           null::varchar context_type,
                           count(*) shown_sources,
                           count(*) filter (where started) started_sources,
                           count(*) filter (where first_created_at is not null) created_sources,
                           count(*) filter (where completed) completed_sources,
                           percentile_cont(0.5) within group (
                               order by extract(epoch from (first_created_at - shown_at)) / 3600.0
                           ) filter (where first_created_at is not null) median_hours
                      from source_metrics
                    union all
                    select false total,
                           context_type,
                           count(*) shown_sources,
                           count(*) filter (where started) started_sources,
                           count(*) filter (where first_created_at is not null) created_sources,
                           count(*) filter (where completed) completed_sources,
                           percentile_cont(0.5) within group (
                               order by extract(epoch from (first_created_at - shown_at)) / 3600.0
                           ) filter (where first_created_at is not null) median_hours
                      from context_metrics
                     group by context_type
                )
                select *
                  from metric_rows
                 order by total desc, context_type
                """, parameters(window), (resultSet, rowNumber) -> {
            long shown = resultSet.getLong("shown_sources");
            long started = resultSet.getLong("started_sources");
            long created = resultSet.getLong("created_sources");
            long completed = resultSet.getLong("completed_sources");
            BigDecimal median = resultSet.getBigDecimal("median_hours");
            String context = resultSet.getString("context_type");
            return new RentalTransferBenefitFunnelRow(
                    resultSet.getBoolean("total"),
                    context == null ? null : RentalTransferContextType.valueOf(context),
                    new AnalyticsActionFunnelMetric(
                            shown,
                            started,
                            created,
                            completed,
                            nullableRatio(started, shown),
                            nullableRatio(created, shown),
                            nullableRatio(completed, created),
                            median == null ? null : median.setScale(1, RoundingMode.HALF_UP)
                    )
            );
        });
        List<RentalTransferBenefitAmountRow> amounts = rentalTransferBenefitAmounts(window);
        AnalyticsRentalTransferBenefitMetric total = funnels.stream()
                .filter(RentalTransferBenefitFunnelRow::total)
                .findFirst()
                .map(row -> benefitMetric(row.funnel(), amounts, true, null))
                .orElseGet(() -> benefitMetric(emptyActionFunnel(), amounts, true, null));
        List<AnalyticsRentalTransferBenefitContextMetric> byContext = funnels.stream()
                .filter(row -> !row.total())
                .map(row -> new AnalyticsRentalTransferBenefitContextMetric(
                        row.context(),
                        benefitMetric(row.funnel(), amounts, false, row.context())
                ))
                .toList();
        return new AnalyticsRentalTransferBenefitMetrics(
                total,
                byContext.isEmpty() ? Collections.emptyList() : byContext
        );
    }

    private List<RentalTransferBenefitAmountRow> rentalTransferBenefitAmounts(
            AnalyticsTimeWindow window
    ) {
        return jdbcTemplate.query("""
                with shown_contexts as (
                    select event.customer_id,
                           event.rental_booking_id,
                           event.context_type,
                           min(event.occurred_at) shown_at
                      from rental_transfer_action_event event
                     where event.event_type = 'BENEFIT_SHOWN'
                       and event.occurred_at >= :fromInclusive
                       and event.occurred_at < :toExclusive
                       and :service in ('ALL', 'RENTAL')
                     group by event.customer_id, event.rental_booking_id, event.context_type
                ),
                shown_sources as (
                    select customer_id,
                           rental_booking_id,
                           min(shown_at) shown_at
                      from shown_contexts
                     group by customer_id, rental_booking_id
                ),
                amount_rows as (
                    select true total,
                           null::varchar context_type,
                           transfer.price_currency currency,
                           count(*) completed_transfers,
                           sum(transfer.base_price_amount) base_amount,
                           sum(transfer.discount_amount) discount_amount,
                           sum(transfer.price_amount) payable_amount
                      from shown_sources shown
                      join transfer_booking transfer
                        on transfer.customer_id = shown.customer_id
                       and transfer.source_rental_booking_id = shown.rental_booking_id
                       and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                       and transfer.created_at >= shown.shown_at
                       and transfer.status = 'COMPLETED'
                       and transfer.completed_at < :toExclusive
                     group by transfer.price_currency
                    union all
                    select false total,
                           shown.context_type,
                           transfer.price_currency currency,
                           count(*) completed_transfers,
                           sum(transfer.base_price_amount) base_amount,
                           sum(transfer.discount_amount) discount_amount,
                           sum(transfer.price_amount) payable_amount
                      from shown_contexts shown
                      join transfer_booking transfer
                        on transfer.customer_id = shown.customer_id
                       and transfer.source_rental_booking_id = shown.rental_booking_id
                       and transfer.rental_context_type = shown.context_type
                       and transfer.benefit_type = 'RENTAL_FIRST_TRANSFER'
                       and transfer.created_at >= shown.shown_at
                       and transfer.status = 'COMPLETED'
                       and transfer.completed_at < :toExclusive
                     group by shown.context_type, transfer.price_currency
                )
                select *
                  from amount_rows
                 order by total desc, context_type, currency
                """, parameters(window), (resultSet, rowNumber) -> {
            String context = resultSet.getString("context_type");
            return new RentalTransferBenefitAmountRow(
                    resultSet.getBoolean("total"),
                    context == null ? null : RentalTransferContextType.valueOf(context),
                    new AnalyticsRentalTransferBenefitAmount(
                            resultSet.getString("currency"),
                            resultSet.getLong("completed_transfers"),
                            resultSet.getBigDecimal("base_amount").setScale(2, RoundingMode.HALF_UP),
                            resultSet.getBigDecimal("discount_amount").setScale(2, RoundingMode.HALF_UP),
                            resultSet.getBigDecimal("payable_amount").setScale(2, RoundingMode.HALF_UP)
                    )
            );
        });
    }

    private static AnalyticsRentalTransferBenefitMetric benefitMetric(
            AnalyticsActionFunnelMetric funnel,
            List<RentalTransferBenefitAmountRow> amounts,
            boolean total,
            RentalTransferContextType context
    ) {
        List<AnalyticsRentalTransferBenefitAmount> matchingAmounts = amounts.stream()
                .filter(row -> row.total() == total && row.context() == context)
                .map(RentalTransferBenefitAmountRow::amount)
                .toList();
        return new AnalyticsRentalTransferBenefitMetric(
                funnel,
                matchingAmounts.isEmpty() ? Collections.emptyList() : matchingAmounts
        );
    }

    public List<AnalyticsRepeatActionMetric> repeatActions(AnalyticsTimeWindow window) {
        return jdbcTemplate.query("""
                with source_tasks as (
                    select cleaning.id source_id,
                           cleaning.customer_id,
                           'CLEANING' service,
                           cleaning.completed_at source_completed_at
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :fromInclusive
                       and cleaning.completed_at < :toExclusive
                       and :service in ('ALL', 'CLEANING')
                    union all
                    select transfer.id source_id,
                           transfer.customer_id,
                           'TRANSFER' service,
                           transfer.completed_at source_completed_at
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :fromInclusive
                       and transfer.completed_at < :toExclusive
                       and :service in ('ALL', 'TRANSFER')
                ),
                repeat_targets as (
                    select cleaning.repeat_source_order_id source_id,
                           'CLEANING' service,
                           cleaning.created_at,
                           cleaning.status,
                           cleaning.completed_at
                      from cleaning_order cleaning
                     where cleaning.repeat_source_order_id is not null
                       and cleaning.created_at < :toExclusive
                    union all
                    select transfer.repeat_source_booking_id source_id,
                           'TRANSFER' service,
                           transfer.created_at,
                           transfer.status,
                           transfer.completed_at
                      from transfer_booking transfer
                     where transfer.repeat_source_booking_id is not null
                       and transfer.created_at < :toExclusive
                ),
                source_metrics as (
                    select source.*,
                           exists (
                               select 1
                                 from repeat_action_event event
                                where event.customer_id = source.customer_id
                                  and event.service = source.service
                                  and event.source_entity_id = source.source_id
                                  and event.event_type = 'CTA_SHOWN'
                                  and event.occurred_at < :toExclusive
                           ) shown,
                           exists (
                               select 1
                                 from repeat_action_event event
                                where event.customer_id = source.customer_id
                                  and event.service = source.service
                                  and event.source_entity_id = source.source_id
                                  and event.event_type = 'PREFILL_STARTED'
                                  and event.occurred_at < :toExclusive
                           ) started,
                           (select min(target.created_at)
                              from repeat_targets target
                             where target.service = source.service
                               and target.source_id = source.source_id
                           ) first_repeat_created_at,
                           exists (
                               select 1
                                 from repeat_targets target
                                where target.service = source.service
                                  and target.source_id = source.source_id
                                  and target.status = 'COMPLETED'
                                  and target.completed_at < :toExclusive
                           ) completed
                      from source_tasks source
                )
                select service,
                       count(*) filter (where shown) shown_sources,
                       count(*) filter (where started) started_sources,
                       count(*) filter (where first_repeat_created_at is not null) created_sources,
                       count(*) filter (where completed) completed_sources,
                       percentile_cont(0.5) within group (
                           order by extract(epoch from (first_repeat_created_at - source_completed_at)) / 3600.0
                       ) filter (where first_repeat_created_at is not null) median_hours
                  from source_metrics
                 group by service
                 order by service
                """, parameters(window), (resultSet, rowNumber) -> {
            long shown = resultSet.getLong("shown_sources");
            long started = resultSet.getLong("started_sources");
            long created = resultSet.getLong("created_sources");
            long completed = resultSet.getLong("completed_sources");
            BigDecimal medianHours = resultSet.getBigDecimal("median_hours");
            return new AnalyticsRepeatActionMetric(
                    PlatformService.valueOf(resultSet.getString("service")),
                    shown,
                    started,
                    created,
                    completed,
                    nullableRatio(started, shown),
                    nullableRatio(completed, created),
                    medianHours == null ? null : medianHours.setScale(1, RoundingMode.HALF_UP)
            );
        });
    }

    public List<AnalyticsReminderMetric> reminders(AnalyticsTimeWindow window) {
        return jdbcTemplate.query("""
                with definitions(type, source_service, sort_order) as (
                    values ('CLEANING_REPEAT', 'CLEANING', 1),
                           ('RENTAL_CHECKOUT_TRANSFER', 'RENTAL', 2),
                           ('TRANSFER_UPCOMING', 'TRANSFER', 3)
                ),
                reminder_cohort as (
                    select reminder.id,
                           reminder.type,
                           reminder.source_service,
                           reminder.source_entity_id,
                           reminder.notified_at
                      from customer_reminder reminder
                     where reminder.status = 'NOTIFIED'
                       and reminder.notified_at >= :fromInclusive
                       and reminder.notified_at < :toExclusive
                       and (:service = 'ALL' or reminder.source_service = :service)
                ),
                reminder_outcomes as (
                    select reminder.*,
                           case reminder.type
                               when 'CLEANING_REPEAT' then exists (
                                   select 1
                                     from cleaning_order target
                                    where target.repeat_source_order_id = reminder.source_entity_id
                                      and target.created_at >= reminder.notified_at
                                      and target.created_at < :toExclusive
                               )
                               when 'RENTAL_CHECKOUT_TRANSFER' then exists (
                                   select 1
                                     from transfer_booking target
                                    where target.source_rental_booking_id = reminder.source_entity_id
                                      and target.rental_context_type = 'CHECKOUT'
                                      and target.created_at >= reminder.notified_at
                                      and target.created_at < :toExclusive
                               )
                               else null
                           end target_created,
                           case reminder.type
                               when 'CLEANING_REPEAT' then exists (
                                   select 1
                                     from cleaning_order target
                                    where target.repeat_source_order_id = reminder.source_entity_id
                                      and target.status = 'COMPLETED'
                                      and target.created_at >= reminder.notified_at
                                      and target.completed_at < :toExclusive
                               )
                               when 'RENTAL_CHECKOUT_TRANSFER' then exists (
                                   select 1
                                     from transfer_booking target
                                    where target.source_rental_booking_id = reminder.source_entity_id
                                      and target.rental_context_type = 'CHECKOUT'
                                      and target.status = 'COMPLETED'
                                      and target.created_at >= reminder.notified_at
                                      and target.completed_at < :toExclusive
                               )
                               when 'TRANSFER_UPCOMING' then exists (
                                   select 1
                                     from transfer_booking source
                                    where source.id = reminder.source_entity_id
                                      and source.status = 'COMPLETED'
                                      and source.completed_at >= reminder.notified_at
                                      and source.completed_at < :toExclusive
                               )
                           end target_completed
                      from reminder_cohort reminder
                )
                select definition.type,
                       definition.source_service,
                       count(outcome.id) notifications_created,
                       count(outcome.id) filter (where outcome.target_created) target_created,
                       count(outcome.id) filter (where outcome.target_completed) target_completed
                  from definitions definition
                  left join reminder_outcomes outcome on outcome.type = definition.type
                 where :service = 'ALL' or definition.source_service = :service
                 group by definition.type, definition.source_service, definition.sort_order
                 order by definition.sort_order
                """, parameters(window), (resultSet, rowNumber) -> {
            CustomerReminderType type = CustomerReminderType.valueOf(resultSet.getString("type"));
            long notified = resultSet.getLong("notifications_created");
            long created = resultSet.getLong("target_created");
            long completed = resultSet.getLong("target_completed");
            boolean operational = type == CustomerReminderType.TRANSFER_UPCOMING;
            return new AnalyticsReminderMetric(
                    type,
                    PlatformService.valueOf(resultSet.getString("source_service")),
                    notified,
                    operational ? null : created,
                    completed,
                    operational ? null : nullableRatio(created, notified),
                    nullableRatio(completed, operational ? notified : created)
            );
        });
    }

    public AnalyticsCustomerMetrics customerMetrics(AnalyticsTimeWindow window) {
        Map<String, Object> parameters = parameters(window);
        return jdbcTemplate.queryForObject("""
                with period_transactions as (
                    select cleaning.customer_id
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :fromInclusive
                       and cleaning.completed_at < :toExclusive
                       and :service in ('ALL', 'CLEANING')
                    union all
                    select rental.customer_id
                      from rental_booking rental
                     where rental.status = 'COMPLETED'
                       and rental.completed_at >= :fromInclusive
                       and rental.completed_at < :toExclusive
                       and :service in ('ALL', 'RENTAL')
                    union all
                    select transfer.customer_id
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :fromInclusive
                       and transfer.completed_at < :toExclusive
                       and :service in ('ALL', 'TRANSFER')
                ),
                active_customers as (
                    select distinct customer_id from period_transactions
                ),
                lifetime_counts as (
                    select successful.customer_id, count(*) transaction_count
                      from (
                          select customer_id
                            from cleaning_order
                           where status = 'COMPLETED'
                             and completed_at >= :lifetimeFromInclusive
                          union all
                          select customer_id
                            from rental_booking
                           where status = 'COMPLETED'
                             and completed_at >= :lifetimeFromInclusive
                          union all
                          select customer_id
                            from transfer_booking
                           where status = 'COMPLETED'
                             and completed_at >= :lifetimeFromInclusive
                      ) successful
                     where successful.customer_id in (select customer_id from active_customers)
                     group by successful.customer_id
                )
                select (
                           select count(*)
                             from customer_account customer
                             left join customer_acquisition acquisition
                               on acquisition.customer_id = customer.id
                            where customer.created_at >= :fromInclusive
                              and customer.created_at < :toExclusive
                              and (
                                  :service = 'ALL'
                                  or acquisition.first_touch_service = :service
                              )
                       ) new_customers,
                       (select count(*) from active_customers) active_customers,
                       (select count(*) from lifetime_counts where transaction_count >= 2) repeat_customers
                """, parameters, (resultSet, rowNumber) -> {
            long newCustomers = resultSet.getLong("new_customers");
            long activeCustomers = resultSet.getLong("active_customers");
            long repeatCustomers = resultSet.getLong("repeat_customers");
            BigDecimal repeatRate = activeCustomers == 0
                    ? BigDecimal.ZERO.setScale(4, RoundingMode.UNNECESSARY)
                    : BigDecimal.valueOf(repeatCustomers)
                            .divide(BigDecimal.valueOf(activeCustomers), 4, RoundingMode.HALF_UP);
            return new AnalyticsCustomerMetrics(
                    newCustomers,
                    activeCustomers,
                    repeatCustomers,
                    repeatRate
            );
        });
    }

    public List<AverageCheckMetric> averageChecks(AnalyticsTimeWindow window) {
        return jdbcTemplate.query("""
                select successful.service,
                       successful.currency,
                       avg(successful.amount) average_amount,
                       count(*) completed_transactions
                  from (
                      select 'CLEANING' service,
                             cleaning.currency,
                             cleaning.final_customer_price amount
                        from cleaning_order cleaning
                       where cleaning.status = 'COMPLETED'
                         and cleaning.completed_at >= :fromInclusive
                         and cleaning.completed_at < :toExclusive
                         and :service in ('ALL', 'CLEANING')
                      union all
                      select 'RENTAL' service,
                             rental.currency,
                             rental.total_price amount
                        from rental_booking rental
                       where rental.status = 'COMPLETED'
                         and rental.completed_at >= :fromInclusive
                         and rental.completed_at < :toExclusive
                         and :service in ('ALL', 'RENTAL')
                      union all
                      select 'TRANSFER' service,
                             transfer.price_currency currency,
                             transfer.price_amount amount
                        from transfer_booking transfer
                       where transfer.status = 'COMPLETED'
                         and transfer.completed_at >= :fromInclusive
                         and transfer.completed_at < :toExclusive
                         and :service in ('ALL', 'TRANSFER')
                  ) successful
                 group by successful.service, successful.currency
                 order by successful.service, successful.currency
                """, parameters(window), (resultSet, rowNumber) -> new AverageCheckMetric(
                PlatformService.valueOf(resultSet.getString("service")),
                resultSet.getString("currency"),
                resultSet.getBigDecimal("average_amount").setScale(2, RoundingMode.HALF_UP),
                resultSet.getLong("completed_transactions")
        ));
    }

    public List<AcquisitionMetric> acquisitionMetrics(AnalyticsTimeWindow window) {
        return jdbcTemplate.query("""
                with new_counts as (
                    select coalesce(acquisition.channel, 'ORGANIC') channel,
                           acquisition.campaign_id,
                           count(*) new_customers
                      from customer_account customer
                      left join customer_acquisition acquisition
                        on acquisition.customer_id = customer.id
                     where customer.created_at >= :fromInclusive
                       and customer.created_at < :toExclusive
                       and (
                           :service = 'ALL'
                           or acquisition.first_touch_service = :service
                       )
                     group by coalesce(acquisition.channel, 'ORGANIC'), acquisition.campaign_id
                ),
                entry_counts as (
                    select campaign.channel,
                           entry.campaign_id,
                           count(*) entries
                      from acquisition_campaign_entry entry
                      join acquisition_campaign campaign on campaign.id = entry.campaign_id
                     where entry.occurred_at >= :fromInclusive
                       and entry.occurred_at < :toExclusive
                       and (
                           :service = 'ALL'
                           or campaign.target_service in ('PLATFORM', :service)
                       )
                     group by campaign.channel, entry.campaign_id
                ),
                successful_transactions as (
                    select cleaning.customer_id
                      from cleaning_order cleaning
                     where cleaning.status = 'COMPLETED'
                       and cleaning.completed_at >= :fromInclusive
                       and cleaning.completed_at < :toExclusive
                       and :service in ('ALL', 'CLEANING')
                    union all
                    select rental.customer_id
                      from rental_booking rental
                     where rental.status = 'COMPLETED'
                       and rental.completed_at >= :fromInclusive
                       and rental.completed_at < :toExclusive
                       and :service in ('ALL', 'RENTAL')
                    union all
                    select transfer.customer_id
                      from transfer_booking transfer
                     where transfer.status = 'COMPLETED'
                       and transfer.completed_at >= :fromInclusive
                       and transfer.completed_at < :toExclusive
                       and :service in ('ALL', 'TRANSFER')
                ),
                transaction_counts as (
                    select coalesce(acquisition.channel, 'ORGANIC') channel,
                           acquisition.campaign_id,
                           count(*) completed_transactions
                      from successful_transactions completed
                      left join customer_acquisition acquisition
                        on acquisition.customer_id = completed.customer_id
                     group by coalesce(acquisition.channel, 'ORGANIC'), acquisition.campaign_id
                ),
                metric_keys as (
                    select channel, campaign_id from new_counts
                    union
                    select channel, campaign_id from entry_counts
                    union
                    select channel, campaign_id from transaction_counts
                )
                select keys.channel,
                       keys.campaign_id,
                       campaign.name campaign_name,
                       campaign.medium,
                       coalesce(entries.entries, 0) entries,
                       coalesce(customers.new_customers, 0) new_customers,
                       coalesce(transactions.completed_transactions, 0) completed_transactions
                  from metric_keys keys
                  left join acquisition_campaign campaign on campaign.id = keys.campaign_id
                  left join entry_counts entries
                    on entries.channel = keys.channel
                   and entries.campaign_id is not distinct from keys.campaign_id
                  left join new_counts customers
                    on customers.channel = keys.channel
                   and customers.campaign_id is not distinct from keys.campaign_id
                  left join transaction_counts transactions
                    on transactions.channel = keys.channel
                   and transactions.campaign_id is not distinct from keys.campaign_id
                 order by coalesce(customers.new_customers, 0) desc,
                          coalesce(entries.entries, 0) desc,
                          keys.channel,
                          campaign.name
                """, parameters(window), this::acquisitionMetric);
    }

    private AcquisitionMetric acquisitionMetric(ResultSet resultSet, int rowNumber) throws SQLException {
        String medium = resultSet.getString("medium");
        long campaignId = resultSet.getLong("campaign_id");
        return new AcquisitionMetric(
                AcquisitionChannel.valueOf(resultSet.getString("channel")),
                resultSet.wasNull() ? null : campaignId,
                resultSet.getString("campaign_name"),
                medium == null ? null : AcquisitionMedium.valueOf(medium),
                resultSet.getLong("entries"),
                resultSet.getLong("new_customers"),
                resultSet.getLong("completed_transactions")
        );
    }

    private static Map<String, Object> parameters(AnalyticsTimeWindow window) {
        return Map.of(
                "fromInclusive", OffsetDateTime.ofInstant(window.fromInclusive(), ZoneOffset.UTC),
                "toExclusive", OffsetDateTime.ofInstant(window.toExclusive(), ZoneOffset.UTC),
                "lifetimeFromInclusive", OffsetDateTime.ofInstant(
                        window.lifetimeFromInclusive(),
                        ZoneOffset.UTC
                ),
                "service", window.service().name()
        );
    }

    private static AnalyticsCohortMetric cohort(long cohortCustomers, long convertedCustomers) {
        return new AnalyticsCohortMetric(
                cohortCustomers,
                convertedCustomers,
                nullableRatio(convertedCustomers, cohortCustomers)
        );
    }

    private static BigDecimal ratio(long numerator, long denominator) {
        return denominator == 0
                ? BigDecimal.ZERO.setScale(4, RoundingMode.UNNECESSARY)
                : BigDecimal.valueOf(numerator)
                        .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal nullableRatio(long numerator, long denominator) {
        return denominator == 0 ? null : ratio(numerator, denominator);
    }

    private static AnalyticsActionFunnelMetric emptyActionFunnel() {
        return new AnalyticsActionFunnelMetric(0, 0, 0, 0, null, null, null, null);
    }

    private record RentalTransferMetricRow(
            boolean total,
            RentalTransferContextType context,
            AnalyticsActionFunnelMetric funnel
    ) {
    }

    private record RentalTransferBenefitFunnelRow(
            boolean total,
            RentalTransferContextType context,
            AnalyticsActionFunnelMetric funnel
    ) {
    }

    private record RentalTransferBenefitAmountRow(
            boolean total,
            RentalTransferContextType context,
            AnalyticsRentalTransferBenefitAmount amount
    ) {
    }
}
