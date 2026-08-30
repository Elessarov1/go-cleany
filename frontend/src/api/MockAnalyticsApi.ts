import type { AnalyticsApi, AnalyticsOverviewRequest, CreateAcquisitionCampaignRequest } from "./AnalyticsApi";
import type { AcquisitionCampaign, AnalyticsOverview } from "../domain/analytics";

export class MockAnalyticsApi implements AnalyticsApi {
  private nextCampaignId = 100;

  getOverview(request: AnalyticsOverviewRequest): Promise<AnalyticsOverview> {
    const all = request.service === "ALL";
    const transitions: AnalyticsOverview["transitions"] = [
      { fromService: "CLEANING", toService: "CLEANING", cohortCustomers: 18, convertedCustomers: 6, conversionRate: 0.3333 },
      { fromService: "RENTAL", toService: "TRANSFER", cohortCustomers: 7, convertedCustomers: 3, conversionRate: 0.4286 },
      { fromService: "RENTAL", toService: "CLEANING", cohortCustomers: 7, convertedCustomers: 1, conversionRate: 0.1429 },
      { fromService: "TRANSFER", toService: "TRANSFER", cohortCustomers: 9, convertedCustomers: 2, conversionRate: 0.2222 },
    ];
    return Promise.resolve({
      period: request,
      customers: {
        newCustomers: 42,
        activeCustomers: 31,
        repeatCustomers: 9,
        repeatRate: 0.2903,
      },
      businessHealth: {
        completedTasks: 31,
        activeCustomers: 22,
        completedTasksPerActiveCustomer: 1.4091,
        customersWithTwoPlusCompletedTasks: 9,
        customersUsingTwoPlusServices: 5,
        crossServiceCustomerRate: 0.2273,
      },
      retention: {
        repeat30Days: { cohortCustomers: 18, convertedCustomers: 6, rate: 0.3333 },
        repeat90Days: { cohortCustomers: 12, convertedCustomers: 5, rate: 0.4167 },
        secondOrderConversion: { cohortCustomers: 34, convertedCustomers: 13, rate: 0.3824 },
        medianDaysToSecondTask: 18.5,
      },
      transitions: transitions.filter((transition) => all || transition.fromService === request.service),
      repeatActions: [
        {
          service: "CLEANING" as const,
          shownSources: 18,
          startedSources: 11,
          createdRepeatSources: 8,
          completedRepeatSources: 6,
          startRate: 0.6111,
          completionRate: 0.75,
          medianHoursToRepeat: 36.5,
        },
        {
          service: "TRANSFER" as const,
          shownSources: 9,
          startedSources: 5,
          createdRepeatSources: 4,
          completedRepeatSources: 2,
          startRate: 0.5556,
          completionRate: 0.5,
          medianHoursToRepeat: 18,
        },
      ].filter((metric) => all || metric.service === request.service),
      averageChecks: [
        ...(all || request.service === "CLEANING" ? [{ service: "CLEANING" as const, currency: "TRY", amount: 2150, completedTransactions: 19 }] : []),
        ...(all || request.service === "RENTAL" ? [{ service: "RENTAL" as const, currency: "TRY", amount: 27850, completedTransactions: 5 }] : []),
        ...(all || request.service === "TRANSFER" ? [{ service: "TRANSFER" as const, currency: "TRY", amount: 2400, completedTransactions: 7 }] : []),
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
        : request.targetService === "RENTAL"
          ? "/rent"
          : request.targetService === "TRANSFER" ? "/transfer" : "/",
    });
  }
}
