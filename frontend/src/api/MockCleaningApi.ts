import type { CleaningConfiguration } from "../domain/configuration";
import type {
  AdminDashboard,
  AdminOrderDetails,
  AdminReferralOverview,
  PartnerPayout,
  ReferralPartner,
} from "../domain/admin";
import { calculateDisplayedPrice } from "../domain/pricing";
import type {
  CleaningOrder,
  CleaningOrderQuote,
  CleaningOrderQuoteRequest,
  CleaningOrderStatus,
  CreateCleaningOrderRequest,
  ReferralSummary,
} from "../domain/order";
import type { Platform } from "../platform/Platform";
import { CleaningApiError, type CleaningApi } from "./CleaningApi";

const STORAGE_KEY = "cleany.mock.orders.v2";

export const mockConfiguration: CleaningConfiguration = {
  areas: ["MAHMUTLAR", "KARGICAK", "KESTEL"],
  apartmentTypes: [
    { type: "STUDIO", regularPrice: 800, deepPrice: 1200 },
    { type: "ONE_PLUS_ONE", regularPrice: 900, deepPrice: 1400 },
    { type: "TWO_PLUS_ONE", regularPrice: 1100, deepPrice: 1700 },
    { type: "THREE_PLUS_ONE", regularPrice: 1350, deepPrice: 2050 },
    { type: "FOUR_PLUS_ONE", regularPrice: 1650, deepPrice: 2450 },
  ],
  duplexSurcharges: {
    REGULAR: 300,
    DEEP: 450,
  },
  bookingDaysAhead: 7,
  currency: "TRY",
};

const scenarioIds: Record<CleaningOrderStatus, number> = {
  NEW: 101,
  ACCEPTED: 102,
  AWAITING_REPORT: 103,
  COMPLETED: 104,
  REJECTED: 105,
  CANCELLED: 106,
};

function dateFromToday(offset: number): string {
  const date = new Date();
  date.setDate(date.getDate() + offset);
  return date.toISOString().slice(0, 10);
}

function createScenarioOrder(status: CleaningOrderStatus): CleaningOrder {
  const createdAt = new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString();
  const accepted = ["ACCEPTED", "AWAITING_REPORT", "COMPLETED"].includes(status);

  return {
    id: scenarioIds[status],
    telegramUserId: 900_001,
    telegramUsername: "browser_preview",
    customerName: "Alex",
    phone: "+90 555 123 45 67",
    area: "MAHMUTLAR",
    address: "Barbaros Cd., building 24, apartment 16",
    apartmentType: "TWO_PLUS_ONE",
    duplex: false,
    cleaningType: "REGULAR",
    price: 1100,
    basePrice: 1100,
    customerDiscount: 0,
    finalCustomerPrice: 1100,
    customerDiscountType: "NONE",
    currency: "TRY",
    requestedDate: dateFromToday(2),
    customerComment: "The key is with security.",
    cleanerComment:
      status === "COMPLETED" ? "Everything is ready. Thank you!" : undefined,
    cleanerTelegramUserId: accepted ? 123_456_789 : undefined,
    status,
    createdAt,
    acceptedAt: accepted ? new Date().toISOString() : undefined,
    completedAt: status === "COMPLETED" ? new Date().toISOString() : undefined,
    photoCount: status === "COMPLETED" ? 5 : undefined,
  };
}

type PreviewScenario = CleaningOrderStatus | "empty";

function getPreviewScenario(): PreviewScenario | null {
  if (!import.meta.env.DEV) {
    return null;
  }

  const search = new URLSearchParams(window.location.search);
  const previewEnabled =
    search.get("preview") === "true" ||
    sessionStorage.getItem("cleany.preview.enabled") === "true";

  if (!previewEnabled) {
    return null;
  }

  const scenario = (
    search.get("scenario") ?? sessionStorage.getItem("cleany.preview.scenario")
  )?.toUpperCase();

  const validStatuses: CleaningOrderStatus[] = [
    "NEW",
    "ACCEPTED",
    "AWAITING_REPORT",
    "COMPLETED",
    "REJECTED",
    "CANCELLED",
  ];

  if (scenario === "EMPTY") {
    return "empty";
  }

  return validStatuses.includes(scenario as CleaningOrderStatus)
    ? (scenario as CleaningOrderStatus)
    : null;
}

function readStoredOrders(): CleaningOrder[] {
  try {
    const value = localStorage.getItem(STORAGE_KEY);
    return value ? (JSON.parse(value) as CleaningOrder[]) : [];
  } catch {
    return [];
  }
}

function writeStoredOrders(orders: CleaningOrder[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(orders));
}

async function simulateNetwork<T>(value: T): Promise<T> {
  await new Promise((resolve) => window.setTimeout(resolve, 180));
  return value;
}

export class MockCleaningApi implements CleaningApi {
  constructor(private readonly platform: Platform) {}

  hasAdminAccess(): Promise<boolean> {
    return simulateNetwork(true);
  }

  async getAdminDashboard(limit = 100): Promise<AdminDashboard> {
    const orders = await this.getOrders();
    const today = new Date().toISOString().slice(0, 10);
    const completed = orders.filter((order) => order.status === "COMPLETED");
    return simulateNetwork({
      stats: {
        totalOrders: orders.length,
        ordersToday: orders.filter((order) => order.createdAt.slice(0, 10) === today).length,
        newOrders: orders.filter((order) => order.status === "NEW").length,
        activeOrders: orders.filter((order) =>
          order.status === "ACCEPTED" || order.status === "AWAITING_REPORT"
        ).length,
        completedOrders: completed.length,
        cancelledOrders: orders.filter((order) => order.status === "CANCELLED").length,
        completedAmount: completed.reduce((sum, order) => sum + order.price, 0),
        currency: mockConfiguration.currency,
      },
      recentOrders: orders.slice(0, limit),
    });
  }

  async getAdminOrder(id: number): Promise<AdminOrderDetails> {
    const order = await this.getOrder(id);
    const details: AdminOrderDetails = {
      order,
      financial: {
        basePrice: order.basePrice,
        commissionRate: 0.15,
        baseCommission: order.basePrice * 0.15,
        customerDiscount: order.customerDiscount,
        partnerPayout: 0,
        finalCustomerPrice: order.finalCustomerPrice,
        platformNet: order.basePrice * 0.15 - order.customerDiscount,
        acquisitionSource: "ORGANIC",
        customerDiscountType: order.customerDiscountType,
      },
      photoCount: order.photoCount ?? 0,
      events: [
        {
          id: 1,
          eventType: "CREATED",
          toStatus: "NEW",
          actorType: "CUSTOMER",
          actorTelegramUserId: order.telegramUserId,
          occurredAt: order.createdAt,
        },
        ...(order.acceptedAt ? [{
          id: 2,
          eventType: "ACCEPTED" as const,
          fromStatus: "NEW" as const,
          toStatus: "ACCEPTED" as const,
          actorType: "CLEANER" as const,
          actorTelegramUserId: order.cleanerTelegramUserId,
          occurredAt: order.acceptedAt,
        }] : []),
        ...(order.completedAt ? [{
          id: 3,
          eventType: "COMPLETED" as const,
          fromStatus: "AWAITING_REPORT" as const,
          toStatus: "COMPLETED" as const,
          actorType: "CLEANER" as const,
          actorTelegramUserId: order.cleanerTelegramUserId,
          occurredAt: order.completedAt,
        }] : []),
      ],
    };
    return simulateNetwork(details);
  }

  getAdminReferralOverview(): Promise<AdminReferralOverview> {
    return simulateNetwork({ partners: [], payouts: [] });
  }

  createReferralPartner(name: string): Promise<ReferralPartner> {
    return simulateNetwork({
      id: Date.now(),
      name,
      referralCode: "GCPREVIEW1",
      active: true,
      createdAt: new Date().toISOString(),
    });
  }

  markPartnerPayoutPaid(id: number): Promise<PartnerPayout> {
    return Promise.reject(new CleaningApiError(`Payout ${id} not found`, 404));
  }

  getConfiguration(): Promise<CleaningConfiguration> {
    return simulateNetwork(mockConfiguration);
  }

  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote> {
    const basePrice = calculateDisplayedPrice(
      mockConfiguration,
      request.apartmentType,
      request.cleaningType,
      request.duplex,
    );
    const customerDiscount = request.referralCode ? basePrice * 0.15 : 0;
    return simulateNetwork({
      basePrice,
      customerDiscount,
      finalCustomerPrice: basePrice - customerDiscount,
      customerDiscountType: request.referralCode ? "FRIEND_REFERRAL" : "NONE",
      currency: mockConfiguration.currency,
    });
  }

  async createOrder(
    request: CreateCleaningOrderRequest,
  ): Promise<CleaningOrder> {
    const user = this.platform.getUser();
    if (!user) {
      throw new CleaningApiError("Mock user is unavailable", 401);
    }
    const quote = await this.quoteOrder(request);

    const order: CleaningOrder = {
      id: Date.now(),
      telegramUserId: user.id,
      telegramUsername: user.username,
      customerName: [user.firstName, user.lastName].filter(Boolean).join(" "),
      phone: request.phone,
      area: request.area,
      address: request.address,
      apartmentType: request.apartmentType,
      duplex: request.duplex,
      cleaningType: request.cleaningType,
      price: quote.finalCustomerPrice,
      basePrice: quote.basePrice,
      customerDiscount: quote.customerDiscount,
      finalCustomerPrice: quote.finalCustomerPrice,
      customerDiscountType: quote.customerDiscountType,
      currency: mockConfiguration.currency,
      requestedDate: request.requestedDate,
      customerComment: request.comment?.trim() || undefined,
      status: "NEW",
      createdAt: new Date().toISOString(),
    };

    writeStoredOrders([order, ...readStoredOrders()]);
    return simulateNetwork(order);
  }

  getReferralSummary(): Promise<ReferralSummary> {
    return simulateNetwork({
      referralCode: "GCPREVIEW1",
      availableRewards: 1,
      referralProgramUnlocked: true,
    });
  }

  getOrders(): Promise<CleaningOrder[]> {
    const scenario = getPreviewScenario();
    const orders = scenario === "empty"
      ? []
      : scenario
      ? [createScenarioOrder(scenario)]
      : readStoredOrders();
    return simulateNetwork(orders);
  }

  async getOrder(id: number): Promise<CleaningOrder> {
    const scenario = getPreviewScenario();
    const order = scenario && scenario !== "empty"
      ? createScenarioOrder(scenario)
      : readStoredOrders().find((item) => item.id === id);

    if (!order || order.id !== id) {
      throw new CleaningApiError("Order not found", 404);
    }

    return simulateNetwork(order);
  }

  async cancelOrder(id: number): Promise<CleaningOrder> {
    const scenario = getPreviewScenario();
    if (scenario && scenario !== "empty") {
      const scenarioOrder = createScenarioOrder(scenario);
      if (scenarioOrder.id === id && scenarioOrder.status === "NEW") {
        return simulateNetwork({ ...scenarioOrder, status: "CANCELLED" });
      }
    }

    const orders = readStoredOrders();
    const order = orders.find((item) => item.id === id);

    if (!order) {
      throw new CleaningApiError("Order not found", 404);
    }

    if (order.status !== "NEW") {
      throw new CleaningApiError("Order can no longer be cancelled", 409);
    }

    const cancelledOrder: CleaningOrder = { ...order, status: "CANCELLED" };
    writeStoredOrders(
      orders.map((item) => (item.id === id ? cancelledOrder : item)),
    );
    return simulateNetwork(cancelledOrder);
  }
}

export function getPreviewOrderId(status: CleaningOrderStatus): number {
  return scenarioIds[status];
}
