# loco_place_api.api.AccountApi

## Load the API package
```dart
import 'package:loco_place_api/api.dart';
```

All URIs are relative to *https://loco-place.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**confirmAccountDeletion**](AccountApi.md#confirmaccountdeletion) | **POST** /api/v1/account/deletion-requests/{id}/confirm | 
[**confirmIdentityLink**](AccountApi.md#confirmidentitylink) | **POST** /api/v1/account/identity-links/{id}/confirm | 
[**createAccountDeletionRequest**](AccountApi.md#createaccountdeletionrequest) | **POST** /api/v1/account/deletion-requests | 
[**createIdentityLink**](AccountApi.md#createidentitylink) | **POST** /api/v1/account/identity-links | 
[**getAccountIdentities**](AccountApi.md#getaccountidentities) | **GET** /api/v1/account/identities | 
[**getCustomerActivity**](AccountApi.md#getcustomeractivity) | **GET** /api/v1/account/activity | 
[**getCustomerHome**](AccountApi.md#getcustomerhome) | **GET** /api/v1/account/home | 
[**getCustomerProfile**](AccountApi.md#getcustomerprofile) | **GET** /api/v1/customers/me | 
[**getNotificationPreferences**](AccountApi.md#getnotificationpreferences) | **GET** /api/v1/account/notification-preferences | 
[**getNotificationUnreadCount**](AccountApi.md#getnotificationunreadcount) | **GET** /api/v1/account/notifications/unread-count | 
[**getNotifications**](AccountApi.md#getnotifications) | **GET** /api/v1/account/notifications | 
[**markAllNotificationsRead**](AccountApi.md#markallnotificationsread) | **POST** /api/v1/account/notifications/read-all | 
[**markNotificationRead**](AccountApi.md#marknotificationread) | **POST** /api/v1/account/notifications/{notificationId}/read | 
[**registerCommunicationEndpoint**](AccountApi.md#registercommunicationendpoint) | **POST** /api/v1/account/communication-endpoints | 
[**unlinkIdentity**](AccountApi.md#unlinkidentity) | **DELETE** /api/v1/account/identities/{identityId} | 
[**unregisterCommunicationEndpoint**](AccountApi.md#unregistercommunicationendpoint) | **DELETE** /api/v1/account/communication-endpoints | 
[**unregisterCommunicationEndpointById**](AccountApi.md#unregistercommunicationendpointbyid) | **DELETE** /api/v1/account/communication-endpoints/{id} | 
[**updateNotificationPreferences**](AccountApi.md#updatenotificationpreferences) | **PATCH** /api/v1/account/notification-preferences | 
[**verifyIdentityLink**](AccountApi.md#verifyidentitylink) | **POST** /api/v1/account/identity-links/{id}/verify | 


# **confirmAccountDeletion**
> confirmAccountDeletion(id, sensitiveProof)



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

final api = LocoPlaceApi().getAccountApi();
final String id = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 
final SensitiveProof sensitiveProof = ; // SensitiveProof | 

try {
    api.confirmAccountDeletion(id, sensitiveProof);
} on DioException catch (e) {
    print('Exception when calling AccountApi->confirmAccountDeletion: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  | 
 **sensitiveProof** | [**SensitiveProof**](SensitiveProof.md)|  | 

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **confirmIdentityLink**
> AccountIdentities confirmIdentityLink(id, confirmIdentityLinkRequest)



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

final api = LocoPlaceApi().getAccountApi();
final String id = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 
final ConfirmIdentityLinkRequest confirmIdentityLinkRequest = ; // ConfirmIdentityLinkRequest | 

try {
    final response = api.confirmIdentityLink(id, confirmIdentityLinkRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->confirmIdentityLink: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  | 
 **confirmIdentityLinkRequest** | [**ConfirmIdentityLinkRequest**](ConfirmIdentityLinkRequest.md)|  | 

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createAccountDeletionRequest**
> ReauthenticationChallenge createAccountDeletionRequest()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.createAccountDeletionRequest();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->createAccountDeletionRequest: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ReauthenticationChallenge**](ReauthenticationChallenge.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createIdentityLink**
> IdentityLinkAttempt createIdentityLink(createIdentityLinkRequest)



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

final api = LocoPlaceApi().getAccountApi();
final CreateIdentityLinkRequest createIdentityLinkRequest = ; // CreateIdentityLinkRequest | 

try {
    final response = api.createIdentityLink(createIdentityLinkRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->createIdentityLink: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createIdentityLinkRequest** | [**CreateIdentityLinkRequest**](CreateIdentityLinkRequest.md)|  | 

### Return type

[**IdentityLinkAttempt**](IdentityLinkAttempt.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAccountIdentities**
> AccountIdentities getAccountIdentities()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getAccountIdentities();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getAccountIdentities: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCustomerActivity**
> CustomerActivity getCustomerActivity()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getCustomerActivity();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getCustomerActivity: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CustomerActivity**](CustomerActivity.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCustomerHome**
> CustomerHome getCustomerHome()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getCustomerHome();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getCustomerHome: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CustomerHome**](CustomerHome.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCustomerProfile**
> CustomerProfile getCustomerProfile()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getCustomerProfile();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getCustomerProfile: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CustomerProfile**](CustomerProfile.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getNotificationPreferences**
> NotificationPreferences getNotificationPreferences()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getNotificationPreferences();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getNotificationPreferences: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getNotificationUnreadCount**
> NotificationUnreadCount getNotificationUnreadCount()



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

final api = LocoPlaceApi().getAccountApi();

try {
    final response = api.getNotificationUnreadCount();
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getNotificationUnreadCount: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**NotificationUnreadCount**](NotificationUnreadCount.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getNotifications**
> NotificationCursorPage getNotifications(cursor, size)



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

final api = LocoPlaceApi().getAccountApi();
final String cursor = cursor_example; // String | 
final int size = 56; // int | 

try {
    final response = api.getNotifications(cursor, size);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->getNotifications: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **cursor** | **String**|  | [optional] 
 **size** | **int**|  | [optional] [default to 20]

### Return type

[**NotificationCursorPage**](NotificationCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **markAllNotificationsRead**
> markAllNotificationsRead()



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

final api = LocoPlaceApi().getAccountApi();

try {
    api.markAllNotificationsRead();
} on DioException catch (e) {
    print('Exception when calling AccountApi->markAllNotificationsRead: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **markNotificationRead**
> markNotificationRead(notificationId)



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

final api = LocoPlaceApi().getAccountApi();
final int notificationId = 789; // int | 

try {
    api.markNotificationRead(notificationId);
} on DioException catch (e) {
    print('Exception when calling AccountApi->markNotificationRead: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **notificationId** | **int**|  | 

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **registerCommunicationEndpoint**
> CommunicationEndpoint registerCommunicationEndpoint(registerCommunicationEndpointRequest)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAccountApi();
final RegisterCommunicationEndpointRequest registerCommunicationEndpointRequest = ; // RegisterCommunicationEndpointRequest | 

try {
    final response = api.registerCommunicationEndpoint(registerCommunicationEndpointRequest);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->registerCommunicationEndpoint: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **registerCommunicationEndpointRequest** | [**RegisterCommunicationEndpointRequest**](RegisterCommunicationEndpointRequest.md)|  | 

### Return type

[**CommunicationEndpoint**](CommunicationEndpoint.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **unlinkIdentity**
> AccountIdentities unlinkIdentity(identityId, sensitiveProof)



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

final api = LocoPlaceApi().getAccountApi();
final int identityId = 789; // int | 
final SensitiveProof sensitiveProof = ; // SensitiveProof | 

try {
    final response = api.unlinkIdentity(identityId, sensitiveProof);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->unlinkIdentity: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **identityId** | **int**|  | 
 **sensitiveProof** | [**SensitiveProof**](SensitiveProof.md)|  | 

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **unregisterCommunicationEndpoint**
> unregisterCommunicationEndpoint()



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAccountApi();

try {
    api.unregisterCommunicationEndpoint();
} on DioException catch (e) {
    print('Exception when calling AccountApi->unregisterCommunicationEndpoint: $e\n');
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **unregisterCommunicationEndpointById**
> unregisterCommunicationEndpointById(id)



### Example
```dart
import 'package:loco_place_api/api.dart';

final api = LocoPlaceApi().getAccountApi();
final String id = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 

try {
    api.unregisterCommunicationEndpointById(id);
} on DioException catch (e) {
    print('Exception when calling AccountApi->unregisterCommunicationEndpointById: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateNotificationPreferences**
> NotificationPreferences updateNotificationPreferences(notificationPreferences)



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

final api = LocoPlaceApi().getAccountApi();
final NotificationPreferences notificationPreferences = ; // NotificationPreferences | 

try {
    final response = api.updateNotificationPreferences(notificationPreferences);
    print(response);
} on DioException catch (e) {
    print('Exception when calling AccountApi->updateNotificationPreferences: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **notificationPreferences** | [**NotificationPreferences**](NotificationPreferences.md)|  | 

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **verifyIdentityLink**
> verifyIdentityLink(id, verifyIdentityLinkRequest)



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

final api = LocoPlaceApi().getAccountApi();
final String id = 38400000-8cf0-11bd-b23e-10b96e4ef00d; // String | 
final VerifyIdentityLinkRequest verifyIdentityLinkRequest = ; // VerifyIdentityLinkRequest | 

try {
    api.verifyIdentityLink(id, verifyIdentityLinkRequest);
} on DioException catch (e) {
    print('Exception when calling AccountApi->verifyIdentityLink: $e\n');
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  | 
 **verifyIdentityLinkRequest** | [**VerifyIdentityLinkRequest**](VerifyIdentityLinkRequest.md)|  | 

### Return type

void (empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

