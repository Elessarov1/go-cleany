package com.cleany.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionTarget.OpenTransaction.class, name = "OPEN_TRANSACTION"),
        @JsonSubTypes.Type(value = ActionTarget.OpenCleaningHistory.class, name = "OPEN_CLEANING_HISTORY"),
        @JsonSubTypes.Type(value = ActionTarget.RepeatCleaning.class, name = "REPEAT_CLEANING"),
        @JsonSubTypes.Type(value = ActionTarget.RepeatTransfer.class, name = "REPEAT_TRANSFER"),
        @JsonSubTypes.Type(value = ActionTarget.StartRentalCleaning.class, name = "START_RENTAL_CLEANING"),
        @JsonSubTypes.Type(value = ActionTarget.StartRentalTransfer.class, name = "START_RENTAL_TRANSFER"),
        @JsonSubTypes.Type(value = ActionTarget.OpenAdminTransaction.class, name = "OPEN_ADMIN_TRANSACTION"),
        @JsonSubTypes.Type(value = ActionTarget.OpenSupportCase.class, name = "OPEN_SUPPORT_CASE")
})
public sealed interface ActionTarget permits ActionTarget.OpenTransaction,
        ActionTarget.OpenCleaningHistory, ActionTarget.RepeatCleaning,
        ActionTarget.RepeatTransfer, ActionTarget.StartRentalCleaning,
        ActionTarget.StartRentalTransfer, ActionTarget.OpenAdminTransaction,
        ActionTarget.OpenSupportCase {

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    ActionType type();

    record OpenTransaction(PlatformService service, long entityId) implements ActionTarget {
        public OpenTransaction {
            if (service == null) throw new IllegalArgumentException("service must not be null");
            if (entityId <= 0) throw new IllegalArgumentException("entityId must be positive");
        }
        @Override public ActionType type() { return ActionType.OPEN_TRANSACTION; }
    }

    record OpenCleaningHistory() implements ActionTarget {
        @Override public ActionType type() { return ActionType.OPEN_CLEANING_HISTORY; }
    }

    record RepeatCleaning(long sourceOrderId) implements ActionTarget {
        public RepeatCleaning {
            if (sourceOrderId <= 0) throw new IllegalArgumentException("sourceOrderId must be positive");
        }
        @Override public ActionType type() { return ActionType.REPEAT_CLEANING; }
    }

    record RepeatTransfer(long sourceBookingId) implements ActionTarget {
        public RepeatTransfer {
            if (sourceBookingId <= 0) throw new IllegalArgumentException("sourceBookingId must be positive");
        }
        @Override public ActionType type() { return ActionType.REPEAT_TRANSFER; }
    }

    record StartRentalCleaning(long rentalBookingId) implements ActionTarget {
        public StartRentalCleaning {
            if (rentalBookingId <= 0) throw new IllegalArgumentException("rentalBookingId must be positive");
        }
        @Override public ActionType type() { return ActionType.START_RENTAL_CLEANING; }
    }

    record StartRentalTransfer(long rentalBookingId, RentalTransferContextType context)
            implements ActionTarget {
        public StartRentalTransfer {
            if (rentalBookingId <= 0) throw new IllegalArgumentException("rentalBookingId must be positive");
            if (context == null) throw new IllegalArgumentException("context must not be null");
        }
        @Override public ActionType type() { return ActionType.START_RENTAL_TRANSFER; }
    }

    record OpenAdminTransaction(PlatformService service, long entityId) implements ActionTarget {
        public OpenAdminTransaction {
            if (service == null) throw new IllegalArgumentException("service must not be null");
            if (entityId <= 0) throw new IllegalArgumentException("entityId must be positive");
        }
        @Override public ActionType type() { return ActionType.OPEN_ADMIN_TRANSACTION; }
    }

    record OpenSupportCase(long caseId) implements ActionTarget {
        public OpenSupportCase {
            if (caseId <= 0) throw new IllegalArgumentException("caseId must be positive");
        }
        @Override public ActionType type() { return ActionType.OPEN_SUPPORT_CASE; }
    }
}
