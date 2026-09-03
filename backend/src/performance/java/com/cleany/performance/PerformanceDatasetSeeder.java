package com.cleany.performance;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
final class PerformanceDatasetSeeder {

    private static final Logger log = LoggerFactory.getLogger(PerformanceDatasetSeeder.class);
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Istanbul");
    private static final int IMAGE_WIDTH = 1280;
    private static final int IMAGE_HEIGHT = 720;

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    void seed() {
        int scale = integerSetting("PERF_SCALE", 1, 1, 20);
        long seed = longSetting("PERF_SEED", 42L);
        LocalDate anchorDate = LocalDate.parse(environment.getProperty(
                "PERF_ANCHOR_DATE",
                LocalDate.now(BUSINESS_ZONE).toString()
        ));
        Counts counts = Counts.scaled(scale);
        List<BufferedImage> localImages = loadLocalImages();

        var transaction = new TransactionTemplate(transactionManager);
        SeedResult result = transaction.execute(status -> {
            assertDedicatedDatabase();
            resetDataset();
            seedCustomers(counts, anchorDate);
            seedCleaning(counts, anchorDate);
            List<PropertyManifest> properties = seedRental(counts, anchorDate, seed, localImages);
            seedTransfers(counts, anchorDate);
            seedNotifications(counts, anchorDate);
            seedReminders(counts, anchorDate);
            seedSupportCases(counts, anchorDate);
            synchronizeSequences();
            return new SeedResult(properties);
        });
        if (result == null) {
            throw new IllegalStateException("Performance dataset transaction returned no result");
        }

        writeManifest(seed, scale, anchorDate, counts, result.properties());
        log.info(
                "Performance dataset created: seed={}, scale={}, customers={}, cleaningOrders={}, "
                        + "rentalProperties={}, rentalBookings={}, transferBookings={}, notifications={}, "
                        + "reminders={}, supportCases={}",
                seed,
                scale,
                counts.customers(),
                counts.cleaningOrders(),
                counts.rentalProperties(),
                counts.rentalBookings(),
                counts.transferBookings(),
                counts.notifications(),
                counts.reminders(),
                counts.supportCases()
        );
    }

    private void assertDedicatedDatabase() {
        String database = jdbcTemplate.queryForObject("select current_database()", String.class);
        if (!"loco_performance".equals(database)) {
            throw new IllegalStateException("Refusing to seed database: " + database);
        }
    }

    private void resetDataset() {
        jdbcTemplate.execute("""
                truncate table customer_account, rental_property, media_asset, transfer_driver
                restart identity cascade;

                insert into platform_service_state (
                    service, status, updated_at, updated_by_customer_id, version, display_order
                ) values
                    ('CLEANING', 'ENABLED', now(), null, 0, 1),
                    ('RENTAL', 'ENABLED', now(), null, 0, 2),
                    ('TRANSFER', 'ENABLED', now(), null, 0, 3)
                """);
    }

    private void seedCustomers(Counts counts, LocalDate anchorDate) {
        jdbcTemplate.execute("""
                insert into customer_account (id, created_at, phone)
                select i,
                       ('%1$s'::date - ((i %% 365) * interval '1 day'))::timestamptz,
                       '+90555' || lpad(i::text, 7, '0')
                  from generate_series(1, %2$d) i;

                insert into customer_external_identity (
                    id, customer_id, provider, external_subject, username, display_name,
                    language_code, email, email_verified, write_access_allowed,
                    write_access_updated_at, last_seen_at
                )
                select i,
                       i,
                       'TELEGRAM',
                       case when i = 1 then '990000001' else (990000000 + i)::text end,
                       'perf_customer_' || i,
                       'Performance Customer ' || i,
                       case when i %% 3 = 0 then 'ru' else 'en' end,
                       null,
                       false,
                       false,
                       null,
                       '%1$s'::date::timestamptz
                  from generate_series(1, %2$d) i;
                """.formatted(anchorDate, counts.customers()));
    }

    private void seedCleaning(Counts counts, LocalDate anchorDate) {
        jdbcTemplate.execute("""
                with generated as (
                    select i,
                           ((i - 1) %% %2$d) + 1 as customer_id,
                           case i %% 12
                               when 6 then 'NEW'
                               when 7 then 'ACCEPTED'
                               when 8 then 'AWAITING_REPORT'
                               when 9 then 'ONSITE_ISSUE_REPORTED'
                               when 10 then 'CANCELLED'
                               when 11 then 'REJECTED'
                               else 'COMPLETED'
                           end as status,
                           800 + (i %% 6) * 150 as base_price
                      from generate_series(1, %3$d) i
                )
                insert into cleaning_order (
                    id, telegram_user_id, telegram_username, customer_name, phone, area,
                    address, apartment_type, duplex, cleaning_type, price, currency,
                    requested_date, customer_comment, cleaner_comment, cleaner_telegram_user_id,
                    status, created_at, accepted_at, completed_at, report_input_active,
                    customer_id, base_price, commission_rate, base_commission, customer_discount,
                    partner_payout, final_customer_price, platform_net, acquisition_source,
                    customer_discount_type, communication_identity_id, version
                )
                select i,
                       null,
                       null,
                       'Performance Customer ' || customer_id,
                       '+90555' || lpad(customer_id::text, 7, '0'),
                       (array['MAHMUTLAR', 'KARGICAK', 'KESTEL'])[(i %% 3) + 1],
                       'Performance Residence ' || ((i - 1) %% 40 + 1),
                       (array['STUDIO', 'ONE_PLUS_ONE', 'TWO_PLUS_ONE', 'THREE_PLUS_ONE'])[(i %% 4) + 1],
                       i %% 7 = 0,
                       case when i %% 4 = 0 then 'DEEP' else 'REGULAR' end,
                       base_price,
                       'TRY',
                       case when status in ('NEW', 'ACCEPTED', 'AWAITING_REPORT', 'ONSITE_ISSUE_REPORTED')
                           then '%1$s'::date + ((i %% 7) + 1)
                           else '%1$s'::date - ((i %% 90) + 1)
                       end,
                       null,
                       case when status = 'COMPLETED' then 'Synthetic completion' else null end,
                       case when status in ('ACCEPTED', 'AWAITING_REPORT', 'ONSITE_ISSUE_REPORTED', 'COMPLETED')
                           then 123456789 + i else null end,
                       status,
                       ('%1$s'::date - ((i %% 180) * interval '1 day'))::timestamptz,
                       case when status in ('ACCEPTED', 'AWAITING_REPORT', 'ONSITE_ISSUE_REPORTED', 'COMPLETED')
                           then ('%1$s'::date - ((i %% 90) * interval '1 day'))::timestamptz else null end,
                       case when status = 'COMPLETED'
                           then ('%1$s'::date - ((i %% 80) * interval '1 day'))::timestamptz else null end,
                       status = 'AWAITING_REPORT',
                       customer_id,
                       base_price,
                       0.150000,
                       round(base_price * 0.15, 2),
                       0,
                       0,
                       base_price,
                       round(base_price * 0.15, 2),
                       'ORGANIC',
                       'NONE',
                       customer_id,
                       0
                  from generated;

                insert into cleaning_order_event (
                    id, order_id, event_type, from_status, to_status, actor_type,
                    actor_telegram_user_id, details, occurred_at
                )
                select id,
                       id,
                       'CREATED',
                       null,
                       status,
                       'SYSTEM',
                       null,
                       'Synthetic performance event',
                       created_at
                  from cleaning_order;
                """.formatted(anchorDate, counts.customers(), counts.cleaningOrders()));
    }

    private List<PropertyManifest> seedRental(
            Counts counts,
            LocalDate anchorDate,
            long seed,
            List<BufferedImage> localImages
    ) {
        jdbcTemplate.execute("""
                insert into rental_property (
                    id, slug, title_ru, title_en, description_en, area, address, bedrooms,
                    beds, bathrooms, max_guests, area_sqm, floor, base_daily_price,
                    currency, status, created_at, updated_at
                )
                select i,
                       'performance-apartment-' || i,
                       'Тестовая квартира ' || i,
                       'Performance apartment ' || i,
                       'Deterministic local performance dataset apartment.',
                       (array['Mahmutlar', 'Kestel', 'Oba', 'Kargicak'])[(i %% 4) + 1],
                       'Performance Rental Address ' || i,
                       1 + (i %% 3),
                       1 + (i %% 4),
                       1 + (i %% 2),
                       2 + (i %% 5),
                       55 + (i %% 8) * 10,
                       i %% 10,
                       1800 + (i %% 7) * 250,
                       'TRY',
                       'PUBLISHED',
                       ('%1$s'::date - ((i %% 90) * interval '1 day'))::timestamptz,
                       '%1$s'::date::timestamptz
                  from generate_series(1, %2$d) i;

                insert into rental_property_amenity (property_id, amenity)
                select property_id, amenity
                  from generate_series(1, %2$d) property_id
                  cross join (values ('WIFI'), ('AIR_CONDITIONING'), ('KITCHEN')) amenities(amenity);
                """.formatted(anchorDate, counts.rentalProperties()));

        var manifests = new ArrayList<PropertyManifest>(counts.rentalProperties());
        long mediaId = 1;
        long mediaAssetId = 1;
        for (long propertyId = 1; propertyId <= counts.rentalProperties(); propertyId++) {
            var mediaUrls = new ArrayList<String>(Counts.IMAGES_PER_PROPERTY);
            var cardUrls = new ArrayList<String>(Counts.IMAGES_PER_PROPERTY);
            var thumbnailUrls = new ArrayList<String>(Counts.IMAGES_PER_PROPERTY);
            for (int imageIndex = 0; imageIndex < Counts.IMAGES_PER_PROPERTY; imageIndex++) {
                BufferedImage image = performanceImage(seed, propertyId, imageIndex, localImages);
                byte[] full = encodeJpeg(image, 0.72f + (imageIndex % 4) * 0.06f);
                byte[] card = encodeJpeg(resizedToLongSide(image, 960), 0.78f);
                byte[] thumbnail = encodeJpeg(resizedToLongSide(image, 320), 0.72f);
                long fullAssetId = mediaAssetId++;
                long cardAssetId = mediaAssetId++;
                long thumbnailAssetId = mediaAssetId++;
                insertMediaAsset(fullAssetId, full, anchorDate);
                insertMediaAsset(cardAssetId, card, anchorDate);
                insertMediaAsset(thumbnailAssetId, thumbnail, anchorDate);
                jdbcTemplate.update("""
                        insert into rental_property_media (
                            id, property_id, media_asset_id, card_media_asset_id,
                            thumbnail_media_asset_id, sort_order, is_cover, created_at
                        ) values (?, ?, ?, ?, ?, ?, ?, ?::date::timestamptz)
                        """,
                        mediaId,
                        propertyId,
                        fullAssetId,
                        cardAssetId,
                        thumbnailAssetId,
                        imageIndex,
                        imageIndex == 0,
                        anchorDate.toString()
                );
                String mediaUrl = "/api/v1/rental/properties/" + propertyId + "/media/" + mediaId;
                mediaUrls.add(mediaUrl);
                cardUrls.add(mediaUrl + "/card");
                thumbnailUrls.add(mediaUrl + "/thumbnail");
                mediaId++;
            }
            var imageBurstUrls = new ArrayList<String>(Counts.IMAGES_PER_PROPERTY + 1);
            imageBurstUrls.add(mediaUrls.getFirst());
            imageBurstUrls.addAll(thumbnailUrls);
            manifests.add(new PropertyManifest(
                    propertyId,
                    "performance-apartment-" + propertyId,
                    mediaUrls,
                    cardUrls,
                    thumbnailUrls,
                    imageBurstUrls
            ));
        }

        jdbcTemplate.execute("""
                with generated as (
                    select i,
                           ((i - 1) %% %2$d) + 1 as property_id,
                           ((i - 1) / %2$d) as slot,
                           ((i - 1) %% %3$d) + 1 as customer_id
                      from generate_series(1, %4$d) i
                ), resolved as (
                    select *,
                           case when slot = 6
                               then '%1$s'::date - 4
                               else '%1$s'::date + (-100 + slot * 20)
                           end as check_in_date,
                           case
                               when slot <= 3 then 'COMPLETED'
                               when slot = 4 then 'CANCELLED_BY_CUSTOMER'
                               when slot = 5 then 'CANCELLED_BY_ADMIN'
                               else 'CONFIRMED'
                           end as status
                      from generated
                )
                insert into rental_booking (
                    id, customer_id, communication_identity_id, property_id, check_in_date,
                    check_out_date, duration_days, customer_name, phone, guests, comment,
                    base_daily_price_snapshot, long_term_discount_rate_snapshot, discount_amount,
                    total_price, currency, status, created_at, cancelled_at, cancellation_reason,
                    completed_at, term_type, rental_months, monthly_price_snapshot, version
                )
                select i,
                       customer_id,
                       customer_id,
                       property_id,
                       check_in_date,
                       check_in_date + 7,
                       7,
                       'Performance Customer ' || customer_id,
                       '+90555' || lpad(customer_id::text, 7, '0'),
                       1 + (i %% 4),
                       null,
                       1800 + (property_id %% 7) * 250,
                       0,
                       0,
                       (1800 + (property_id %% 7) * 250) * 7,
                       'TRY',
                       status,
                       (check_in_date - interval '14 days')::timestamptz,
                       case when status like 'CANCELLED%%' then (check_in_date - interval '7 days')::timestamptz else null end,
                       case when status = 'CANCELLED_BY_ADMIN' then 'Synthetic cancellation' else null end,
                       case when status = 'COMPLETED' then (check_in_date + interval '7 days')::timestamptz else null end,
                       'DATE_RANGE',
                       null,
                       null,
                       0
                  from resolved;

                insert into rental_occupancy (
                    id, property_id, date_range, type, booking_id, note, created_at, created_by_admin_id
                )
                select id,
                       property_id,
                       daterange(check_in_date, check_out_date, '[)'),
                       'BOOKING',
                       id,
                       null,
                       created_at,
                       null
                  from rental_booking
                 where status in ('CONFIRMED', 'COMPLETED');
                """.formatted(
                anchorDate,
                counts.rentalProperties(),
                counts.customers(),
                counts.rentalBookings()
        ));
        return manifests;
    }

    private void seedTransfers(Counts counts, LocalDate anchorDate) {
        TransferConfiguration config = jdbcTemplate.queryForObject("""
                select airport.id, airport.code, airport.name_ru, airport.name_en,
                       vehicle.id, vehicle.code, vehicle.name_ru, vehicle.name_en
                  from transfer_airport airport
                  cross join transfer_vehicle_type vehicle
                 where airport.enabled and vehicle.enabled
                 order by airport.sort_order, vehicle.sort_order
                 limit 1
                """, (resultSet, rowNum) -> new TransferConfiguration(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getLong(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8)
        ));
        if (config == null) {
            throw new IllegalStateException("Transfer configuration was not seeded by Liquibase");
        }
        jdbcTemplate.update("""
                insert into transfer_driver (
                    id, name, phone, enabled, configured_telegram_user_id,
                    verified_telegram_user_id, telegram_chat_id, telegram_notifications_enabled,
                    telegram_bot_authorized_at, created_at, updated_at, version
                ) values (1, 'Performance Driver', '+905550000001', true, null, null, null,
                          false, null, ?::date::timestamptz, ?::date::timestamptz, 0)
                """, anchorDate.toString(), anchorDate.toString());

        jdbcTemplate.execute("""
                with generated as (
                    select i,
                           ((i - 1) %% %2$d) + 1 as customer_id,
                           case i %% 10
                               when 5 then 'CONFIRMED'
                               when 6 then 'CONFIRMED'
                               when 7 then 'REQUESTED'
                               when 8 then 'CANCELLED'
                               when 9 then 'REJECTED'
                               else 'COMPLETED'
                           end as status,
                           case when i %% 2 = 0 then 'FROM_AIRPORT' else 'TO_AIRPORT' end as direction
                      from generate_series(1, %3$d) i
                ), resolved as (
                    select *,
                           case when status = 'COMPLETED'
                               then '%1$s'::date - ((i %% 90) + 1)
                               else '%1$s'::date + ((i %% 30) + 1)
                           end as pickup_date
                      from generated
                )
                insert into transfer_booking (
                    id, customer_id, communication_identity_id, customer_name_snapshot,
                    customer_phone_snapshot, direction, airport_id, airport_code_snapshot,
                    airport_name_ru_snapshot, airport_name_en_snapshot, vehicle_type_id,
                    vehicle_code_snapshot, vehicle_name_ru_snapshot, vehicle_name_en_snapshot,
                    pickup_date, pickup_time, address, passenger_count, luggage_count,
                    flight_number, scheduled_arrival_time, comment, price_amount, price_currency,
                    status, driver_id, created_at, confirmed_at, completed_at, cancelled_at,
                    rejected_at, status_reason, version
                )
                select i,
                       customer_id,
                       customer_id,
                       'Performance Customer ' || customer_id,
                       '+90555' || lpad(customer_id::text, 7, '0'),
                       direction,
                       %4$d,
                       '%5$s',
                       '%6$s',
                       '%7$s',
                       %8$d,
                       '%9$s',
                       '%10$s',
                       '%11$s',
                       pickup_date,
                       make_time(6 + (i %% 16), (i %% 2) * 30, 0),
                       'Performance Transfer Address ' || ((i - 1) %% 50 + 1),
                       1 + (i %% 4),
                       i %% 3,
                       case when direction = 'FROM_AIRPORT' then 'PC' || lpad(i::text, 4, '0') else null end,
                       case when direction = 'FROM_AIRPORT' then make_time(5 + (i %% 16), 0, 0) else null end,
                       null,
                       1200 + (i %% 5) * 150,
                       'TRY',
                       status,
                       case when status in ('CONFIRMED', 'COMPLETED') then 1 else null end,
                       (pickup_date - interval '10 days')::timestamptz,
                       case when status in ('CONFIRMED', 'COMPLETED') then (pickup_date - interval '5 days')::timestamptz else null end,
                       case when status = 'COMPLETED' then (pickup_date + interval '2 hours')::timestamptz else null end,
                       case when status = 'CANCELLED' then (pickup_date - interval '2 days')::timestamptz else null end,
                       case when status = 'REJECTED' then (pickup_date - interval '2 days')::timestamptz else null end,
                       case when status in ('CANCELLED', 'REJECTED') then 'Synthetic terminal state' else null end,
                       0
                  from resolved;
                """.formatted(
                anchorDate,
                counts.customers(),
                counts.transferBookings(),
                config.airportId(),
                sqlLiteral(config.airportCode()),
                sqlLiteral(config.airportNameRu()),
                sqlLiteral(config.airportNameEn()),
                config.vehicleId(),
                sqlLiteral(config.vehicleCode()),
                sqlLiteral(config.vehicleNameRu()),
                sqlLiteral(config.vehicleNameEn())
        ));
    }

    private void seedNotifications(Counts counts, LocalDate anchorDate) {
        jdbcTemplate.execute("""
                insert into customer_notification (
                    id, customer_id, type, target_path, dedup_key, created_at, read_at
                )
                select i,
                       ((i - 1) %% %2$d) + 1,
                       (array['CLEANING_ORDER_ACCEPTED', 'RENTAL_BOOKING_CONFIRMED',
                              'TRANSFER_BOOKING_CONFIRMED', 'SMART_REMINDER'])[(i %% 4) + 1],
                       '/cleaning/orders/' || (((i - 1) %% %3$d) + 1),
                       'performance-notification-' || i,
                       ('%1$s'::date - ((i %% 60) * interval '1 day')
                           + ((i %% 1440) * interval '1 minute'))::timestamptz,
                       case when i %% 2 = 0
                           then ('%1$s'::date - ((i %% 30) * interval '1 day'))::timestamptz
                           else null end
                  from generate_series(1, %4$d) i;
                """.formatted(
                anchorDate,
                counts.customers(),
                counts.cleaningOrders(),
                counts.notifications()
        ));
    }

    private void seedReminders(Counts counts, LocalDate anchorDate) {
        jdbcTemplate.execute("""
                with generated as (
                    select i,
                           ((i - 1) %% %2$d) + 1 as customer_id,
                           case i %% 3
                               when 0 then 'CLEANING_REPEAT'
                               when 1 then 'RENTAL_CHECKOUT_TRANSFER'
                               else 'TRANSFER_UPCOMING'
                           end as type,
                           case i %% 5
                               when 0 then 'PENDING'
                               when 1 then 'NOTIFIED'
                               when 2 then 'SUPERSEDED'
                               when 3 then 'EXPIRED'
                               else 'DISABLED'
                           end as raw_status
                      from generate_series(1, %3$d) i
                )
                insert into customer_reminder (
                    id, customer_id, type, source_service, source_entity_id, scheduled_date,
                    cleaning_interval_days, status, created_at, updated_at, notified_at
                )
                select i,
                       customer_id,
                       type,
                       case type when 'CLEANING_REPEAT' then 'CLEANING'
                                 when 'RENTAL_CHECKOUT_TRANSFER' then 'RENTAL'
                                 else 'TRANSFER' end,
                       case type when 'CLEANING_REPEAT' then ((i - 1) %% %4$d) + 1
                                 when 'RENTAL_CHECKOUT_TRANSFER' then ((i - 1) %% %5$d) + 1
                                 else ((i - 1) %% %6$d) + 1 end,
                       case when type = 'CLEANING_REPEAT' and raw_status = 'DISABLED'
                           then null else '%1$s'::date + ((i %% 20) - 10) end,
                       case when type = 'CLEANING_REPEAT' and raw_status <> 'DISABLED'
                           then case when i %% 2 = 0 then 14 else 30 end else null end,
                       case when type <> 'CLEANING_REPEAT' and raw_status = 'DISABLED'
                           then 'PENDING' else raw_status end,
                       ('%1$s'::date - ((i %% 30) * interval '1 day'))::timestamptz,
                       '%1$s'::date::timestamptz,
                       case when raw_status = 'NOTIFIED' then '%1$s'::date::timestamptz else null end
                  from generated;
                """.formatted(
                anchorDate,
                counts.customers(),
                counts.reminders(),
                counts.cleaningOrders(),
                counts.rentalBookings(),
                counts.transferBookings()
        ));
    }

    private void seedSupportCases(Counts counts, LocalDate anchorDate) {
        jdbcTemplate.execute("""
                with generated as (
                    select i,
                           ((i - 1) %% %2$d) + 1 as customer_id,
                           case i %% 3 when 0 then 'CLEANING' when 1 then 'RENTAL' else 'TRANSFER' end as service,
                           case when i %% 4 = 0 then 'RESOLVED' else 'OPEN' end as status
                      from generate_series(1, %3$d) i
                )
                insert into support_case (
                    id, customer_id, service, source_entity_id, category, status, description,
                    created_at, resolved_at, resolved_by_customer_id, resolution_comment, version
                )
                select i,
                       customer_id,
                       service,
                       i,
                       (array['PROVIDER_LATE', 'QUALITY_PROBLEM', 'BOOKING_PROBLEM', 'OTHER'])[(i %% 4) + 1],
                       status,
                       'Synthetic support case ' || i,
                       ('%1$s'::date - ((i %% 45) * interval '1 day'))::timestamptz,
                       case when status = 'RESOLVED' then '%1$s'::date::timestamptz else null end,
                       case when status = 'RESOLVED' then 1 else null end,
                       case when status = 'RESOLVED' then 'Synthetic resolution' else null end,
                       0
                  from generated;
                """.formatted(anchorDate, counts.customers(), counts.supportCases()));
    }

    private void synchronizeSequences() {
        for (String table : List.of(
                "customer_account",
                "customer_external_identity",
                "cleaning_order",
                "cleaning_order_event",
                "rental_property",
                "media_asset",
                "rental_property_media",
                "rental_booking",
                "rental_occupancy",
                "transfer_driver",
                "transfer_booking",
                "customer_notification",
                "customer_reminder",
                "support_case"
        )) {
            jdbcTemplate.execute("select setval(pg_get_serial_sequence('" + table
                    + "', 'id'), (select max(id) from " + table + "), true)");
        }
    }

    private void insertMediaAsset(long mediaAssetId, byte[] content, LocalDate anchorDate) {
        jdbcTemplate.update("""
                insert into media_asset (
                    id, content, content_type, size_bytes, sha256, created_at
                ) values (?, ?, 'image/jpeg', ?, ?, ?::date::timestamptz)
                """,
                mediaAssetId,
                content,
                content.length,
                sha256(content),
                anchorDate.toString()
        );
    }

    private BufferedImage performanceImage(
            long seed,
            long propertyId,
            int imageIndex,
            List<BufferedImage> localImages
    ) {
        BufferedImage image;
        if (localImages.isEmpty()) {
            image = generatedImage(seed + propertyId * 101 + imageIndex * 17, imageIndex);
        } else {
            int sourceIndex = Math.floorMod((int) (propertyId * 7 + imageIndex), localImages.size());
            image = resized(localImages.get(sourceIndex));
        }
        return image;
    }

    private static BufferedImage generatedImage(long seed, int variant) {
        var image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            var random = new Random(seed);
            Color start = new Color(random.nextInt(160), random.nextInt(160), random.nextInt(160));
            Color end = new Color(95 + random.nextInt(160), 95 + random.nextInt(160), 95 + random.nextInt(160));
            graphics.setPaint(new GradientPaint(0, 0, start, IMAGE_WIDTH, IMAGE_HEIGHT, end));
            graphics.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
            for (int i = 0; i < 30 + variant * 5; i++) {
                graphics.setColor(new Color(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256),
                        70 + random.nextInt(120)
                ));
                int width = 40 + random.nextInt(320);
                int height = 30 + random.nextInt(220);
                graphics.fillRoundRect(
                        random.nextInt(IMAGE_WIDTH),
                        random.nextInt(IMAGE_HEIGHT),
                        width,
                        height,
                        20,
                        20
                );
            }
        } finally {
            graphics.dispose();
        }
        if (variant % 3 == 2) {
            var random = new Random(seed * 31);
            for (int y = 0; y < IMAGE_HEIGHT; y += 2) {
                for (int x = 0; x < IMAGE_WIDTH; x += 2) {
                    int value = random.nextInt(80);
                    int rgb = image.getRGB(x, y);
                    Color original = new Color(rgb);
                    int red = Math.min(255, original.getRed() + value);
                    int green = Math.min(255, original.getGreen() + value / 2);
                    int blue = Math.min(255, original.getBlue() + value / 3);
                    int noisy = new Color(red, green, blue).getRGB();
                    image.setRGB(x, y, noisy);
                }
            }
        }
        return image;
    }

    private static BufferedImage resized(BufferedImage source) {
        double scale = Math.min(
                1.0,
                Math.min((double) IMAGE_WIDTH / source.getWidth(), (double) IMAGE_HEIGHT / source.getHeight())
        );
        int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
        var target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(source, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private static BufferedImage resizedToLongSide(BufferedImage source, int maxLongSide) {
        int sourceLongSide = Math.max(source.getWidth(), source.getHeight());
        if (sourceLongSide <= maxLongSide) {
            return source;
        }
        double scale = (double) maxLongSide / sourceLongSide;
        int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
        var target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(source, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private static byte[] encodeJpeg(BufferedImage image, float quality) {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (var bytes = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), parameters);
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot encode performance image", exception);
        } finally {
            writer.dispose();
        }
    }

    private List<BufferedImage> loadLocalImages() {
        String configured = environment.getProperty("PERF_IMAGE_DIR");
        if (configured == null || configured.isBlank()) {
            return Collections.emptyList();
        }
        Path directory = Path.of(configured).toAbsolutePath().normalize();
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("PERF_IMAGE_DIR is not a directory: " + directory);
        }
        try (var files = Files.list(directory)) {
            List<Path> candidates = files
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
                    })
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
            var images = new ArrayList<BufferedImage>(candidates.size());
            for (Path candidate : candidates) {
                BufferedImage image = ImageIO.read(candidate.toFile());
                if (image != null) {
                    images.add(image);
                }
            }
            if (images.isEmpty()) {
                throw new IllegalArgumentException("PERF_IMAGE_DIR contains no decodable images");
            }
            return images;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read PERF_IMAGE_DIR: " + directory, exception);
        }
    }

    private void writeManifest(
            long seed,
            int scale,
            LocalDate anchorDate,
            Counts counts,
            List<PropertyManifest> properties
    ) {
        Path manifestPath = Path.of(environment.getRequiredProperty("PERF_MANIFEST"))
                .toAbsolutePath()
                .normalize();
        var root = new LinkedHashMap<String, Object>();
        root.put("schemaVersion", 1);
        root.put("seed", seed);
        root.put("scale", scale);
        root.put("anchorDate", anchorDate.toString());
        root.put("localCustomerExternalSubject", "990000001");
        root.put("counts", counts.asMap());
        root.put("rentalProperties", properties);
        try {
            Files.createDirectories(manifestPath.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(manifestPath.toFile(), root);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write performance manifest: " + manifestPath, exception);
        }
    }

    private int integerSetting(String name, int defaultValue, int min, int max) {
        String raw = environment.getProperty(name);
        int value = raw == null || raw.isBlank() ? defaultValue : Integer.parseInt(raw);
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value;
    }

    private long longSetting(String name, long defaultValue) {
        String raw = environment.getProperty(name);
        return raw == null || raw.isBlank() ? defaultValue : Long.parseLong(raw);
    }

    private static String sha256(byte[] content) {
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static String sqlLiteral(String value) {
        return value.replace("'", "''");
    }

    private record SeedResult(List<PropertyManifest> properties) {
    }

    private record PropertyManifest(
            long id,
            String slug,
            List<String> mediaUrls,
            List<String> cardUrls,
            List<String> thumbnailUrls,
            List<String> imageBurstUrls
    ) {
    }

    private record TransferConfiguration(
            long airportId,
            String airportCode,
            String airportNameRu,
            String airportNameEn,
            long vehicleId,
            String vehicleCode,
            String vehicleNameRu,
            String vehicleNameEn
    ) {
    }

    private record Counts(
            int customers,
            int cleaningOrders,
            int rentalProperties,
            int rentalBookings,
            int transferBookings,
            int notifications,
            int reminders,
            int supportCases
    ) {

        private static final int IMAGES_PER_PROPERTY = 6;

        static Counts scaled(int scale) {
            return new Counts(
                    100 * scale,
                    500 * scale,
                    20 * scale,
                    200 * scale,
                    300 * scale,
                    1_000 * scale,
                    150 * scale,
                    75 * scale
            );
        }

        Map<String, Integer> asMap() {
            var values = new LinkedHashMap<String, Integer>();
            values.put("customers", customers);
            values.put("cleaningOrders", cleaningOrders);
            values.put("rentalProperties", rentalProperties);
            values.put("rentalImages", rentalProperties * IMAGES_PER_PROPERTY);
            values.put("rentalBookings", rentalBookings);
            values.put("transferBookings", transferBookings);
            values.put("notifications", notifications);
            values.put("reminders", reminders);
            values.put("supportCases", supportCases);
            return values;
        }
    }
}
