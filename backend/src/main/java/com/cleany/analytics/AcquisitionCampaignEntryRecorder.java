package com.cleany.analytics;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcquisitionCampaignEntryRecorder {

    private final AcquisitionCampaignEntryRepository entryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(long campaignId, Instant occurredAt, AcquisitionPlatform platform) {
        entryRepository.save(new AcquisitionCampaignEntry(campaignId, occurredAt, platform));
    }
}
