import type { AnalyticsApi, AnalyticsOverviewRequest, CreateAcquisitionCampaignRequest } from "./AnalyticsApi";
import type { AcquisitionCampaign, AnalyticsOverview } from "../domain/analytics";

export class MockAnalyticsApi implements AnalyticsApi {
  private nextCampaignId = 100;

  getOverview(request: AnalyticsOverviewRequest): Promise<AnalyticsOverview> {
    const all = request.service === "ALL";
    return Promise.resolve({
      period: request,
      customers: {
        newCustomers: 42,
        activeCustomers: 31,
        repeatCustomers: 9,
        repeatRate: 0.2903,
      },
      averageChecks: [
        ...(all || request.service === "CLEANING" ? [{ service: "CLEANING" as const, currency: "TRY", amount: 2150, completedTransactions: 19 }] : []),
        ...(all || request.service === "RENTAL" ? [{ service: "RENTAL" as const, currency: "TRY", amount: 27850, completedTransactions: 5 }] : []),
      ],
      acquisition: [
        { channel: "QR", campaignId: 12, campaignName: "Mahmutlar magnets", medium: "QR_MAGNET", entries: 170, newCustomers: 28, completedTransactions: 19 },
        { channel: "ORGANIC", campaignId: null, campaignName: null, medium: null, entries: 0, newCustomers: 14, completedTransactions: 8 },
      ],
    });
  }

  createCampaign(request: CreateAcquisitionCampaignRequest): Promise<AcquisitionCampaign> {
    return Promise.resolve({
      id: this.nextCampaignId++,
      ...request,
      partnerName: null,
      active: true,
      createdAt: new Date().toISOString(),
      disabledAt: null,
      trackingPath: `/a/${request.publicCode}`,
      targetPath: request.targetService === "CLEANING"
        ? "/cleaning"
        : request.targetService === "RENTAL" ? "/rent" : "/",
    });
  }
}
