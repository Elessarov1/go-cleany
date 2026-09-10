package com.cleany.action;

import com.cleany.catalog.PlatformService;

public final class ActionTargetWebPathResolver {
    private ActionTargetWebPathResolver() {
    }

    public static String resolve(ActionTarget target) {
        return switch (target) {
            case ActionTarget.OpenTransaction action -> transactionPath(action.service(), action.entityId());
            case ActionTarget.OpenCleaningHistory ignored -> "/cleaning/orders";
            case ActionTarget.RepeatCleaning action -> "/cleaning?repeatFrom=" + action.sourceOrderId();
            case ActionTarget.RepeatTransfer action -> "/transfer?repeatFrom=" + action.sourceBookingId();
            case ActionTarget.StartRentalCleaning action -> "/cleaning?rentalBooking=" + action.rentalBookingId();
            case ActionTarget.StartRentalTransfer action -> "/transfer?rentalBooking="
                    + action.rentalBookingId() + "&rentalContext=" + action.context();
            case ActionTarget.OpenAdminTransaction action -> adminPath(action.service(), action.entityId());
            case ActionTarget.OpenSupportCase action -> "/admin/support/cases/" + action.caseId();
        };
    }

    private static String transactionPath(PlatformService service, long id) {
        return switch (service) {
            case CLEANING -> "/cleaning/orders/" + id;
            case RENTAL -> "/rent/bookings/" + id;
            case TRANSFER -> "/transfer/bookings/" + id;
        };
    }

    private static String adminPath(PlatformService service, long id) {
        return switch (service) {
            case CLEANING -> "/admin/cleaning/orders/" + id;
            case RENTAL -> "/admin/rent/bookings/" + id;
            case TRANSFER -> "/admin/transfer/bookings/" + id;
        };
    }
}
