# loco_place_api.api.PlatformApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getPlatformServices**](PlatformApi.md#getplatformservices) | **GET** /api/v1/catalog/services | 


# **getPlatformServices**
> BuiltList<PlatformServiceState> getPlatformServices()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getPlatformApi();

try {
    final response = api.getPlatformServices();
    print(response);
} on DioException catch (e) {
    print('Exception when calling PlatformApi->getPlatformServices: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**BuiltList&lt;PlatformServiceState&gt;**](PlatformServiceState.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

