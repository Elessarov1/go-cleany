package com.cleany.media;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaOrphanCleanupService {

    private final MediaAssetRepository assetRepository;
    private final MediaProviderReferenceRepository providerReferenceRepository;

    @Transactional
    public int deleteUnreferenced() {
        List<Long> mediaIds = assetRepository.lockAllUnreferencedIds();
        if (mediaIds.isEmpty()) {
            return 0;
        }
        providerReferenceRepository.deleteAllByMediaAssetIds(mediaIds);
        return assetRepository.deleteAllByIds(mediaIds);
    }
}
