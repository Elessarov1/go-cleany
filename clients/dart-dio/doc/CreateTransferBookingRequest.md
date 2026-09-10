# loco_place_api.model.CreateTransferBookingRequest

## Load the model package
```dart
import 'package:loco_place_api/api.dart';
```

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**direction** | **String** |  | 
**airportId** | **int** |  | 
**vehicleTypeId** | **int** |  | 
**pickupDate** | [**Date**](Date.md) |  | 
**pickupTime** | **String** |  | 
**address** | **String** |  | 
**passengerCount** | **int** |  | 
**luggageCount** | **int** |  | 
**flightNumber** | **String** |  | [optional] 
**scheduledArrivalTime** | **String** |  | [optional] 
**phone** | **String** |  | 
**comment** | **String** |  | [optional] 
**repeatFromBookingId** | **int** |  | [optional] 
**rentalSource** | [**RentalTransferSourceRequest**](RentalTransferSourceRequest.md) |  | [optional] 
**benefit** | **String** |  | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


