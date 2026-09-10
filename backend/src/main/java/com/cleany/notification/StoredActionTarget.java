package com.cleany.notification;

import com.cleany.action.ActionTarget;
import com.cleany.action.ActionType;
import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;

record StoredActionTarget(
        ActionType type,
        PlatformService service,
        Long entityId,
        String context
) {
    static StoredActionTarget from(ActionTarget target) {
        return switch (target) {
            case ActionTarget.OpenTransaction action ->
                    new StoredActionTarget(target.type(), action.service(), action.entityId(), null);
            case ActionTarget.OpenCleaningHistory ignored ->
                    new StoredActionTarget(target.type(), PlatformService.CLEANING, null, null);
            case ActionTarget.RepeatCleaning action ->
                    new StoredActionTarget(target.type(), PlatformService.CLEANING, action.sourceOrderId(), null);
            case ActionTarget.RepeatTransfer action ->
                    new StoredActionTarget(target.type(), PlatformService.TRANSFER, action.sourceBookingId(), null);
            case ActionTarget.StartRentalCleaning action ->
                    new StoredActionTarget(target.type(), PlatformService.RENTAL, action.rentalBookingId(), null);
            case ActionTarget.StartRentalTransfer action ->
                    new StoredActionTarget(target.type(), PlatformService.RENTAL,
                            action.rentalBookingId(), action.context().name());
            case ActionTarget.OpenAdminTransaction action ->
                    new StoredActionTarget(target.type(), action.service(), action.entityId(), null);
            case ActionTarget.OpenSupportCase action ->
                    new StoredActionTarget(target.type(), null, action.caseId(), null);
        };
    }

    ActionTarget toAction() {
        return switch (type) {
            case OPEN_TRANSACTION -> new ActionTarget.OpenTransaction(requiredService(), requiredId());
            case OPEN_CLEANING_HISTORY -> new ActionTarget.OpenCleaningHistory();
            case REPEAT_CLEANING -> new ActionTarget.RepeatCleaning(requiredId());
            case REPEAT_TRANSFER -> new ActionTarget.RepeatTransfer(requiredId());
            case START_RENTAL_CLEANING -> new ActionTarget.StartRentalCleaning(requiredId());
            case START_RENTAL_TRANSFER -> new ActionTarget.StartRentalTransfer(
                    requiredId(), RentalTransferContextType.valueOf(requiredContext()));
            case OPEN_ADMIN_TRANSACTION -> new ActionTarget.OpenAdminTransaction(requiredService(), requiredId());
            case OPEN_SUPPORT_CASE -> new ActionTarget.OpenSupportCase(requiredId());
        };
    }

    private long requiredId() {
        if (entityId == null || entityId <= 0) throw new IllegalStateException("Stored action has no entity id");
        return entityId;
    }

    private PlatformService requiredService() {
        if (service == null) throw new IllegalStateException("Stored action has no service");
        return service;
    }

    private String requiredContext() {
        if (context == null || context.isBlank()) throw new IllegalStateException("Stored action has no context");
        return context;
    }
}
