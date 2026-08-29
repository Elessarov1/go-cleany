package com.cleany.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cleany.catalog.PlatformService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnalyticsQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

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
}
