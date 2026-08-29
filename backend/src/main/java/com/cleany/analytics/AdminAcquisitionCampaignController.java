package com.cleany.analytics;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.admin.AdminAccessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/acquisition-campaigns")
@RequiredArgsConstructor
public class AdminAcquisitionCampaignController {

    private final AdminAccessService accessService;
    private final AcquisitionCampaignService campaignService;

    @GetMapping
    public List<AcquisitionCampaignResponse> getCampaigns() {
        accessService.requireCurrentAdmin();
        return campaignService.getCampaigns();
    }

    @PostMapping
    public AcquisitionCampaignResponse create(
            @Valid @RequestBody CreateAcquisitionCampaignRequest request
    ) {
        accessService.requireCurrentAdmin();
        return campaignService.create(request);
    }

    @PatchMapping("/{campaignId}")
    public AcquisitionCampaignResponse update(
            @PathVariable long campaignId,
            @Valid @RequestBody UpdateAcquisitionCampaignRequest request
    ) {
        accessService.requireCurrentAdmin();
        return campaignService.update(campaignId, request);
    }
}
