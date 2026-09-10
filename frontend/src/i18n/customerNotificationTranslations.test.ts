import { describe, expect, it } from "vitest";
import { CUSTOMER_NOTIFICATION_TYPES } from "../domain/customer";
import en from "./en.json";
import ru from "./ru.json";

describe("customer notification translations", () => {
  it.each(CUSTOMER_NOTIFICATION_TYPES)("has RU and EN copy for %s", (type) => {
    expect(ru.notifications.types[type]).toBeTruthy();
    expect(en.notifications.types[type]).toBeTruthy();
  });

  it("has RU and EN fallback copy for unknown future types", () => {
    expect(ru.notifications.types.UNKNOWN).toBeTruthy();
    expect(en.notifications.types.UNKNOWN).toBeTruthy();
  });
});
