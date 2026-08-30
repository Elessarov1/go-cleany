export type AnalyticsService = "ALL" | "CLEANING" | "RENTAL" | "TRANSFER";

export type AcquisitionTargetService = "PLATFORM" | "CLEANING" | "RENTAL" | "TRANSFER";

export type AcquisitionChannel =
  | "ORGANIC"
  | "QR"
  | "PARTNER"
  | "CUSTOMER_REFERRAL"
  | "PROMO_CAMPAIGN"
  | "DIRECT_CAMPAIGN"
  | "OTHER";

export type AcquisitionMedium =
  | "QR_STICKER"
  | "QR_MAGNET"
  | "QR_PRINT"
  | "PARTNER_LINK"
  | "REFERRAL_CODE"
  | "PROMO_CODE"
  | "DIRECT_LINK"
  | "OTHER";

export interface AcquisitionCampaign {
  id: number;
  publicCode: string;
  name: string;
  channel: AcquisitionChannel;
  medium: AcquisitionMedium;
  targetService: AcquisitionTargetService;
  partnerId: number | null;
  partnerName: string | null;
  active: boolean;
  createdAt: string;
  disabledAt: string | null;
  trackingPath: string;
  targetPath: string;
}

export interface AnalyticsOverview {
  period: {
    from: string;
    to: string;
    service: AnalyticsService;
  };
  customers: {
    newCustomers: number;
    activeCustomers: number;
    repeatCustomers: number;
    repeatRate: number;
  };
  businessHealth: {
    completedTasks: number;
    activeCustomers: number;
    completedTasksPerActiveCustomer: number;
    customersWithTwoPlusCompletedTasks: number;
    customersUsingTwoPlusServices: number;
    crossServiceCustomerRate: number;
  };
  retention: {
    repeat30Days: AnalyticsCohortMetric;
    repeat90Days: AnalyticsCohortMetric;
    secondOrderConversion: AnalyticsCohortMetric;
    medianDaysToSecondTask: number | null;
  };
  transitions: Array<{
    fromService: Exclude<AnalyticsService, "ALL">;
    toService: Exclude<AnalyticsService, "ALL">;
    cohortCustomers: number;
    convertedCustomers: number;
    conversionRate: number | null;
  }>;
  averageChecks: Array<{
    service: Exclude<AnalyticsService, "ALL">;
    currency: string;
    amount: number;
    completedTransactions: number;
  }>;
  acquisition: Array<{
    channel: AcquisitionChannel;
    campaignId: number | null;
    campaignName: string | null;
    medium: AcquisitionMedium | null;
    entries: number;
    newCustomers: number;
    completedTransactions: number;
  }>;
}

interface AnalyticsCohortMetric {
  cohortCustomers: number;
  convertedCustomers: number;
  rate: number | null;
}
