package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransferBookingTest {

    private static final Instant NOW = Instant.parse("2026-08-29T12:00:00Z");

    @Test
    void bookingStartsRequestedAndKeepsConfigurationAndPriceSnapshots() {
        TransferAirport airport = airport(true);
        TransferVehicleType vehicle = vehicle(true);
        TransferPrice price = price(airport, vehicle, true, "3200.00");
        TransferBooking booking = booking(
                TransferDirection.TO_AIRPORT,
                airport,
                vehicle,
                price,
                3,
                3,
                null,
                null
        );

        airport.update("Новое имя", "New name", true, 10, NOW.plusSeconds(1));
        vehicle.update("Новый минивэн", "New minivan", 6, 6, true, 10, NOW.plusSeconds(1));
        price.update(new BigDecimal("4000.00"), "TRY", true, NOW.plusSeconds(1));

        Assertions.assertAll(
                () -> Assertions.assertEquals(TransferBookingStatus.REQUESTED, booking.getStatus()),
                () -> Assertions.assertEquals("Аэропорт Анталья", booking.getAirportNameRuSnapshot()),
                () -> Assertions.assertEquals("Minivan", booking.getVehicleNameEnSnapshot()),
                () -> Assertions.assertEquals(new BigDecimal("3200.00"), booking.getPriceAmount()),
                () -> Assertions.assertEquals("TRY", booking.getPriceCurrency())
        );
    }

    @Test
    void capacitiesAndFlightRequirementsAreEnforced() {
        TransferAirport airport = airport(true);
        TransferVehicleType vehicle = vehicle(true);
        TransferPrice fromPrice = new TransferPrice(
                airport,
                vehicle,
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("3200.00"),
                "TRY",
                true,
                NOW
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> booking(
                                TransferDirection.FROM_AIRPORT,
                                airport,
                                vehicle,
                                fromPrice,
                                7,
                                1,
                                "TK123",
                                LocalTime.of(3, 30)
                        )
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> booking(
                                TransferDirection.FROM_AIRPORT,
                                airport,
                                vehicle,
                                fromPrice,
                                2,
                                7,
                                "TK123",
                                LocalTime.of(3, 30)
                        )
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> booking(
                                TransferDirection.FROM_AIRPORT,
                                airport,
                                vehicle,
                                fromPrice,
                                2,
                                2,
                                null,
                                null
                        )
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> booking(
                                TransferDirection.TO_AIRPORT,
                                airport,
                                vehicle,
                                price(airport, vehicle, true, "3000.00"),
                                2,
                                2,
                                null,
                                null
                        )
                )
        );
    }

    @Test
    void inactiveConfigurationAndIllegalTransitionsAreRejected() {
        TransferAirport airport = airport(false);
        TransferVehicleType vehicle = vehicle(true);
        TransferPrice price = price(airport, vehicle, true, "3200.00");

        Assertions.assertThrows(
                InvalidTransferBookingException.class,
                () -> booking(
                        TransferDirection.TO_AIRPORT,
                        airport,
                        vehicle,
                        price,
                        2,
                        1,
                        null,
                        null
                )
        );

        TransferAirport enabledAirport = airport(true);
        TransferBooking booking = booking(
                TransferDirection.TO_AIRPORT,
                enabledAirport,
                vehicle,
                price(enabledAirport, vehicle, true, "3200.00"),
                2,
                1,
                null,
                null
        );
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        TransferBookingStateException.class,
                        () -> booking.complete(true, NOW)
                ),
                () -> Assertions.assertThrows(
                        TransferBookingStateException.class,
                        () -> booking.cancelByCustomer(true, NOW)
                )
        );
    }

    @Test
    void disabledDriverCannotBeAssignedAndCompletionRequiresPickupToStart() {
        TransferAirport airport = airport(true);
        TransferVehicleType vehicle = vehicle(true);
        TransferBooking booking = booking(
                TransferDirection.TO_AIRPORT,
                airport,
                vehicle,
                price(airport, vehicle, true, "3200.00"),
                2,
                1,
                null,
                null
        );
        TransferDriver disabled = new TransferDriver("Driver", "+905551112233", false, null, NOW);
        TransferDriver enabled = new TransferDriver("Driver", "+905551112233", true, null, NOW);

        Assertions.assertThrows(
                TransferBookingStateException.class,
                () -> booking.assignDriver(disabled, NOW)
        );
        booking.assignDriver(enabled, NOW);

        Assertions.assertAll(
                () -> Assertions.assertEquals(TransferBookingStatus.CONFIRMED, booking.getStatus()),
                () -> Assertions.assertThrows(
                        TransferBookingStateException.class,
                        () -> booking.complete(false, NOW.plusSeconds(1))
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> booking.complete(true, NOW.plusSeconds(1))
                ),
                () -> Assertions.assertEquals(TransferBookingStatus.COMPLETED, booking.getStatus())
        );
    }

    private static TransferBooking booking(
            TransferDirection direction,
            TransferAirport airport,
            TransferVehicleType vehicle,
            TransferPrice price,
            int passengerCount,
            int luggageCount,
            String flightNumber,
            LocalTime arrivalTime
    ) {
        return new TransferBooking(new NewTransferBooking(
                1,
                2,
                "Alex",
                "+905551112233",
                direction,
                airport,
                vehicle,
                LocalDate.of(2026, 8, 31),
                LocalTime.of(3, 30),
                "Kestel, Alanya",
                passengerCount,
                luggageCount,
                flightNumber,
                arrivalTime,
                null,
                price,
                TransferPriceQuote.standard(price),
                NOW
        ));
    }

    private static TransferAirport airport(boolean enabled) {
        return new TransferAirport(
                "AYT",
                "Аэропорт Анталья",
                "Antalya Airport",
                enabled,
                10,
                NOW
        );
    }

    private static TransferVehicleType vehicle(boolean enabled) {
        return new TransferVehicleType(
                "MINIVAN",
                "Минивэн",
                "Minivan",
                6,
                6,
                enabled,
                10,
                NOW
        );
    }

    private static TransferPrice price(
            TransferAirport airport,
            TransferVehicleType vehicle,
            boolean enabled,
            String amount
    ) {
        return new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal(amount),
                "TRY",
                enabled,
                NOW
        );
    }
}
