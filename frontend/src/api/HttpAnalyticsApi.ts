import type { AnalyticsApi, AnalyticsOverviewRequest, CreateAcquisitionCampaignRequest } from "./AnalyticsApi";
import type { AcquisitionCampaign, AnalyticsOverview } from "../domain/analytics";
import { HttpApiClient } from "./HttpApiClient";

export class HttpAnalyticsApi implements AnalyticsApi {
  constructor(private readonly client: HttpApiClient) {}

  getOverview(request: AnalyticsOverviewRequest): Promise<AnalyticsOverview> {
    const parameters = new URLSearchParams({
      from: request.from,
      to: request.to,
      service: request.service,
    });
    return this.client.request(`/api/v1/admin/analytics/overview?${parameters.toString()}`);
  }

  createCampaign(request: CreateAcquisitionCampaignRequest): Promise<AcquisitionCampaign> {
    return this.client.request("/api/v1/admin/acquisition-campaigns", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }
}
