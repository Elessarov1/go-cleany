# loco_place_api.api.TransferApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelTransferBooking**](TransferApi.md#canceltransferbooking) | **POST** /api/v1/transfer/bookings/{bookingId}/cancel | 
[**createTransferBooking**](TransferApi.md#createtransferbooking) | **POST** /api/v1/transfer/bookings | 
[**getTransferBooking**](TransferApi.md#gettransferbooking) | **GET** /api/v1/transfer/bookings/{bookingId} | 
[**getTransferBookings**](TransferApi.md#gettransferbookings) | **GET** /api/v1/transfer/bookings | 
[**getTransferConfiguration**](TransferApi.md#gettransferconfiguration) | **GET** /api/v1/transfer/configuration | 
[**getTransferRepeatPrefill**](TransferApi.md#gettransferrepeatprefill) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-prefill | 
[**quoteTransfer**](TransferApi.md#quotetransfer) | **POST** /api/v1/transfer/quote | 
[**recordTransferRepeatShown**](TransferApi.md#recordtransferrepeatshown) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-shown | 


# **cancelTransferBooking**
> TransferBooking cancelTransferBooking(bookingId)



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

final api = LocoPlaceApi().getTransferApi();
final int bookingId = 789; // int | 

try {
    final response = api.cancelTransferBooking(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->cancelTransferBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createTransferBooking**
> TransferBooking createTransferBooking(idempotencyKey, createTransferBookingRequest)



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

final api = LocoPlaceApi().getTransferApi();
final String idempotencyKey = idempotencyKey_example; // String | 
final CreateTransferBookingRequest createTransferBookingRequest = ; // CreateTransferBookingRequest | 

try {
    final response = api.createTransferBooking(idempotencyKey, createTransferBookingRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->createTransferBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **idempotencyKey** | **String**|  | 
 **createTransferBookingRequest** | [**CreateTransferBookingRequest**](CreateTransferBookingRequest.md)|  | 

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransferBooking**
> TransferBooking getTransferBooking(bookingId)



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

final api = LocoPlaceApi().getTransferApi();
final int bookingId = 789; // int | 

try {
    final response = api.getTransferBooking(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->getTransferBooking: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransferBookings**
> TransferBookingCursorPage getTransferBookings(cursor, size)



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

final api = LocoPlaceApi().getTransferApi();
final String cursor = cursor_example; // String | 
final int size = 56; // int | 

try {
    final response = api.getTransferBookings(cursor, size);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->getTransferBookings: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **cursor** | **String**|  | [optional] 
 **size** | **int**|  | [optional] [default to 20]

### Return type

[**TransferBookingCursorPage**](TransferBookingCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransferConfiguration**
> TransferConfiguration getTransferConfiguration()



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

final api = LocoPlaceApi().getTransferApi();

try {
    final response = api.getTransferConfiguration();
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->getTransferConfiguration: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**TransferConfiguration**](TransferConfiguration.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransferRepeatPrefill**
> TransferRepeatPrefill getTransferRepeatPrefill(bookingId)



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

final api = LocoPlaceApi().getTransferApi();
final int bookingId = 789; // int | 

try {
    final response = api.getTransferRepeatPrefill(bookingId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->getTransferRepeatPrefill: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

[**TransferRepeatPrefill**](TransferRepeatPrefill.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **quoteTransfer**
> TransferQuote quoteTransfer(transferQuoteRequest)



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

final api = LocoPlaceApi().getTransferApi();
final TransferQuoteRequest transferQuoteRequest = ; // TransferQuoteRequest | 

try {
    final response = api.quoteTransfer(transferQuoteRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling TransferApi->quoteTransfer: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **transferQuoteRequest** | [**TransferQuoteRequest**](TransferQuoteRequest.md)|  | 

### Return type

[**TransferQuote**](TransferQuote.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **recordTransferRepeatShown**
> recordTransferRepeatShown(bookingId)



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

final api = LocoPlaceApi().getTransferApi();
final int bookingId = 789; // int | 

try {
    api.recordTransferRepeatShown(bookingId);
} on DioException catch (e) {
    print('Exception when calling TransferApi->recordTransferRepeatShown: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bookingId** | **int**|  | 

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

