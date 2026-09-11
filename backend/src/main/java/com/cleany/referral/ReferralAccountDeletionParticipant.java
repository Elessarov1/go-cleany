package com.cleany.referral;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cleany.customer.AccountDeletionParticipant;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ReferralAccountDeletionParticipant implements AccountDeletionParticipant {

    private final CleaningOrderRepository orderRepository;
    private final ReferralEligibilityService eligibilityService;
    private final ReferralCodeRepository codeRepository;
    private final ReferralRewardRepository rewardRepository;
    private final Clock clock;

    @Override
    public void verifyCancellable(long customerId) {
        // Referral state cannot block deletion. Vertical participants own cancellation gates.
    }

    @Override
    public void cancel(long customerId) {
        // Referral cleanup must run only after every vertical has released cancellable work.
    }

    @Override
    public void afterCancellation(long customerId) {
        if (orderRepository.existsByCustomerIdAndStatus(customerId, CleaningOrderStatus.COMPLETED)) {
            eligibilityService.markCompletedCustomerIdentities(customerId);
        }
        codeRepository.findAllByCustomerIdAndActiveTrueOrderByCreatedAtAsc(customerId)
                .forEach(ReferralCode::deactivate);
        rewardRepository.findAllRevocableByCustomerIdForUpdate(
                customerId,
                List.of(ReferralRewardStatus.AVAILABLE, ReferralRewardStatus.RESERVED)
        ).forEach(reward -> reward.revoke(clock.instant()));
    }
}
