export type AnalyticsService = "ALL" | "CLEANING" | "RENTAL";

export type AcquisitionTargetService = "PLATFORM" | "CLEANING" | "RENTAL";

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
