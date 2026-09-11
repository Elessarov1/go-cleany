# AccountApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**confirmAccountDeletion**](AccountApi.md#confirmaccountdeletion) | **POST** /api/v1/account/deletion-requests/{id}/confirm |  |
| [**confirmIdentityLink**](AccountApi.md#confirmidentitylinkoperation) | **POST** /api/v1/account/identity-links/{id}/confirm |  |
| [**createAccountDeletionRequest**](AccountApi.md#createaccountdeletionrequest) | **POST** /api/v1/account/deletion-requests |  |
| [**createIdentityLink**](AccountApi.md#createidentitylinkoperation) | **POST** /api/v1/account/identity-links |  |
| [**getAccountIdentities**](AccountApi.md#getaccountidentities) | **GET** /api/v1/account/identities |  |
| [**getCustomerActivity**](AccountApi.md#getcustomeractivity) | **GET** /api/v1/account/activity |  |
| [**getCustomerHome**](AccountApi.md#getcustomerhome) | **GET** /api/v1/account/home |  |
| [**getCustomerProfile**](AccountApi.md#getcustomerprofile) | **GET** /api/v1/customers/me |  |
| [**getNotificationPreferences**](AccountApi.md#getnotificationpreferences) | **GET** /api/v1/account/notification-preferences |  |
| [**getNotificationUnreadCount**](AccountApi.md#getnotificationunreadcount) | **GET** /api/v1/account/notifications/unread-count |  |
| [**getNotifications**](AccountApi.md#getnotifications) | **GET** /api/v1/account/notifications |  |
| [**markAllNotificationsRead**](AccountApi.md#markallnotificationsread) | **POST** /api/v1/account/notifications/read-all |  |
| [**markNotificationRead**](AccountApi.md#marknotificationread) | **POST** /api/v1/account/notifications/{notificationId}/read |  |
| [**registerCommunicationEndpoint**](AccountApi.md#registercommunicationendpointoperation) | **POST** /api/v1/account/communication-endpoints |  |
| [**unlinkIdentity**](AccountApi.md#unlinkidentity) | **DELETE** /api/v1/account/identities/{identityId} |  |
| [**unregisterCommunicationEndpoint**](AccountApi.md#unregistercommunicationendpoint) | **DELETE** /api/v1/account/communication-endpoints |  |
| [**unregisterCommunicationEndpointById**](AccountApi.md#unregistercommunicationendpointbyid) | **DELETE** /api/v1/account/communication-endpoints/{id} |  |
| [**updateNotificationPreferences**](AccountApi.md#updatenotificationpreferences) | **PATCH** /api/v1/account/notification-preferences |  |
| [**verifyIdentityLink**](AccountApi.md#verifyidentitylinkoperation) | **POST** /api/v1/account/identity-links/{id}/verify |  |



## confirmAccountDeletion

> confirmAccountDeletion(id, sensitiveProof)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { ConfirmAccountDeletionRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // SensitiveProof
    sensitiveProof: ...,
  } satisfies ConfirmAccountDeletionRequest;

  try {
    const data = await api.confirmAccountDeletion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |
| **sensitiveProof** | [SensitiveProof](SensitiveProof.md) |  | |

### Return type

`void` (Empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Account tombstoned and PII removed |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## confirmIdentityLink

> AccountIdentities confirmIdentityLink(id, confirmIdentityLinkRequest)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { ConfirmIdentityLinkOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // ConfirmIdentityLinkRequest
    confirmIdentityLinkRequest: ...,
  } satisfies ConfirmIdentityLinkOperationRequest;

  try {
    const data = await api.confirmIdentityLink(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |
| **confirmIdentityLinkRequest** | [ConfirmIdentityLinkRequest](ConfirmIdentityLinkRequest.md) |  | |

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Identity linked |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createAccountDeletionRequest

> ReauthenticationChallenge createAccountDeletionRequest()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { CreateAccountDeletionRequestRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.createAccountDeletionRequest();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**ReauthenticationChallenge**](ReauthenticationChallenge.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Fresh-auth challenge |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createIdentityLink

> IdentityLinkAttempt createIdentityLink(createIdentityLinkRequest)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { CreateIdentityLinkOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // CreateIdentityLinkRequest
    createIdentityLinkRequest: ...,
  } satisfies CreateIdentityLinkOperationRequest;

  try {
    const data = await api.createIdentityLink(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createIdentityLinkRequest** | [CreateIdentityLinkRequest](CreateIdentityLinkRequest.md) |  | |

### Return type

[**IdentityLinkAttempt**](IdentityLinkAttempt.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Link challenge |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getAccountIdentities

> AccountIdentities getAccountIdentities()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetAccountIdentitiesRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getAccountIdentities();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Linked login methods |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCustomerActivity

> CustomerActivity getCustomerActivity()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetCustomerActivityRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getCustomerActivity();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**CustomerActivity**](CustomerActivity.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cross-service active tasks and history |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCustomerHome

> CustomerHome getCustomerHome()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetCustomerHomeRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getCustomerHome();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**CustomerHome**](CustomerHome.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Contextual customer home read model |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCustomerProfile

> CustomerProfile getCustomerProfile()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetCustomerProfileRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getCustomerProfile();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**CustomerProfile**](CustomerProfile.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current customer profile |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getNotificationPreferences

> NotificationPreferences getNotificationPreferences()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetNotificationPreferencesRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getNotificationPreferences();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Preferences |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getNotificationUnreadCount

> NotificationUnreadCount getNotificationUnreadCount()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetNotificationUnreadCountRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.getNotificationUnreadCount();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**NotificationUnreadCount**](NotificationUnreadCount.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current unread notification count |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getNotifications

> NotificationCursorPage getNotifications(cursor, size)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { GetNotificationsRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // string (optional)
    cursor: cursor_example,
    // number (optional)
    size: 56,
  } satisfies GetNotificationsRequest;

  try {
    const data = await api.getNotifications(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **cursor** | `string` |  | [Optional] [Defaults to `undefined`] |
| **size** | `number` |  | [Optional] [Defaults to `20`] |

### Return type

[**NotificationCursorPage**](NotificationCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Stable newest-first notification page |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## markAllNotificationsRead

> markAllNotificationsRead()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { MarkAllNotificationsReadRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.markAllNotificationsRead();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

`void` (Empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Every customer notification marked read |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## markNotificationRead

> markNotificationRead(notificationId)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { MarkNotificationReadRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // number
    notificationId: 789,
  } satisfies MarkNotificationReadRequest;

  try {
    const data = await api.markNotificationRead(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **notificationId** | `number` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Notification marked read |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## registerCommunicationEndpoint

> CommunicationEndpoint registerCommunicationEndpoint(registerCommunicationEndpointRequest)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { RegisterCommunicationEndpointOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AccountApi(config);

  const body = {
    // RegisterCommunicationEndpointRequest
    registerCommunicationEndpointRequest: ...,
  } satisfies RegisterCommunicationEndpointOperationRequest;

  try {
    const data = await api.registerCommunicationEndpoint(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **registerCommunicationEndpointRequest** | [RegisterCommunicationEndpointRequest](RegisterCommunicationEndpointRequest.md) |  | |

### Return type

[**CommunicationEndpoint**](CommunicationEndpoint.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Endpoint registered |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## unlinkIdentity

> AccountIdentities unlinkIdentity(identityId, sensitiveProof)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { UnlinkIdentityRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // number
    identityId: 789,
    // SensitiveProof
    sensitiveProof: ...,
  } satisfies UnlinkIdentityRequest;

  try {
    const data = await api.unlinkIdentity(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **identityId** | `number` |  | [Defaults to `undefined`] |
| **sensitiveProof** | [SensitiveProof](SensitiveProof.md) |  | |

### Return type

[**AccountIdentities**](AccountIdentities.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Identity unlinked |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## unregisterCommunicationEndpoint

> unregisterCommunicationEndpoint()



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { UnregisterCommunicationEndpointRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AccountApi(config);

  try {
    const data = await api.unregisterCommunicationEndpoint();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Endpoint detached from current session |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## unregisterCommunicationEndpointById

> unregisterCommunicationEndpointById(id)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { UnregisterCommunicationEndpointByIdRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AccountApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies UnregisterCommunicationEndpointByIdRequest;

  try {
    const data = await api.unregisterCommunicationEndpointById(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Endpoint detached |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateNotificationPreferences

> NotificationPreferences updateNotificationPreferences(notificationPreferences)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { UpdateNotificationPreferencesRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // NotificationPreferences
    notificationPreferences: ...,
  } satisfies UpdateNotificationPreferencesRequest;

  try {
    const data = await api.updateNotificationPreferences(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **notificationPreferences** | [NotificationPreferences](NotificationPreferences.md) |  | |

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Updated preferences |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## verifyIdentityLink

> verifyIdentityLink(id, verifyIdentityLinkRequest)



### Example

```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { VerifyIdentityLinkOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaAuth
    apiKey: "YOUR API KEY",
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // VerifyIdentityLinkRequest
    verifyIdentityLinkRequest: ...,
  } satisfies VerifyIdentityLinkOperationRequest;

  try {
    const data = await api.verifyIdentityLink(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |
| **verifyIdentityLinkRequest** | [VerifyIdentityLinkRequest](VerifyIdentityLinkRequest.md) |  | |

### Return type

`void` (Empty response body)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Target identity proved |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

