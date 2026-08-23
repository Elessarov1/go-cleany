package com.cleany.rental;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RentalOccupancyRepository {

    private static final String SELECT_COLUMNS = """
            occupancy.id,
            occupancy.property_id,
            lower(occupancy.date_range) as start_date,
            upper(occupancy.date_range) as end_date,
            occupancy.type,
            occupancy.booking_id,
            occupancy.note,
            occupancy.created_at,
            occupancy.created_by_admin_id
            """;

    private final JdbcTemplate jdbcTemplate;

    public boolean overlaps(long propertyId, LocalDate startDate, LocalDate endDate) {
        Boolean result = jdbcTemplate.queryForObject(
                """
                select exists (
                    select 1
                      from rental_occupancy occupancy
                     where occupancy.property_id = ?
                       and occupancy.date_range && daterange(?, ?, '[)')
                )
                """,
                Boolean.class,
                propertyId,
                startDate,
                endDate
        );
        return Boolean.TRUE.equals(result);
    }

    public RentalOccupancy create(
            long propertyId,
            LocalDate startDate,
            LocalDate endDate,
            RentalOccupancyType type,
            Long bookingId,
            String note,
            Instant createdAt,
            Long createdByAdminId
    ) {
        return jdbcTemplate.queryForObject(
                """
                insert into rental_occupancy (
                    property_id,
                    date_range,
                    type,
                    booking_id,
                    note,
                    created_at,
                    created_by_admin_id
                ) values (?, daterange(?, ?, '[)'), ?, ?, ?, ?, ?)
                returning
                """ + SELECT_COLUMNS.replace("occupancy.", ""),
                RentalOccupancyRepository::map,
                propertyId,
                startDate,
                endDate,
                type.name(),
                bookingId,
                note,
                OffsetDateTime.ofInstant(createdAt, ZoneOffset.UTC),
                createdByAdminId
        );
    }

    public RentalOccupancy updateManual(
            long occupancyId,
            long propertyId,
            LocalDate startDate,
            LocalDate endDate,
            RentalOccupancyType type,
            String note
    ) {
        List<RentalOccupancy> updated = jdbcTemplate.query(
                """
                update rental_occupancy
                   set date_range = daterange(?, ?, '[)'),
                       type = ?,
                       note = ?
                 where id = ?
                   and property_id = ?
                   and type <> 'BOOKING'
                returning
                """ + SELECT_COLUMNS.replace("occupancy.", ""),
                RentalOccupancyRepository::map,
                startDate,
                endDate,
                type.name(),
                note,
                occupancyId,
                propertyId
        );
        return updated.stream().findFirst()
                .orElseThrow(() -> new RentalOccupancyNotFoundException(occupancyId));
    }

    public List<RentalOccupancy> findOverlapping(
            long propertyId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return jdbcTemplate.query(
                "select " + SELECT_COLUMNS + """
                  from rental_occupancy occupancy
                 where occupancy.property_id = ?
                   and occupancy.date_range && daterange(?, ?, '[)')
                 order by lower(occupancy.date_range), occupancy.id
                """,
                RentalOccupancyRepository::map,
                propertyId,
                startDate,
                endDate
        );
    }

    public Optional<RentalOccupancy> findByIdAndPropertyId(long occupancyId, long propertyId) {
        return jdbcTemplate.query(
                "select " + SELECT_COLUMNS + """
                  from rental_occupancy occupancy
                 where occupancy.id = ?
                   and occupancy.property_id = ?
                """,
                RentalOccupancyRepository::map,
                occupancyId,
                propertyId
        ).stream().findFirst();
    }

    public int deleteManual(long occupancyId, long propertyId) {
        return jdbcTemplate.update(
                """
                delete from rental_occupancy
                 where id = ?
                   and property_id = ?
                   and type <> 'BOOKING'
                """,
                occupancyId,
                propertyId
        );
    }

    public int deleteByBookingId(long bookingId) {
        return jdbcTemplate.update(
                "delete from rental_occupancy where booking_id = ? and type = 'BOOKING'",
                bookingId
        );
    }

    public int deleteManualByPropertyId(long propertyId) {
        return jdbcTemplate.update(
                "delete from rental_occupancy where property_id = ? and booking_id is null",
                propertyId
        );
    }

    private static RentalOccupancy map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RentalOccupancy(
                resultSet.getLong("id"),
                resultSet.getLong("property_id"),
                resultSet.getObject("start_date", LocalDate.class),
                resultSet.getObject("end_date", LocalDate.class),
                RentalOccupancyType.valueOf(resultSet.getString("type")),
                nullableLong(resultSet, "booking_id"),
                resultSet.getString("note"),
                resultSet.getObject("created_at", OffsetDateTime.class).toInstant(),
                nullableLong(resultSet, "created_by_admin_id")
        );
    }

    private static Long nullableLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? null : value;
    }
}
