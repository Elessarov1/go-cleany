# loco_place_api.model.CleaningOrder

## Load the model package
```dart
import 'package:loco_place_api/api.dart';
```

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** |  | 
**communicationIdentityId** | **int** |  | 
**customerName** | **String** |  | 
**phone** | **String** |  | 
**area** | **String** |  | 
**address** | **String** |  | 
**apartmentType** | **String** |  | 
**duplex** | **bool** |  | 
**cleaningType** | **String** |  | 
**price** | **num** |  | 
**basePrice** | **num** |  | 
**customerDiscount** | **num** |  | 
**finalCustomerPrice** | **num** |  | 
**customerDiscountType** | **String** |  | 
**currency** | **String** |  | 
**requestedDate** | [**Date**](Date.md) |  | 
**customerComment** | **String** |  | [optional] 
**cleanerComment** | **String** |  | [optional] 
**cleanerTelegramUserId** | **int** |  | [optional] 
**status** | **String** |  | 
**createdAt** | [**DateTime**](DateTime.md) |  | 
**acceptedAt** | [**DateTime**](DateTime.md) |  | [optional] 
**completedAt** | [**DateTime**](DateTime.md) |  | [optional] 
**report** | [**CleaningReport**](CleaningReport.md) |  | [optional] 
**money** | [**Money**](Money.md) |  | 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


