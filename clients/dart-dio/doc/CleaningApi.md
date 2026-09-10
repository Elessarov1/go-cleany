# loco_place_api.api.CleaningApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelCleaningOrder**](CleaningApi.md#cancelcleaningorder) | **POST** /api/v1/cleaning/orders/{orderId}/cancel | 
[**createCleaningOrder**](CleaningApi.md#createcleaningorder) | **POST** /api/v1/cleaning/orders | 
[**getCleaningConfiguration**](CleaningApi.md#getcleaningconfiguration) | **GET** /api/v1/cleaning/configuration | 
[**getCleaningOrder**](CleaningApi.md#getcleaningorder) | **GET** /api/v1/cleaning/orders/{orderId} | 
[**getCleaningOrders**](CleaningApi.md#getcleaningorders) | **GET** /api/v1/cleaning/orders | 
[**getCleaningReferralSummary**](CleaningApi.md#getcleaningreferralsummary) | **GET** /api/v1/referrals/me | 
[**getCleaningRepeatPrefill**](CleaningApi.md#getcleaningrepeatprefill) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-prefill | 
[**getCleaningRepeatReminder**](CleaningApi.md#getcleaningrepeatreminder) | **GET** /api/v1/cleaning/orders/{orderId}/repeat-reminder | 
[**getCleaningReport**](CleaningApi.md#getcleaningreport) | **GET** /api/v1/cleaning/orders/{orderId}/report | 
[**getCleaningReportPhoto**](CleaningApi.md#getcleaningreportphoto) | **GET** /api/v1/cleaning/orders/{orderId}/report/photos/{mediaId} | 
[**quoteCleaningOrder**](CleaningApi.md#quotecleaningorder) | **POST** /api/v1/cleaning/orders/quote | 
[**recordCleaningRepeatShown**](CleaningApi.md#recordcleaningrepeatshown) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-shown | 
[**updateCleaningRepeatReminder**](CleaningApi.md#updatecleaningrepeatreminder) | **PUT** /api/v1/cleaning/orders/{orderId}/repeat-reminder | 


# **cancelCleaningOrder**
> CleaningOrder cancelCleaningOrder(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    final response = api.cancelCleaningOrder(orderId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->cancelCleaningOrder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createCleaningOrder**
> CleaningOrder createCleaningOrder(idempotencyKey, createCleaningOrderRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final String idempotencyKey = idempotencyKey_example; // String | 
final CreateCleaningOrderRequest createCleaningOrderRequest = ; // CreateCleaningOrderRequest | 

try {
    final response = api.createCleaningOrder(idempotencyKey, createCleaningOrderRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->createCleaningOrder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **idempotencyKey** | **String**|  | 
 **createCleaningOrderRequest** | [**CreateCleaningOrderRequest**](CreateCleaningOrderRequest.md)|  | 

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningConfiguration**
> CleaningConfiguration getCleaningConfiguration()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getCleaningApi();

try {
    final response = api.getCleaningConfiguration();
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningConfiguration: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CleaningConfiguration**](CleaningConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningOrder**
> CleaningOrder getCleaningOrder(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    final response = api.getCleaningOrder(orderId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningOrder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningOrders**
> CleaningOrderCursorPage getCleaningOrders(cursor, size)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final String cursor = cursor_example; // String | 
final int size = 56; // int | 

try {
    final response = api.getCleaningOrders(cursor, size);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningOrders: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **cursor** | **String**|  | [optional] 
 **size** | **int**|  | [optional] [default to 20]

### Return type

[**CleaningOrderCursorPage**](CleaningOrderCursorPage.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningReferralSummary**
> ReferralSummary getCleaningReferralSummary()



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();

try {
    final response = api.getCleaningReferralSummary();
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningReferralSummary: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ReferralSummary**](ReferralSummary.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningRepeatPrefill**
> CleaningRepeatPrefill getCleaningRepeatPrefill(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    final response = api.getCleaningRepeatPrefill(orderId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningRepeatPrefill: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

[**CleaningRepeatPrefill**](CleaningRepeatPrefill.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningRepeatReminder**
> CleaningRepeatReminder getCleaningRepeatReminder(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    final response = api.getCleaningRepeatReminder(orderId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningRepeatReminder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

[**CleaningRepeatReminder**](CleaningRepeatReminder.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningReport**
> CleaningReport getCleaningReport(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    final response = api.getCleaningReport(orderId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningReport: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

[**CleaningReport**](CleaningReport.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCleaningReportPhoto**
> Uint8List getCleaningReportPhoto(orderId, mediaId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 
final int mediaId = 789; // int | 

try {
    final response = api.getCleaningReportPhoto(orderId, mediaId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->getCleaningReportPhoto: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 
 **mediaId** | **int**|  | 

### Return type

[**Uint8List**](Uint8List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: image/*, application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **quoteCleaningOrder**
> CleaningOrderQuote quoteCleaningOrder(cleaningOrderQuoteRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final CleaningOrderQuoteRequest cleaningOrderQuoteRequest = ; // CleaningOrderQuoteRequest | 

try {
    final response = api.quoteCleaningOrder(cleaningOrderQuoteRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->quoteCleaningOrder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **cleaningOrderQuoteRequest** | [**CleaningOrderQuoteRequest**](CleaningOrderQuoteRequest.md)|  | 

### Return type

[**CleaningOrderQuote**](CleaningOrderQuote.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **recordCleaningRepeatShown**
> recordCleaningRepeatShown(orderId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 

try {
    api.recordCleaningRepeatShown(orderId);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->recordCleaningRepeatShown: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateCleaningRepeatReminder**
> CleaningRepeatReminder updateCleaningRepeatReminder(orderId, cleaningRepeatReminderRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getCleaningApi();
final int orderId = 789; // int | 
final CleaningRepeatReminderRequest cleaningRepeatReminderRequest = ; // CleaningRepeatReminderRequest | 

try {
    final response = api.updateCleaningRepeatReminder(orderId, cleaningRepeatReminderRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling CleaningApi->updateCleaningRepeatReminder: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **int**|  | 
 **cleaningRepeatReminderRequest** | [**CleaningRepeatReminderRequest**](CleaningRepeatReminderRequest.md)|  | 

### Return type

[**CleaningRepeatReminder**](CleaningRepeatReminder.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

