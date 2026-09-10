# loco_place_api.api.SupportApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createSupportCase**](SupportApi.md#createsupportcase) | **POST** /api/v1/account/support/cases | 
[**getTransactionSupport**](SupportApi.md#gettransactionsupport) | **GET** /api/v1/account/support/sources/{service}/{sourceEntityId} | 
[**submitTransactionFeedback**](SupportApi.md#submittransactionfeedback) | **POST** /api/v1/account/support/feedback | 


# **createSupportCase**
> SupportCase createSupportCase(createSupportCaseRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getSupportApi();
final CreateSupportCaseRequest createSupportCaseRequest = ; // CreateSupportCaseRequest | 

try {
    final response = api.createSupportCase(createSupportCaseRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling SupportApi->createSupportCase: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createSupportCaseRequest** | [**CreateSupportCaseRequest**](CreateSupportCaseRequest.md)|  | 

### Return type

[**SupportCase**](SupportCase.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransactionSupport**
> TransactionSupport getTransactionSupport(service, sourceEntityId)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getSupportApi();
final String service = service_example; // String | 
final int sourceEntityId = 789; // int | 

try {
    final response = api.getTransactionSupport(service, sourceEntityId);
    print(response);
} on DioException catch (e) {
    print('Exception when calling SupportApi->getTransactionSupport: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **service** | **String**|  | 
 **sourceEntityId** | **int**|  | 

### Return type

[**TransactionSupport**](TransactionSupport.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **submitTransactionFeedback**
> TransactionSupport submitTransactionFeedback(createTransactionFeedbackRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getSupportApi();
final CreateTransactionFeedbackRequest createTransactionFeedbackRequest = ; // CreateTransactionFeedbackRequest | 

try {
    final response = api.submitTransactionFeedback(createTransactionFeedbackRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling SupportApi->submitTransactionFeedback: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createTransactionFeedbackRequest** | [**CreateTransactionFeedbackRequest**](CreateTransactionFeedbackRequest.md)|  | 

### Return type

[**TransactionSupport**](TransactionSupport.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

