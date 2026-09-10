# loco_place_api.api.ClientApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**captureTelegramAcquisition**](ClientApi.md#capturetelegramacquisition) | **POST** /api/v1/acquisition/telegram | 
[**getClientConfiguration**](ClientApi.md#getclientconfiguration) | **GET** /api/v1/client/configuration | 


# **captureTelegramAcquisition**
> AcquisitionCapture captureTelegramAcquisition(telegramAcquisitionRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getClientApi();
final TelegramAcquisitionRequest telegramAcquisitionRequest = ; // TelegramAcquisitionRequest | 

try {
    final response = api.captureTelegramAcquisition(telegramAcquisitionRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling ClientApi->captureTelegramAcquisition: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **telegramAcquisitionRequest** | [**TelegramAcquisitionRequest**](TelegramAcquisitionRequest.md)|  | 

### Return type

[**AcquisitionCapture**](AcquisitionCapture.md)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getClientConfiguration**
> ClientConfiguration getClientConfiguration()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getClientApi();

try {
    final response = api.getClientConfiguration();
    print(response);
} on DioException catch (e) {
    print('Exception when calling ClientApi->getClientConfiguration: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ClientConfiguration**](ClientConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

