# loco_place_api.model.CreateRentalBookingRequest

## Load the model package
```dart
import 'package:loco_place_api/api.dart';
```

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**propertyId** | **int** |  | 
**termType** | [**RentalTermType**](RentalTermType.md) |  | 
**checkInDate** | [**Date**](Date.md) |  | 
**checkOutDate** | [**Date**](Date.md) |  | [optional] 
**months** | **int** |  | [optional] 
**guests** | **int** |  | 
**phone** | **String** |  | 
**comment** | **String** |  | [optional] 
**expectedTotalPrice** | **num** | Legacy optimistic-price input; response money always uses the Money object. | 
**expectedCurrency** | **String** |  | 
**searchExecutionId** | **String** |  | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


