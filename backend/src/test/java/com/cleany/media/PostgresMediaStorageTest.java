package com.cleany.media;

import java.time.Clock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PostgresMediaStorageTest {

    private final MediaAssetRepository assetRepository = Mockito.mock(MediaAssetRepository.class);
    private final MediaProviderReferenceRepository referenceRepository = Mockito.mock(
            MediaProviderReferenceRepository.class
    );
    private final PostgresMediaStorage storage = new PostgresMediaStorage(
            assetRepository,
            referenceRepository,
            Clock.systemUTC()
    );

    @Test
    void delete_existingAsset_doesNotMaterializeBinaryEntity() {
        Mockito.when(assetRepository.deleteByIdWithoutLoading(71L)).thenReturn(1);

        storage.delete(71L);

        Mockito.verify(referenceRepository).deleteAllByMediaAssetId(71L);
        Mockito.verify(assetRepository).deleteByIdWithoutLoading(71L);
        Mockito.verify(assetRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void delete_missingAsset_preservesNotFoundContract() {
        Mockito.when(assetRepository.deleteByIdWithoutLoading(71L)).thenReturn(0);

        Assertions.assertThrows(MediaNotFoundException.class, () -> storage.delete(71L));

        Mockito.verify(referenceRepository).deleteAllByMediaAssetId(71L);
        Mockito.verify(assetRepository).deleteByIdWithoutLoading(71L);
        Mockito.verify(assetRepository, Mockito.never()).findById(Mockito.anyLong());
    }
}
