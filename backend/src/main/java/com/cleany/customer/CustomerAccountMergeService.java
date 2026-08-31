package com.cleany.customer;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.EntityManager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerAccountMergeService {

    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public long mergeInto(long targetCustomerId, long sourceCustomerId) {
        if (targetCustomerId == sourceCustomerId) {
            return targetCustomerId;
        }
        List<Long> orderedIds = List.of(
                Math.min(targetCustomerId, sourceCustomerId),
                Math.max(targetCustomerId, sourceCustomerId)
        );
        Map<Long, CustomerAccount> lockedAccounts = accountRepository.findAllByIdForUpdate(orderedIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(CustomerAccount::getId, account -> account));
        if (lockedAccounts.size() != 2) {
            throw new AccountLinkConflictException("One of the accounts no longer exists");
        }
        CustomerAccount target = lockedAccounts.get(targetCustomerId);
        CustomerAccount source = lockedAccounts.get(sourceCustomerId);
        validateIdentities(targetCustomerId, sourceCustomerId);

        String mergedPhone = mergePhone(target.getPhone(), source.getPhone());
        Instant earliestCreatedAt = target.getCreatedAt().isBefore(source.getCreatedAt())
                ? target.getCreatedAt()
                : source.getCreatedAt();
        target.mergeProfile(earliestCreatedAt, mergedPhone);
        entityManager.flush();

        mergeRoles(targetCustomerId, sourceCustomerId);
        mergeRentalAdminPreference(targetCustomerId, sourceCustomerId);
        mergeCustomerAcquisition(targetCustomerId, sourceCustomerId);
        reassignCustomerOwnership(targetCustomerId, sourceCustomerId);
        identityRepository.flush();
        accountRepository.delete(source);
        accountRepository.flush();
        return targetCustomerId;
    }

    private void validateIdentities(long targetCustomerId, long sourceCustomerId) {
        Map<ExternalIdentityProvider, String> target = identitiesByProvider(targetCustomerId);
        Map<ExternalIdentityProvider, String> source = identitiesByProvider(sourceCustomerId);
        source.forEach((provider, subject) -> {
            String targetSubject = target.get(provider);
            if (targetSubject != null && !Objects.equals(targetSubject, subject)) {
                throw new AccountLinkConflictException(
                        "Both accounts already have different " + provider + " identities"
                );
            }
        });
    }

    private Map<ExternalIdentityProvider, String> identitiesByProvider(long customerId) {
        Map<ExternalIdentityProvider, String> result = new EnumMap<>(ExternalIdentityProvider.class);
        identityRepository.findAllByCustomerIdOrderByProvider(customerId)
                .forEach(identity -> result.put(identity.getProvider(), identity.getExternalSubject()));
        return result;
    }

    private static String mergePhone(String targetPhone, String sourcePhone) {
        if (targetPhone == null) {
            return sourcePhone;
        }
        if (sourcePhone == null || targetPhone.equals(sourcePhone)) {
            return targetPhone;
        }
        throw new AccountLinkConflictException("Accounts have different phone numbers");
    }

    private void mergeRoles(long targetId, long sourceId) {
        jdbcTemplate.update("""
                insert into customer_role (customer_id, role, created_at)
                select ?, role, created_at from customer_role where customer_id = ?
                on conflict (customer_id, role) do nothing
                """, targetId, sourceId);
        jdbcTemplate.update("delete from customer_role where customer_id = ?", sourceId);
    }

    private void mergeRentalAdminPreference(long targetId, long sourceId) {
        jdbcTemplate.update("""
                insert into rental_admin_notification_preference (customer_id, telegram_enabled, updated_at)
                select ?, telegram_enabled, updated_at
                  from rental_admin_notification_preference
                 where customer_id = ?
                on conflict (customer_id) do update
                    set telegram_enabled = rental_admin_notification_preference.telegram_enabled
                                           or excluded.telegram_enabled,
                        updated_at = greatest(rental_admin_notification_preference.updated_at,
                                              excluded.updated_at)
                """, targetId, sourceId);
        jdbcTemplate.update(
                "delete from rental_admin_notification_preference where customer_id = ?",
                sourceId
        );
    }

    private void mergeCustomerAcquisition(long targetId, long sourceId) {
        jdbcTemplate.update("""
                insert into customer_acquisition(
                    customer_id, channel, campaign_id, first_touch_at,
                    first_touch_service, attribution_method, created_at
                )
                select ?, channel, campaign_id, first_touch_at,
                       first_touch_service, attribution_method, created_at
                  from customer_acquisition
                 where customer_id in (?, ?)
                 order by first_touch_at, case when customer_id = ? then 0 else 1 end
                 limit 1
                on conflict (customer_id) do update
                    set channel = excluded.channel,
                        campaign_id = excluded.campaign_id,
                        first_touch_at = excluded.first_touch_at,
                        first_touch_service = excluded.first_touch_service,
                        attribution_method = excluded.attribution_method,
                        created_at = excluded.created_at
                """, targetId, targetId, sourceId, targetId);
        jdbcTemplate.update("delete from customer_acquisition where customer_id = ?", sourceId);
    }

    private void reassignCustomerOwnership(long targetId, long sourceId) {
        jdbcTemplate.update("""
                delete from customer_notification source
                 where source.customer_id = ?
                   and exists (
                       select 1 from customer_notification target
                        where target.customer_id = ?
                          and target.dedup_key = source.dedup_key
                   )
                """, sourceId, targetId);
        jdbcTemplate.update(
                "update customer_notification set customer_id = ? where customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update(
                "update support_case set resolved_by_customer_id = ? where resolved_by_customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update(
                "update support_case set customer_id = ? where customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update(
                "update transaction_feedback set customer_id = ? where customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update("update cleaning_order set customer_id = ? where customer_id = ?", targetId, sourceId);
        jdbcTemplate.update(
                "update cleaning_order set referrer_customer_id = ? where referrer_customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update("update rental_booking set customer_id = ? where customer_id = ?", targetId, sourceId);
        jdbcTemplate.update("update transfer_booking set customer_id = ? where customer_id = ?", targetId, sourceId);
        jdbcTemplate.update(
                "update rental_cleaning_benefit set customer_id = ? where customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update("update referral_reward set customer_id = ? where customer_id = ?", targetId, sourceId);
        jdbcTemplate.update("update referral_code set customer_id = ? where customer_id = ?", targetId, sourceId);
        jdbcTemplate.update(
                "update platform_service_state set updated_by_customer_id = ? where updated_by_customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update(
                "update customer_identity_link_request set target_customer_id = ? where target_customer_id = ?",
                targetId,
                sourceId
        );
        jdbcTemplate.update(
                "update customer_external_identity set customer_id = ? where customer_id = ?",
                targetId,
                sourceId
        );
    }
}
