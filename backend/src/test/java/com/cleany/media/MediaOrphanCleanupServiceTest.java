package com.cleany.media;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

class MediaOrphanCleanupServiceTest {

    @Test
    void orphanAssets_providerReferencesDeletedBeforeAssets() {
        MediaAssetRepository assetRepository = Mockito.mock(MediaAssetRepository.class);
        MediaProviderReferenceRepository providerReferenceRepository =
                Mockito.mock(MediaProviderReferenceRepository.class);
        List<Long> mediaIds = List.of(71L, 72L);
        Mockito.when(assetRepository.lockAllUnreferencedIds()).thenReturn(mediaIds);
        Mockito.when(providerReferenceRepository.deleteAllByMediaAssetIds(mediaIds)).thenReturn(2);
        Mockito.when(assetRepository.deleteAllByIds(mediaIds)).thenReturn(2);
        var service = new MediaOrphanCleanupService(assetRepository, providerReferenceRepository);

        int deleted = service.deleteUnreferenced();

        InOrder deletionOrder = Mockito.inOrder(assetRepository, providerReferenceRepository);
        deletionOrder.verify(assetRepository).lockAllUnreferencedIds();
        deletionOrder.verify(providerReferenceRepository).deleteAllByMediaAssetIds(mediaIds);
        deletionOrder.verify(assetRepository).deleteAllByIds(mediaIds);
        Assertions.assertEquals(2, deleted);
    }

    @Test
    void noOrphans_deleteQueriesSkipped() {
        MediaAssetRepository assetRepository = Mockito.mock(MediaAssetRepository.class);
        MediaProviderReferenceRepository providerReferenceRepository =
                Mockito.mock(MediaProviderReferenceRepository.class);
        Mockito.when(assetRepository.lockAllUnreferencedIds()).thenReturn(List.of());
        var service = new MediaOrphanCleanupService(assetRepository, providerReferenceRepository);

        int deleted = service.deleteUnreferenced();

        Assertions.assertEquals(0, deleted);
        Mockito.verifyNoInteractions(providerReferenceRepository);
        Mockito.verify(assetRepository, Mockito.never()).deleteAllByIds(Mockito.anyList());
    }
}
