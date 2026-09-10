# loco_place_api.api.AuthenticationApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createNativeChallenge**](AuthenticationApi.md#createnativechallenge) | **POST** /api/v1/auth/native/challenges | 
[**createTelegramLoginAttempt**](AuthenticationApi.md#createtelegramloginattempt) | **POST** /api/v1/auth/native/telegram/attempts | 
[**createTmaSession**](AuthenticationApi.md#createtmasession) | **POST** /api/v1/auth/tma/session | 
[**exchangeTelegramLoginAttempt**](AuthenticationApi.md#exchangetelegramloginattempt) | **POST** /api/v1/auth/native/telegram/attempts/{attemptId}/exchange | 
[**getCsrfToken**](AuthenticationApi.md#getcsrftoken) | **GET** /api/v1/auth/csrf | 
[**getCurrentAuthentication**](AuthenticationApi.md#getcurrentauthentication) | **GET** /api/v1/auth/me | 
[**loginWithApple**](AuthenticationApi.md#loginwithapple) | **POST** /api/v1/auth/native/apple | 
[**loginWithGoogle**](AuthenticationApi.md#loginwithgoogle) | **POST** /api/v1/auth/native/google | 
[**logoutCurrentSession**](AuthenticationApi.md#logoutcurrentsession) | **DELETE** /api/v1/auth/sessions/current | 
[**refreshSession**](AuthenticationApi.md#refreshsession) | **POST** /api/v1/auth/sessions/refresh | 
[**revokeAllSessions**](AuthenticationApi.md#revokeallsessions) | **DELETE** /api/v1/auth/sessions | 


# **createNativeChallenge**
> NativeChallenge createNativeChallenge(createNativeChallengeRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();
final CreateNativeChallengeRequest createNativeChallengeRequest = ; // CreateNativeChallengeRequest | 

try {
    final response = api.createNativeChallenge(createNativeChallengeRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->createNativeChallenge: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createNativeChallengeRequest** | [**CreateNativeChallengeRequest**](CreateNativeChallengeRequest.md)|  | 

### Return type

[**NativeChallenge**](NativeChallenge.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createTelegramLoginAttempt**
> TelegramLoginAttempt createTelegramLoginAttempt()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    final response = api.createTelegramLoginAttempt();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->createTelegramLoginAttempt: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**TelegramLoginAttempt**](TelegramLoginAttempt.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createTmaSession**
> createTmaSession()



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: tmaBootstrap
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaBootstrap').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('tmaBootstrap').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    api.createTmaSession();
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->createTmaSession: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[tmaBootstrap](../README.md#tmaBootstrap)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **exchangeTelegramLoginAttempt**
> SessionTokens exchangeTelegramLoginAttempt(attemptId, exchangeTelegramLoginAttemptRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();
final String attemptId = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 
final ExchangeTelegramLoginAttemptRequest exchangeTelegramLoginAttemptRequest = ; // ExchangeTelegramLoginAttemptRequest | 

try {
    final response = api.exchangeTelegramLoginAttempt(attemptId, exchangeTelegramLoginAttemptRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->exchangeTelegramLoginAttempt: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **attemptId** | **String**|  | 
 **exchangeTelegramLoginAttemptRequest** | [**ExchangeTelegramLoginAttemptRequest**](ExchangeTelegramLoginAttemptRequest.md)|  | 

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCsrfToken**
> CsrfToken getCsrfToken()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    final response = api.getCsrfToken();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->getCsrfToken: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CsrfToken**](CsrfToken.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCurrentAuthentication**
> CurrentAuthentication getCurrentAuthentication()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    final response = api.getCurrentAuthentication();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->getCurrentAuthentication: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CurrentAuthentication**](CurrentAuthentication.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **loginWithApple**
> SessionTokens loginWithApple(nativeProviderLoginRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();
final NativeProviderLoginRequest nativeProviderLoginRequest = ; // NativeProviderLoginRequest | 

try {
    final response = api.loginWithApple(nativeProviderLoginRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->loginWithApple: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nativeProviderLoginRequest** | [**NativeProviderLoginRequest**](NativeProviderLoginRequest.md)|  | 

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **loginWithGoogle**
> SessionTokens loginWithGoogle(nativeProviderLoginRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();
final NativeProviderLoginRequest nativeProviderLoginRequest = ; // NativeProviderLoginRequest | 

try {
    final response = api.loginWithGoogle(nativeProviderLoginRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->loginWithGoogle: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nativeProviderLoginRequest** | [**NativeProviderLoginRequest**](NativeProviderLoginRequest.md)|  | 

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logoutCurrentSession**
> logoutCurrentSession()



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    api.logoutCurrentSession();
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->logoutCurrentSession: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **refreshSession**
> SessionTokens refreshSession(idempotencyKey, refreshSessionRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAuthenticationApi();
final String idempotencyKey = idempotencyKey_example; // String | 
final RefreshSessionRequest refreshSessionRequest = ; // RefreshSessionRequest | 

try {
    final response = api.refreshSession(idempotencyKey, refreshSessionRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->refreshSession: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **idempotencyKey** | **String**|  | 
 **refreshSessionRequest** | [**RefreshSessionRequest**](RefreshSessionRequest.md)|  | 

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **revokeAllSessions**
> revokeAllSessions()



### Example
```dart
import 'package:loco_place_api/api.dart';
// TODO Configure API key authorization: cookieSession
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKey = 'YOUR_API_KEY';
// uncomment below to setup prefix (e.g. Bearer) for API key, if needed
//defaultApiClient.getAuthentication<ApiKeyAuth>('cookieSession').apiKeyPrefix = 'Bearer';

final api = LocoPlaceApi().getAuthenticationApi();

try {
    api.revokeAllSessions();
} on DioException catch (e) {
    print('Exception when calling AuthenticationApi->revokeAllSessions: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

