import type { PlatformService } from "./platformService";

export type SupportCaseCategory =
  | "PROVIDER_LATE"
  | "PROVIDER_NO_SHOW"
  | "QUALITY_PROBLEM"
  | "BOOKING_PROBLEM"
  | "OTHER";

export type SupportCaseStatus = "OPEN" | "RESOLVED";
export type FeedbackOutcome = "GOOD" | "PROBLEM";

export interface SupportCase {
  id: number;
  service: PlatformService;
  sourceEntityId: number;
  category: SupportCaseCategory;
  status: SupportCaseStatus;
  description: string | null;
  createdAt: string;
  resolvedAt: string | null;
  resolutionComment: string | null;
}

export interface TransactionFeedback {
  id: number;
  outcome: FeedbackOutcome;
  category: SupportCaseCategory | null;
  comment: string | null;
  supportCaseId: number | null;
  createdAt: string;
}

export interface TransactionSupport {
  service: PlatformService;
  sourceEntityId: number;
  feedbackEligible: boolean;
  feedback: TransactionFeedback | null;
  latestCase: SupportCase | null;
}

export interface CreateSupportCaseRequest {
  service: PlatformService;
  sourceEntityId: number;
  category: SupportCaseCategory;
  description: string | null;
}

export interface CreateTransactionFeedbackRequest {
  service: PlatformService;
  sourceEntityId: number;
  outcome: FeedbackOutcome;
  category: SupportCaseCategory | null;
  comment: string | null;
}

export interface AdminSupportCaseSummary {
  id: number;
  service: PlatformService;
  sourceEntityId: number;
  customerId: number;
  customerName: string;
  customerPhone: string;
  category: SupportCaseCategory;
  status: SupportCaseStatus;
  createdAt: string;
  resolvedAt: string | null;
  sourceAdminPath: string;
}

export interface AdminSupportCaseDetails {
  summary: AdminSupportCaseSummary;
  description: string | null;
  resolvedByCustomerId: number | null;
  resolutionComment: string | null;
  resolvedAt: string | null;
  sourceCustomerPath: string;
}

export interface AdminSupportCasePage {
  content: AdminSupportCaseSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
