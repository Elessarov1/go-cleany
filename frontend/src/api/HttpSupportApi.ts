import type { PlatformService } from "../domain/platformService";
import type {
  AdminSupportCaseDetails,
  AdminSupportCasePage,
  CreateSupportCaseRequest,
  CreateTransactionFeedbackRequest,
  SupportCase,
  TransactionSupport,
} from "../domain/support";
import type { SupportApi } from "./SupportApi";
import { HttpApiClient } from "./HttpApiClient";

export class HttpSupportApi implements SupportApi {
  constructor(private readonly client: HttpApiClient) {}

  getTransactionSupport(service: PlatformService, sourceEntityId: number): Promise<TransactionSupport> {
    return this.client.request(`/api/v1/account/support/sources/${service}/${sourceEntityId}`);
  }

  createCase(request: CreateSupportCaseRequest): Promise<SupportCase> {
    return this.client.request("/api/v1/account/support/cases", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  submitFeedback(request: CreateTransactionFeedbackRequest): Promise<TransactionSupport> {
    return this.client.request("/api/v1/account/support/feedback", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  getAdminCases(filters: Parameters<SupportApi["getAdminCases"]>[0]): Promise<AdminSupportCasePage> {
    const query = new URLSearchParams({
      status: filters.status,
      service: filters.service,
      page: String(filters.page ?? 0),
      size: String(filters.size ?? 20),
    });
    return this.client.request(`/api/v1/admin/support/cases?${query.toString()}`);
  }

  getAdminCase(caseId: number): Promise<AdminSupportCaseDetails> {
    return this.client.request(`/api/v1/admin/support/cases/${caseId}`);
  }

  resolveAdminCase(caseId: number, resolutionComment: string): Promise<AdminSupportCaseDetails> {
    return this.client.request(`/api/v1/admin/support/cases/${caseId}/resolve`, {
      method: "POST",
      body: JSON.stringify({ resolutionComment }),
    });
  }
}
