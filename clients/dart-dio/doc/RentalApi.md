# loco_place_api.api.RentalApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelRentalBooking**](RentalApi.md#cancelrentalbooking) | **POST** /api/v1/rental/bookings/{bookingId}/cancel | 
[**createRentalBooking**](RentalApi.md#createrentalbooking) | **POST** /api/v1/rental/bookings | 
[**getRentalAvailability**](RentalApi.md#getrentalavailability) | **GET** /api/v1/rental/properties/{propertyId}/availability | 
[**getRentalBooking**](RentalApi.md#getrentalbooking) | **GET** /api/v1/rental/bookings/{bookingId} | 
[**getRentalBookings**](RentalApi.md#getrentalbookings) | **GET** /api/v1/rental/bookings | 
[**getRentalCleaningContext**](RentalApi.md#getrentalcleaningcontext) | **GET** /api/v1/rental/bookings/{bookingId}/cleaning-context | 
[**getRentalConfiguration**](RentalApi.md#getrentalconfiguration) | **GET** /api/v1/rental/configuration | 
[**getRentalProperty**](RentalApi.md#getrentalproperty) | **GET** /api/v1/rental/properties/{slug} | 
[**getRentalPropertyMedia**](RentalApi.md#getrentalpropertymedia) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId} | 
[**getRentalPropertyMediaVariant**](RentalApi.md#getrentalpropertymediavariant) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId}/{variant} | 
[**getRentalTransferContext**](RentalApi.md#getrentaltransfercontext) | **GET** /api/v1/rental/bookings/{bookingId}/transfer-context | 
[**getRentalTransferPrefill**](RentalApi.md#getrentaltransferprefill) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/prefill | 
[**quoteRentalProperty**](RentalApi.md#quoterentalproperty) | **GET** /api/v1/rental/properties/{propertyId}/quote | 
[**recordRentalFirstCard**](RentalApi.md#recordrentalfirstcard) | **POST** /api/v1/rental/searches/{executionId}/first-card | 
[**recordRentalSearchOpened**](RentalApi.md#recordrentalsearchopened) | **POST** /api/v1/rental/searches/{executionId}/opened | 
[**recordRentalTransferContextShown**](RentalApi.md#recordrentaltransfercontextshown) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/shown | 
[**searchRentalProperties**](RentalApi.md#searchrentalproperties) | **GET** /api/v1/rental/search | 


# **cancelRentalBooking**
> RentalBooking cancelRentalBooking(bookingId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 

try {
    final response = api.cancelRentalBooking(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->cancelRentalBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createRentalBooking**
> RentalBooking createRentalBooking(idempotencyKey, createRentalBookingRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final String idempotencyKey = idempotencyKey_example; // String | 
final CreateRentalBookingRequest createRentalBookingRequest = ; // CreateRentalBookingRequest | 

try {
    final response = api.createRentalBooking(idempotencyKey, createRentalBookingRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->createRentalBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **idempotencyKey** | **String**|  | 
 **createRentalBookingRequest** | [**CreateRentalBookingRequest**](CreateRentalBookingRequest.md)|  | 

### Return type

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalAvailability**
> RentalAvailability getRentalAvailability(propertyId, fromDate, toDate)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int propertyId = 789; // int | 
final Date fromDate = 2013-10-20; // Date | 
final Date toDate = 2013-10-20; // Date | 

try {
    final response = api.getRentalAvailability(propertyId, fromDate, toDate);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalAvailability: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **propertyId** | **int**|  | 
 **fromDate** | **Date**|  | 
 **toDate** | **Date**|  | 

### Return type

[**RentalAvailability**](RentalAvailability.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalBooking**
> RentalBooking getRentalBooking(bookingId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 

try {
    final response = api.getRentalBooking(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalBookings**
> RentalBookingCursorPage getRentalBookings(cursor, size)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final String cursor = cursor_example; // String | 
final int size = 56; // int | 

try {
    final response = api.getRentalBookings(cursor, size);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalBookings: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **cursor** | **String**|  | [optional] 
 **size** | **int**|  | [optional] [default to 20]

### Return type

[**RentalBookingCursorPage**](RentalBookingCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalCleaningContext**
> RentalCleaningContext getRentalCleaningContext(bookingId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 

try {
    final response = api.getRentalCleaningContext(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalCleaningContext: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**RentalCleaningContext**](RentalCleaningContext.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalConfiguration**
> RentalConfiguration getRentalConfiguration()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getRentalApi();

try {
    final response = api.getRentalConfiguration();
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalConfiguration: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**RentalConfiguration**](RentalConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalProperty**
> RentalProperty getRentalProperty(slug)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final String slug = slug_example; // String | 

try {
    final response = api.getRentalProperty(slug);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalProperty: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **slug** | **String**|  | 

### Return type

[**RentalProperty**](RentalProperty.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalPropertyMedia**
> Uint8List getRentalPropertyMedia(propertyId, mediaId, v)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getRentalApi();
final int propertyId = 789; // int | 
final int mediaId = 789; // int | 
final int v = 789; // int | 

try {
    final response = api.getRentalPropertyMedia(propertyId, mediaId, v);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalPropertyMedia: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **propertyId** | **int**|  | 
 **mediaId** | **int**|  | 
 **v** | **int**|  | [optional] 

### Return type

[**Uint8List**](Uint8List.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: image/*, application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalPropertyMediaVariant**
> Uint8List getRentalPropertyMediaVariant(propertyId, mediaId, variant, v)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getRentalApi();
final int propertyId = 789; // int | 
final int mediaId = 789; // int | 
final String variant = variant_example; // String | 
final int v = 789; // int | 

try {
    final response = api.getRentalPropertyMediaVariant(propertyId, mediaId, variant, v);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalPropertyMediaVariant: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **propertyId** | **int**|  | 
 **mediaId** | **int**|  | 
 **variant** | **String**|  | 
 **v** | **int**|  | [optional] 

### Return type

[**Uint8List**](Uint8List.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: image/*, application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalTransferContext**
> RentalTransferContext getRentalTransferContext(bookingId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 

try {
    final response = api.getRentalTransferContext(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalTransferContext: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**RentalTransferContext**](RentalTransferContext.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getRentalTransferPrefill**
> RentalTransferPrefill getRentalTransferPrefill(bookingId, context)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 
final String context = context_example; // String | 

try {
    final response = api.getRentalTransferPrefill(bookingId, context);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->getRentalTransferPrefill: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 
 **context** | **String**|  | 

### Return type

[**RentalTransferPrefill**](RentalTransferPrefill.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **quoteRentalProperty**
> RentalQuote quoteRentalProperty(propertyId, termType, checkInDate, guests, checkOutDate, months)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int propertyId = 789; // int | 
final RentalTermType termType = ; // RentalTermType | 
final Date checkInDate = 2013-10-20; // Date | 
final int guests = 56; // int | 
final Date checkOutDate = 2013-10-20; // Date | 
final int months = 56; // int | 

try {
    final response = api.quoteRentalProperty(propertyId, termType, checkInDate, guests, checkOutDate, months);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->quoteRentalProperty: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **propertyId** | **int**|  | 
 **termType** | [**RentalTermType**](.md)|  | 
 **checkInDate** | **Date**|  | 
 **guests** | **int**|  | 
 **checkOutDate** | **Date**|  | [optional] 
 **months** | **int**|  | [optional] 

### Return type

[**RentalQuote**](RentalQuote.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **recordRentalFirstCard**
> recordRentalFirstCard(executionId, rentalFirstCardRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getRentalApi();
final String executionId = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 
final RentalFirstCardRequest rentalFirstCardRequest = ; // RentalFirstCardRequest | 

try {
    api.recordRentalFirstCard(executionId, rentalFirstCardRequest);
} on DioException catch (e) {
    print('Exception when calling RentalApi->recordRentalFirstCard: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **executionId** | **String**|  | 
 **rentalFirstCardRequest** | [**RentalFirstCardRequest**](RentalFirstCardRequest.md)|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **recordRentalSearchOpened**
> recordRentalSearchOpened(executionId)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getRentalApi();
final String executionId = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 

try {
    api.recordRentalSearchOpened(executionId);
} on DioException catch (e) {
    print('Exception when calling RentalApi->recordRentalSearchOpened: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **executionId** | **String**|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **recordRentalTransferContextShown**
> recordRentalTransferContextShown(bookingId, context)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final int bookingId = 789; // int | 
final String context = context_example; // String | 

try {
    api.recordRentalTransferContextShown(bookingId, context);
} on DioException catch (e) {
    print('Exception when calling RentalApi->recordRentalTransferContextShown: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 
 **context** | **String**|  | 

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **searchRentalProperties**
> RentalSearch searchRentalProperties(termType, checkInDate, checkOutDate, months, guests, cursor, size, xRentalPreviousSearchId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaAuth
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaAuth').apiKeyPrefix = 'Bearer';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getRentalApi();
final RentalTermType termType = ; // RentalTermType | 
final Date checkInDate = 2013-10-20; // Date | 
final Date checkOutDate = 2013-10-20; // Date | 
final int months = 56; // int | 
final int guests = 56; // int | 
final String cursor = cursor_example; // String | 
final int size = 56; // int | 
final String xRentalPreviousSearchId = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 

try {
    final response = api.searchRentalProperties(termType, checkInDate, checkOutDate, months, guests, cursor, size, xRentalPreviousSearchId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling RentalApi->searchRentalProperties: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **termType** | [**RentalTermType**](.md)|  | [optional] 
 **checkInDate** | **Date**|  | [optional] 
 **checkOutDate** | **Date**|  | [optional] 
 **months** | **int**|  | [optional] 
 **guests** | **int**|  | [optional] 
 **cursor** | **String**|  | [optional] 
 **size** | **int**|  | [optional] [default to 20]
 **xRentalPreviousSearchId** | **String**|  | [optional] 

### Return type

[**RentalSearch**](RentalSearch.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

