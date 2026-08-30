package com.cleany.order;

public record CleaningRepeatPrefillResponse(
        long sourceOrderId,
        ServiceArea area,
        String address,
        ApartmentType apartmentType,
        boolean duplex,
        CleaningType cleaningType
) {

    static CleaningRepeatPrefillResponse from(CleaningOrder order) {
        return new CleaningRepeatPrefillResponse(
                order.getId(),
                order.getArea(),
                order.getAddress(),
                order.getApartmentType(),
                order.isDuplex(),
                order.getCleaningType()
        );
    }
}
