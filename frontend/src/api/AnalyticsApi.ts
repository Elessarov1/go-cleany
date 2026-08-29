import type {
  AcquisitionCampaign,
  AcquisitionChannel,
  AcquisitionMedium,
  AcquisitionTargetService,
  AnalyticsOverview,
  AnalyticsService,
} from "../domain/analytics";

export interface AnalyticsOverviewRequest {
  from: string;
  to: string;
  service: AnalyticsService;
}

export interface CreateAcquisitionCampaignRequest {
  publicCode: string;
  name: string;
  channel: Exclude<AcquisitionChannel, "ORGANIC">;
  medium: AcquisitionMedium;
  targetService: AcquisitionTargetService;
  partnerId: number | null;
}

export interface AnalyticsApi {
  getOverview(request: AnalyticsOverviewRequest): Promise<AnalyticsOverview>;
  createCampaign(request: CreateAcquisitionCampaignRequest): Promise<AcquisitionCampaign>;
}
