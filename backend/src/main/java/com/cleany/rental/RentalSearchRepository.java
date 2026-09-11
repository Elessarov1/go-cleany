package com.cleany.rental;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RentalSearchRepository {

    private static final String SELECT_PROPERTY = """
            property.id,
            property.display_order,
            property.slug,
            property.title_ru,
            property.title_en,
            property.description_en,
            property.area,
            property.bedrooms,
            property.max_guests,
            property.area_sqm,
            property.base_daily_price,
            property.currency
            """;

    private final JdbcTemplate jdbcTemplate;

    public List<RentalSearchPropertyRow> findAvailable(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guests,
            Integer afterDisplayOrder,
            Long afterPropertyId,
            int limit
    ) {
        if (afterDisplayOrder == null || afterPropertyId == null) {
            return jdbcTemplate.query(
                    """
                select %s
                  from rental_property property
                 where property.status = 'PUBLISHED'
                   and property.max_guests >= ?
                   and not exists (
                       select 1
                         from rental_occupancy occupancy
                        where occupancy.property_id = property.id
                          and occupancy.date_range && daterange(?, ?, '[)')
                   )
                 order by property.display_order, property.id
                 limit ?
                    """.formatted(SELECT_PROPERTY),
                    RentalSearchRepository::mapProperty,
                    guests,
                    checkInDate,
                    RentalDateRange.exclusiveEnd(checkOutDate),
                    limit
            );
        }
        return jdbcTemplate.query(
                """
                select %s
                  from rental_property property
                 where property.status = 'PUBLISHED'
                   and property.max_guests >= ?
                   and (property.display_order, property.id) > (?, ?)
                   and not exists (
                       select 1
                         from rental_occupancy occupancy
                        where occupancy.property_id = property.id
                          and occupancy.date_range && daterange(?, ?, '[)')
                   )
                 order by property.display_order, property.id
                 limit ?
                """.formatted(SELECT_PROPERTY),
                RentalSearchRepository::mapProperty,
                guests,
                afterDisplayOrder,
                afterPropertyId,
                checkInDate,
                RentalDateRange.exclusiveEnd(checkOutDate),
                limit
        );
    }

    public List<RentalSearchPropertyRow> findPublished(
            Integer afterDisplayOrder,
            Long afterPropertyId,
            int limit
    ) {
        if (afterDisplayOrder == null || afterPropertyId == null) {
            return jdbcTemplate.query(
                    """
                    select %s
                      from rental_property property
                     where property.status = 'PUBLISHED'
                     order by property.display_order, property.id
                     limit ?
                    """.formatted(SELECT_PROPERTY),
                    RentalSearchRepository::mapProperty,
                    limit
            );
        }
        return jdbcTemplate.query(
                """
                select %s
                  from rental_property property
                 where property.status = 'PUBLISHED'
                   and (property.display_order, property.id) > (?, ?)
                 order by property.display_order, property.id
                 limit ?
                """.formatted(SELECT_PROPERTY),
                RentalSearchRepository::mapProperty,
                afterDisplayOrder,
                afterPropertyId,
                limit
        );
    }

    public List<RentalSearchCoverRow> findCovers(List<Long> propertyIds) {
        if (propertyIds.isEmpty()) {
            return Collections.emptyList();
        }
        return jdbcTemplate.query(connection -> {
            Array ids = connection.createArrayOf("bigint", propertyIds.toArray());
            var statement = connection.prepareStatement("""
                    select media.property_id,
                           media.id,
                           coalesce(media.card_media_asset_id, media.media_asset_id) as version
                      from rental_property_media media
                     where media.property_id = any (?)
                       and media.is_cover
                     order by media.property_id, media.id
                    """);
            statement.setArray(1, ids);
            return statement;
        }, (resultSet, rowNumber) -> new RentalSearchCoverRow(
                resultSet.getLong("property_id"),
                resultSet.getLong("id"),
                resultSet.getLong("version")
        ));
    }

    private static RentalSearchPropertyRow mapProperty(
            java.sql.ResultSet resultSet,
            int rowNumber
    ) throws SQLException {
        return new RentalSearchPropertyRow(
                resultSet.getLong("id"),
                resultSet.getInt("display_order"),
                resultSet.getString("slug"),
                resultSet.getString("title_ru"),
                resultSet.getString("title_en"),
                resultSet.getString("description_en"),
                resultSet.getString("area"),
                resultSet.getInt("bedrooms"),
                resultSet.getInt("max_guests"),
                resultSet.getBigDecimal("area_sqm"),
                resultSet.getBigDecimal("base_daily_price"),
                resultSet.getString("currency")
        );
    }
}
