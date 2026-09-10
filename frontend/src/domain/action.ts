import type { PlatformService } from "./platformService";
import { ActionTargetFromJSON } from "@locoplace/api-client";

export type ActionTarget =
  | { type: "OPEN_TRANSACTION"; service: PlatformService; entityId: number }
  | { type: "OPEN_CLEANING_HISTORY" }
  | { type: "REPEAT_CLEANING"; sourceOrderId: number }
  | { type: "REPEAT_TRANSFER"; sourceBookingId: number }
  | { type: "START_RENTAL_CLEANING"; rentalBookingId: number }
  | { type: "START_RENTAL_TRANSFER"; rentalBookingId: number; context: "ARRIVAL" | "CHECKOUT" }
  | { type: "OPEN_ADMIN_TRANSACTION"; service: PlatformService; entityId: number }
  | { type: "OPEN_SUPPORT_CASE"; caseId: number }
  | { type: "UNKNOWN"; rawType?: string };

export function parseActionTarget(value: unknown): ActionTarget {
  const parsed = ActionTargetFromJSON(value) as unknown as Record<string, unknown>;
  const type = typeof parsed?.type === "string" ? parsed.type : "";
  if ([
    "OPEN_TRANSACTION", "OPEN_CLEANING_HISTORY", "REPEAT_CLEANING", "REPEAT_TRANSFER",
    "START_RENTAL_CLEANING", "START_RENTAL_TRANSFER", "OPEN_ADMIN_TRANSACTION",
    "OPEN_SUPPORT_CASE",
  ].includes(type)) return parsed as unknown as ActionTarget;
  return { type: "UNKNOWN", rawType: type || undefined };
}

export function actionTargetPath(action: ActionTarget): string {
  switch (action.type) {
    case "OPEN_TRANSACTION": return transactionPath(action.service, action.entityId);
    case "OPEN_CLEANING_HISTORY": return "/cleaning/orders";
    case "REPEAT_CLEANING": return `/cleaning?repeatFrom=${action.sourceOrderId}`;
    case "REPEAT_TRANSFER": return `/transfer?repeatFrom=${action.sourceBookingId}`;
    case "START_RENTAL_CLEANING": return `/cleaning?rentalBooking=${action.rentalBookingId}`;
    case "START_RENTAL_TRANSFER":
      return `/transfer?rentalBooking=${action.rentalBookingId}&rentalContext=${action.context}`;
    case "OPEN_ADMIN_TRANSACTION":
      return action.service === "CLEANING"
        ? `/admin/cleaning/orders/${action.entityId}`
        : action.service === "RENTAL"
          ? `/admin/rent/bookings/${action.entityId}`
          : `/admin/transfer/bookings/${action.entityId}`;
    case "OPEN_SUPPORT_CASE": return `/admin/support/cases/${action.caseId}`;
    case "UNKNOWN": return "/";
  }
}

function transactionPath(service: PlatformService, id: number): string {
  if (service === "CLEANING") return `/cleaning/orders/${id}`;
  if (service === "RENTAL") return `/rent/bookings/${id}`;
  return `/transfer/bookings/${id}`;
}
