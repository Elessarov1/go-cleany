import type { PlatformService } from "../domain/platformService";
import type {
  AdminSupportCaseDetails,
  AdminSupportCasePage,
  CreateSupportCaseRequest,
  CreateTransactionFeedbackRequest,
  SupportCase,
  TransactionFeedback,
  TransactionSupport,
} from "../domain/support";
import type { SupportApi } from "./SupportApi";
import { ApiError } from "./ApiError";

type CompletionResolver = (service: PlatformService, sourceEntityId: number) => Promise<boolean>;

export class MockSupportApi implements SupportApi {
  private cases: SupportCase[] = [{
    id: 701,
    service: "RENTAL",
    sourceEntityId: 501,
    category: "BOOKING_PROBLEM",
    status: "OPEN",
    description: "Please confirm the check-in instructions.",
    createdAt: new Date(Date.now() - 75 * 60_000).toISOString(),
    resolvedAt: null,
    resolutionComment: null,
  }];
  private feedback = new Map<string, TransactionFeedback>();

  constructor(private readonly completionResolver: CompletionResolver = async () => true) {}

  async getTransactionSupport(service: PlatformService, sourceEntityId: number): Promise<TransactionSupport> {
    return this.state(service, sourceEntityId);
  }

  async createCase(request: CreateSupportCaseRequest): Promise<SupportCase> {
    const existing = this.cases.find((item) => item.service === request.service
      && item.sourceEntityId === request.sourceEntityId && item.status === "OPEN");
    if (existing) return existing;
    const supportCase: SupportCase = {
      id: this.cases.length + 101,
      service: request.service,
      sourceEntityId: request.sourceEntityId,
      category: request.category,
      status: "OPEN",
      description: request.description,
      createdAt: new Date().toISOString(),
      resolvedAt: null,
      resolutionComment: null,
    };
    this.cases.unshift(supportCase);
    return supportCase;
  }

  async submitFeedback(request: CreateTransactionFeedbackRequest): Promise<TransactionSupport> {
    if (!await this.completionResolver(request.service, request.sourceEntityId)) {
      throw new ApiError("Feedback is available only for completed transactions", 400);
    }
    const key = this.key(request.service, request.sourceEntityId);
    if (!this.feedback.has(key)) {
      const supportCase = request.outcome === "PROBLEM"
        ? await this.createCase({
            service: request.service,
            sourceEntityId: request.sourceEntityId,
            category: request.category!,
            description: request.comment,
          })
        : null;
      this.feedback.set(key, {
        id: this.feedback.size + 1,
        outcome: request.outcome,
        category: request.category,
        comment: request.comment,
        supportCaseId: supportCase?.id ?? null,
        createdAt: new Date().toISOString(),
      });
    }
    return this.state(request.service, request.sourceEntityId);
  }

  async getAdminCases(filters: Parameters<SupportApi["getAdminCases"]>[0]): Promise<AdminSupportCasePage> {
    const filtered = this.cases.filter((item) => (filters.status === "ALL" || item.status === filters.status)
      && (filters.service === "ALL" || item.service === filters.service));
    const page = filters.page ?? 0;
    const size = filters.size ?? 20;
    return {
      content: filtered.slice(page * size, (page + 1) * size).map((item) => ({
        ...item,
        customerId: 77,
        customerName: "Preview customer",
        customerPhone: "+90 555 123 45 67",
        sourceAdminPath: this.adminPath(item.service, item.sourceEntityId),
      })),
      page,
      size,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
    };
  }

  async getAdminCase(caseId: number): Promise<AdminSupportCaseDetails> {
    const supportCase = this.cases.find((item) => item.id === caseId) ?? await this.createCase({
      service: "CLEANING",
      sourceEntityId: 12,
      category: "QUALITY_PROBLEM",
      description: "The result needs attention",
    });
    return {
      summary: {
        ...supportCase,
        customerId: 77,
        customerName: "Preview customer",
        customerPhone: "+90 555 123 45 67",
        sourceAdminPath: this.adminPath(supportCase.service, supportCase.sourceEntityId),
      },
      description: supportCase.description,
      resolvedByCustomerId: supportCase.status === "RESOLVED" ? 1 : null,
      resolutionComment: supportCase.resolutionComment,
      resolvedAt: supportCase.resolvedAt,
      sourceCustomerPath: this.customerPath(supportCase.service, supportCase.sourceEntityId),
    };
  }

  async resolveAdminCase(caseId: number, resolutionComment: string): Promise<AdminSupportCaseDetails> {
    const index = this.cases.findIndex((item) => item.id === caseId);
    if (index >= 0) {
      const current = this.cases[index];
      if (!current) return this.getAdminCase(caseId);
      this.cases[index] = {
        ...current,
        status: "RESOLVED",
        resolvedAt: new Date().toISOString(),
        resolutionComment,
      };
    }
    return this.getAdminCase(caseId);
  }

  private async state(service: PlatformService, sourceEntityId: number): Promise<TransactionSupport> {
    const key = this.key(service, sourceEntityId);
    const completed = await this.completionResolver(service, sourceEntityId);
    return {
      service,
      sourceEntityId,
      feedbackEligible: completed && !this.feedback.has(key),
      feedback: this.feedback.get(key) ?? null,
      latestCase: this.cases.find((item) => item.service === service && item.sourceEntityId === sourceEntityId) ?? null,
    };
  }

  private key(service: PlatformService, sourceEntityId: number) {
    return `${service}:${sourceEntityId}`;
  }

  private adminPath(service: PlatformService, id: number) {
    return service === "CLEANING" ? `/admin/cleaning/orders/${id}`
      : service === "RENTAL" ? `/admin/rent/bookings/${id}` : `/admin/transfer/bookings/${id}`;
  }

  private customerPath(service: PlatformService, id: number) {
    return service === "CLEANING" ? `/cleaning/orders/${id}`
      : service === "RENTAL" ? `/rent/bookings/${id}` : `/transfer/bookings/${id}`;
  }
}
