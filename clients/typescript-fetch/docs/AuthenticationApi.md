# AuthenticationApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createNativeChallenge**](AuthenticationApi.md#createnativechallengeoperation) | **POST** /api/v1/auth/native/challenges |  |
| [**createTelegramLoginAttempt**](AuthenticationApi.md#createtelegramloginattempt) | **POST** /api/v1/auth/native/telegram/attempts |  |
| [**createTmaSession**](AuthenticationApi.md#createtmasession) | **POST** /api/v1/auth/tma/session |  |
| [**exchangeTelegramLoginAttempt**](AuthenticationApi.md#exchangetelegramloginattemptoperation) | **POST** /api/v1/auth/native/telegram/attempts/{attemptId}/exchange |  |
| [**getCsrfToken**](AuthenticationApi.md#getcsrftoken) | **GET** /api/v1/auth/csrf |  |
| [**getCurrentAuthentication**](AuthenticationApi.md#getcurrentauthentication) | **GET** /api/v1/auth/me |  |
| [**loginWithApple**](AuthenticationApi.md#loginwithapple) | **POST** /api/v1/auth/native/apple |  |
| [**loginWithGoogle**](AuthenticationApi.md#loginwithgoogle) | **POST** /api/v1/auth/native/google |  |
| [**logoutCurrentSession**](AuthenticationApi.md#logoutcurrentsession) | **DELETE** /api/v1/auth/sessions/current |  |
| [**refreshSession**](AuthenticationApi.md#refreshsessionoperation) | **POST** /api/v1/auth/sessions/refresh |  |
| [**revokeAllSessions**](AuthenticationApi.md#revokeallsessions) | **DELETE** /api/v1/auth/sessions |  |



## createNativeChallenge

> NativeChallenge createNativeChallenge(createNativeChallengeRequest)



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { CreateNativeChallengeOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  const body = {
    // CreateNativeChallengeRequest
    createNativeChallengeRequest: ...,
  } satisfies CreateNativeChallengeOperationRequest;

  try {
    const data = await api.createNativeChallenge(body);
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
| **createNativeChallengeRequest** | [CreateNativeChallengeRequest](CreateNativeChallengeRequest.md) |  | |

### Return type

[**NativeChallenge**](NativeChallenge.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Nonce-bound login challenge |  -  |
| **400** | Invalid request |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createTelegramLoginAttempt

> TelegramLoginAttempt createTelegramLoginAttempt()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { CreateTelegramLoginAttemptRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  try {
    const data = await api.createTelegramLoginAttempt();
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

[**TelegramLoginAttempt**](TelegramLoginAttempt.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | One-time bot handoff |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createTmaSession

> createTmaSession()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { CreateTmaSessionRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // To configure API key authorization: tmaBootstrap
    apiKey: "YOUR API KEY",
  });
  const api = new AuthenticationApi(config);

  try {
    const data = await api.createTmaSession();
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

[tmaBootstrap](../README.md#tmaBootstrap)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Cookie session created |  -  |
| **401** | Authentication failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## exchangeTelegramLoginAttempt

> SessionTokens exchangeTelegramLoginAttempt(attemptId, exchangeTelegramLoginAttemptRequest)



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { ExchangeTelegramLoginAttemptOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  const body = {
    // string
    attemptId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // ExchangeTelegramLoginAttemptRequest
    exchangeTelegramLoginAttemptRequest: ...,
  } satisfies ExchangeTelegramLoginAttemptOperationRequest;

  try {
    const data = await api.exchangeTelegramLoginAttempt(body);
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
| **attemptId** | `string` |  | [Defaults to `undefined`] |
| **exchangeTelegramLoginAttemptRequest** | [ExchangeTelegramLoginAttemptRequest](ExchangeTelegramLoginAttemptRequest.md) |  | |

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | First-party native credentials |  -  |
| **401** | Authentication failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCsrfToken

> CsrfToken getCsrfToken()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { GetCsrfTokenRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  try {
    const data = await api.getCsrfToken();
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

[**CsrfToken**](CsrfToken.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cookie-session CSRF header and token |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCurrentAuthentication

> CurrentAuthentication getCurrentAuthentication()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { GetCurrentAuthenticationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  try {
    const data = await api.getCurrentAuthentication();
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

[**CurrentAuthentication**](CurrentAuthentication.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current first-party or anonymous browser authentication state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## loginWithApple

> SessionTokens loginWithApple(nativeProviderLoginRequest)



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { LoginWithAppleRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  const body = {
    // NativeProviderLoginRequest
    nativeProviderLoginRequest: ...,
  } satisfies LoginWithAppleRequest;

  try {
    const data = await api.loginWithApple(body);
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
| **nativeProviderLoginRequest** | [NativeProviderLoginRequest](NativeProviderLoginRequest.md) |  | |

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | First-party native credentials |  -  |
| **401** | Authentication failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## loginWithGoogle

> SessionTokens loginWithGoogle(nativeProviderLoginRequest)



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { LoginWithGoogleRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  const body = {
    // NativeProviderLoginRequest
    nativeProviderLoginRequest: ...,
  } satisfies LoginWithGoogleRequest;

  try {
    const data = await api.loginWithGoogle(body);
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
| **nativeProviderLoginRequest** | [NativeProviderLoginRequest](NativeProviderLoginRequest.md) |  | |

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | First-party native credentials |  -  |
| **401** | Authentication failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## logoutCurrentSession

> logoutCurrentSession()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { LogoutCurrentSessionRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AuthenticationApi(config);

  try {
    const data = await api.logoutCurrentSession();
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

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Current session revoked |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## refreshSession

> SessionTokens refreshSession(idempotencyKey, refreshSessionRequest)



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { RefreshSessionOperationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new AuthenticationApi();

  const body = {
    // string
    idempotencyKey: idempotencyKey_example,
    // RefreshSessionRequest
    refreshSessionRequest: ...,
  } satisfies RefreshSessionOperationRequest;

  try {
    const data = await api.refreshSession(body);
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
| **idempotencyKey** | `string` |  | [Defaults to `undefined`] |
| **refreshSessionRequest** | [RefreshSessionRequest](RefreshSessionRequest.md) |  | |

### Return type

[**SessionTokens**](SessionTokens.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Rotated credentials |  -  |
| **401** | Authentication failed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## revokeAllSessions

> revokeAllSessions()



### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '@locoplace/api-client';
import type { RevokeAllSessionsRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AuthenticationApi(config);

  try {
    const data = await api.revokeAllSessions();
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

[bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Every customer session revoked |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

