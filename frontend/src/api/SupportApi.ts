import type { PlatformService } from "../domain/platformService";
import type {
  AdminSupportCaseDetails,
  AdminSupportCasePage,
  CreateSupportCaseRequest,
  CreateTransactionFeedbackRequest,
  SupportCase,
  SupportCaseStatus,
  TransactionSupport,
} from "../domain/support";

export interface SupportApi {
  getTransactionSupport(service: PlatformService, sourceEntityId: number): Promise<TransactionSupport>;
  createCase(request: CreateSupportCaseRequest): Promise<SupportCase>;
  submitFeedback(request: CreateTransactionFeedbackRequest): Promise<TransactionSupport>;
  getAdminCases(filters: {
    status: SupportCaseStatus | "ALL";
    service: PlatformService | "ALL";
    page?: number;
    size?: number;
  }): Promise<AdminSupportCasePage>;
  getAdminCase(caseId: number): Promise<AdminSupportCaseDetails>;
  resolveAdminCase(caseId: number, resolutionComment: string): Promise<AdminSupportCaseDetails>;
}
