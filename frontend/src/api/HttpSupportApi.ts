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
import { generatedWire, HttpApiClient } from "./HttpApiClient";
import {
  Configuration,
  CreateSupportCaseRequestFromJSON,
  CreateTransactionFeedbackRequestFromJSON,
  SupportApi as GeneratedSupportApi,
  SupportCaseToJSON,
  TransactionSupportToJSON,
} from "@locoplace/api-client";

export class HttpSupportApi implements SupportApi {
  private readonly generated: GeneratedSupportApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new GeneratedSupportApi(new Configuration({
      basePath: client.basePath,
      fetchApi: client.generatedFetch,
    }));
  }

  getTransactionSupport(service: PlatformService, sourceEntityId: number): Promise<TransactionSupport> {
    return this.client.generated(this.generated.getTransactionSupport({
      service: service as Parameters<GeneratedSupportApi["getTransactionSupport"]>[0]["service"],
      sourceEntityId,
    })).then((value) => generatedWire<TransactionSupport>(TransactionSupportToJSON(value)));
  }

  createCase(request: CreateSupportCaseRequest): Promise<SupportCase> {
    return this.client.generated(this.generated.createSupportCase({
      createSupportCaseRequest: CreateSupportCaseRequestFromJSON(request),
    })).then((value) => generatedWire<SupportCase>(SupportCaseToJSON(value)));
  }

  submitFeedback(request: CreateTransactionFeedbackRequest): Promise<TransactionSupport> {
    return this.client.generated(this.generated.submitTransactionFeedback({
      createTransactionFeedbackRequest: CreateTransactionFeedbackRequestFromJSON(request),
    })).then((value) => generatedWire<TransactionSupport>(TransactionSupportToJSON(value)));
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
