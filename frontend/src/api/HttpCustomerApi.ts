import type { AccountDeletionChallenge, AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerHome, CustomerNotificationPage, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";
import { HttpApiClient } from "./HttpApiClient";
import { parseActionTarget } from "../domain/action";
import { AccountApi, ClientApi, Configuration } from "@locoplace/api-client";

export class HttpCustomerApi implements CustomerApi {
  private readonly generated: AccountApi;
  private readonly generatedClient: ClientApi;

  constructor(private readonly client: HttpApiClient) {
    const configuration = new Configuration({
      basePath: client.resolveUrl(""),
      credentials: "include",
      fetchApi: client.generatedFetch,
    });
    this.generated = new AccountApi(configuration);
    this.generatedClient = new ClientApi(configuration);
  }

  captureTelegramAcquisition(publicCode: string): Promise<{ targetPath: string }> {
    return this.client.generated(this.generatedClient.captureTelegramAcquisition({
      telegramAcquisitionRequest: { publicCode },
    }));
  }

  getCurrentProfile(): Promise<CustomerProfile> {
    return this.client.generated(this.generated.getCustomerProfile());
  }

  async getActivity(): Promise<CustomerActivity> {
    const result = await this.client.generated(this.generated.getCustomerActivity());
    return {
      activeAndUpcoming: result.activeAndUpcoming.map(parseGeneratedActivityItem),
      history: result.history.map(parseGeneratedActivityItem),
    };
  }

  async getHome(): Promise<CustomerHome> {
    const result = await this.client.generated(this.generated.getCustomerHome());
    return {
      hasActivity: result.hasActivity,
      activeTransactionCount: result.activeTransactionCount,
      activeTransaction: result.activeTransaction
        ? parseGeneratedActivityItem(result.activeTransaction)
        : null,
      primaryAction: result.primaryAction
        ? {
            ...result.primaryAction,
            type: result.primaryAction.type as NonNullable<CustomerHome["primaryAction"]>["type"],
            sourceService: result.primaryAction.sourceService as NonNullable<CustomerHome["primaryAction"]>["sourceService"],
            targetService: result.primaryAction.targetService as NonNullable<CustomerHome["primaryAction"]>["targetService"],
            relevantDate: isoDate(result.primaryAction.relevantDate),
            eligibleFrom: result.primaryAction.eligibleFrom ? isoDate(result.primaryAction.eligibleFrom) : null,
            expiresOn: result.primaryAction.expiresOn ? isoDate(result.primaryAction.expiresOn) : null,
            benefit: result.primaryAction.benefit
              ? { type: "RENTAL_FIRST_TRANSFER", discountRate: result.primaryAction.benefit.discountRate }
              : null,
            action: parseActionTarget(result.primaryAction.action),
          }
        : null,
      repeatOpportunity: result.repeatOpportunity
        ? {
            ...result.repeatOpportunity,
            service: result.repeatOpportunity.service as NonNullable<CustomerHome["repeatOpportunity"]>["service"],
            sourceCompletedAt: result.repeatOpportunity.sourceCompletedAt.toISOString(),
            action: parseActionTarget(result.repeatOpportunity.action),
          }
        : null,
    };
  }

  getAccountIdentities(): Promise<AccountIdentities> {
    return this.client.generated(this.generated.getAccountIdentities()) as Promise<AccountIdentities>;
  }

  async initiateTelegramLink(): Promise<AccountLinkInitiated> {
    const result = await this.client.generated(this.generated.createIdentityLink({
      createIdentityLinkRequest: { provider: "TELEGRAM" },
    }));
    if (!result.telegramDeepLink) throw new Error("Telegram link is unavailable");
    return { id: result.id, deepLink: result.telegramDeepLink, expiresAt: result.expiresAt.toISOString() };
  }

  async confirmTelegramLink(attemptId: string): Promise<AccountIdentities> {
    return this.client.generated(this.generated.confirmIdentityLink({
      id: attemptId,
      confirmIdentityLinkRequest: {},
    })) as Promise<AccountIdentities>;
  }

  async createAccountDeletionRequest(): Promise<AccountDeletionChallenge> {
    const result = await this.client.generated(this.generated.createAccountDeletionRequest());
    return {
      id: result.id,
      provider: result.provider as AccountDeletionChallenge["provider"],
      nonce: result.nonce ?? null,
      expiresAt: result.expiresAt.toISOString(),
    };
  }

  async confirmAccountDeletion(challengeId: string, telegramInitData?: string | null): Promise<void> {
    await this.client.generated(this.generated.confirmAccountDeletion({
      id: challengeId,
      sensitiveProof: {
        challengeId,
        telegramInitData: telegramInitData ?? undefined,
      },
    }));
  }

  async getNotifications(cursor: string | null = null, size = 20): Promise<CustomerNotificationPage> {
    const result = await this.client.generated(this.generated.getNotifications({
      cursor: cursor ?? undefined,
      size,
    }));
    return {
      ...result,
      nextCursor: result.nextCursor ?? null,
      items: result.items.map((item) => ({
        ...item,
        type: item.type as CustomerNotificationPage["items"][number]["type"],
        createdAt: item.createdAt.toISOString(),
        readAt: item.readAt?.toISOString() ?? null,
        action: parseActionTarget(item.action),
      })),
    };
  }

  async getNotificationUnreadCount(): Promise<number> {
    const response = await this.client.generated(this.generated.getNotificationUnreadCount());
    return response.unreadCount;
  }

  async markNotificationRead(notificationId: number): Promise<void> {
    await this.client.generated(this.generated.markNotificationRead({ notificationId }));
  }

  async markAllNotificationsRead(): Promise<void> {
    await this.client.generated(this.generated.markAllNotificationsRead());
  }
}

function isoDate(value: Date): string {
  return value.toISOString().slice(0, 10);
}

function parseGeneratedActivityItem(
  item: Awaited<ReturnType<AccountApi["getCustomerActivity"]>>["activeAndUpcoming"][number],
): CustomerActivity["activeAndUpcoming"][number] {
  return {
    ...item,
    service: item.service as CustomerActivity["activeAndUpcoming"][number]["service"],
    scheduledDate: isoDate(item.scheduledDate),
    scheduledEndDate: item.scheduledEndDate ? isoDate(item.scheduledEndDate) : null,
    scheduledTime: item.scheduledTime ?? null,
    occurredAt: item.occurredAt.toISOString(),
    action: parseActionTarget(item.action),
  };
}
