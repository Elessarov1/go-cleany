package com.cleany.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {

    /**
     * Every new domain table that owns media must be added to this orphan check before rollout.
     */
    @Query(value = """
            select asset.id
              from media_asset asset
             where not exists (
                       select 1
                         from cleaning_order_photo completion_photo
                        where completion_photo.media_asset_id = asset.id
                   )
               and not exists (
                       select 1
                         from cleaning_order_issue_photo issue_photo
                        where issue_photo.media_asset_id = asset.id
                   )
             order by asset.id
             for update of asset
            """, nativeQuery = true)
    List<Long> lockAllUnreferencedIds();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MediaAsset asset where asset.id in :mediaIds")
    int deleteAllByIds(@Param("mediaIds") List<Long> mediaIds);
}
