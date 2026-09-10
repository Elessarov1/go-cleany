# loco_place_api.model.RentalBooking

## Load the model package
```dart
import 'package:loco_place_api/api.dart';
```

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** |  | 
**property** | [**RentalBookingProperty**](RentalBookingProperty.md) |  | 
**termType** | [**RentalTermType**](RentalTermType.md) |  | 
**checkInDate** | [**Date**](Date.md) |  | 
**checkOutDate** | [**Date**](Date.md) |  | 
**rentalMonths** | **int** |  | 
**durationDays** | **int** |  | 
**customerName** | **String** |  | 
**phone** | **String** |  | 
**guests** | **int** |  | 
**comment** | **String** |  | 
**baseDailyPriceSnapshot** | **num** |  | 
**baseMonthlyPriceSnapshot** | **num** |  | 
**monthlyPriceSnapshot** | **num** |  | 
**longTermDiscountRateSnapshot** | **num** |  | 
**discountAmount** | **num** |  | 
**totalPrice** | **num** |  | 
**currency** | **String** |  | 
**status** | **String** |  | 
**createdAt** | [**DateTime**](DateTime.md) |  | 
**cancelledAt** | [**DateTime**](DateTime.md) |  | 
**cancellationReason** | **String** |  | 
**completedAt** | [**DateTime**](DateTime.md) |  | 
**money** | [**Money**](Money.md) |  | 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


