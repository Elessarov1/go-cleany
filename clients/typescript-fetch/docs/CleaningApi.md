# CleaningApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**cancelCleaningOrder**](CleaningApi.md#cancelcleaningorder) | **POST** /api/v1/cleaning/orders/{orderId}/cancel |  |
| [**createCleaningOrder**](CleaningApi.md#createcleaningorderoperation) | **POST** /api/v1/cleaning/orders |  |
| [**getCleaningConfiguration**](CleaningApi.md#getcleaningconfiguration) | **GET** /api/v1/cleaning/configuration |  |
| [**getCleaningOrder**](CleaningApi.md#getcleaningorder) | **GET** /api/v1/cleaning/orders/{orderId} |  |
| [**getCleaningOrders**](CleaningApi.md#getcleaningorders) | **GET** /api/v1/cleaning/orders |  |
| [**getCleaningReferralSummary**](CleaningApi.md#getcleaningreferralsummary) | **GET** /api/v1/referrals/me |  |
| [**getCleaningRepeatPrefill**](CleaningApi.md#getcleaningrepeatprefill) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-prefill |  |
| [**getCleaningRepeatReminder**](CleaningApi.md#getcleaningrepeatreminder) | **GET** /api/v1/cleaning/orders/{orderId}/repeat-reminder |  |
| [**getCleaningReport**](CleaningApi.md#getcleaningreport) | **GET** /api/v1/cleaning/orders/{orderId}/report |  |
| [**getCleaningReportPhoto**](CleaningApi.md#getcleaningreportphoto) | **GET** /api/v1/cleaning/orders/{orderId}/report/photos/{mediaId} |  |
| [**quoteCleaningOrder**](CleaningApi.md#quotecleaningorder) | **POST** /api/v1/cleaning/orders/quote |  |
| [**recordCleaningRepeatShown**](CleaningApi.md#recordcleaningrepeatshown) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-shown |  |
| [**updateCleaningRepeatReminder**](CleaningApi.md#updatecleaningrepeatreminder) | **PUT** /api/v1/cleaning/orders/{orderId}/repeat-reminder |  |



## cancelCleaningOrder

> CleaningOrder cancelCleaningOrder(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { CancelCleaningOrderRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies CancelCleaningOrderRequest;

  try {
    const data = await api.cancelCleaningOrder(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cancelled Cleaning order |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createCleaningOrder

> CleaningOrder createCleaningOrder(idempotencyKey, createCleaningOrderRequest)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { CreateCleaningOrderOperationRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // string
    idempotencyKey: idempotencyKey_example,
    // CreateCleaningOrderRequest
    createCleaningOrderRequest: ...,
  } satisfies CreateCleaningOrderOperationRequest;

  try {
    const data = await api.createCleaningOrder(body);
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
| **createCleaningOrderRequest** | [CreateCleaningOrderRequest](CreateCleaningOrderRequest.md) |  | |

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Cleaning order created |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningConfiguration

> CleaningConfiguration getCleaningConfiguration()



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningConfigurationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new CleaningApi();

  try {
    const data = await api.getCleaningConfiguration();
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

[**CleaningConfiguration**](CleaningConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cleaning form and pricing configuration |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningOrder

> CleaningOrder getCleaningOrder(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningOrderRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies GetCleaningOrderRequest;

  try {
    const data = await api.getCleaningOrder(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CleaningOrder**](CleaningOrder.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Customer-owned Cleaning order |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningOrders

> CleaningOrderCursorPage getCleaningOrders(cursor, size)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningOrdersRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // string (optional)
    cursor: cursor_example,
    // number (optional)
    size: 56,
  } satisfies GetCleaningOrdersRequest;

  try {
    const data = await api.getCleaningOrders(body);
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

[**CleaningOrderCursorPage**](CleaningOrderCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Stable newest-first Cleaning order page |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningReferralSummary

> ReferralSummary getCleaningReferralSummary()



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningReferralSummaryRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  try {
    const data = await api.getCleaningReferralSummary();
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

[**ReferralSummary**](ReferralSummary.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cleaning referral state for the current customer |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningRepeatPrefill

> CleaningRepeatPrefill getCleaningRepeatPrefill(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningRepeatPrefillRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies GetCleaningRepeatPrefillRequest;

  try {
    const data = await api.getCleaningRepeatPrefill(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CleaningRepeatPrefill**](CleaningRepeatPrefill.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verified repeat-order context |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningRepeatReminder

> CleaningRepeatReminder getCleaningRepeatReminder(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningRepeatReminderRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies GetCleaningRepeatReminderRequest;

  try {
    const data = await api.getCleaningRepeatReminder(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CleaningRepeatReminder**](CleaningRepeatReminder.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Current repeat reminder selection |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningReport

> CleaningReport getCleaningReport(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningReportRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies GetCleaningReportRequest;

  try {
    const data = await api.getCleaningReport(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CleaningReport**](CleaningReport.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Retained completion report metadata |  -  |
| **410** | Retained private resource has expired |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCleaningReportPhoto

> Blob getCleaningReportPhoto(orderId, mediaId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { GetCleaningReportPhotoRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
    // number
    mediaId: 789,
  } satisfies GetCleaningReportPhotoRequest;

  try {
    const data = await api.getCleaningReportPhoto(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |
| **mediaId** | `number` |  | [Defaults to `undefined`] |

### Return type

**Blob**

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `image/*`, `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Customer-owned private report photo; never publicly cacheable |  * Cache-Control -  <br>  |
| **404** | Resource not found |  -  |
| **410** | Retained private resource has expired |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## quoteCleaningOrder

> CleaningOrderQuote quoteCleaningOrder(cleaningOrderQuoteRequest)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { QuoteCleaningOrderRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // CleaningOrderQuoteRequest
    cleaningOrderQuoteRequest: ...,
  } satisfies QuoteCleaningOrderRequest;

  try {
    const data = await api.quoteCleaningOrder(body);
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
| **cleaningOrderQuoteRequest** | [CleaningOrderQuoteRequest](CleaningOrderQuoteRequest.md) |  | |

### Return type

[**CleaningOrderQuote**](CleaningOrderQuote.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Authoritative Cleaning quote |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## recordCleaningRepeatShown

> recordCleaningRepeatShown(orderId)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { RecordCleaningRepeatShownRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
  } satisfies RecordCleaningRepeatShownRequest;

  try {
    const data = await api.recordCleaningRepeatShown(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |

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
| **204** | Repeat opportunity impression recorded |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateCleaningRepeatReminder

> CleaningRepeatReminder updateCleaningRepeatReminder(orderId, cleaningRepeatReminderRequest)



### Example

```ts
import {
  Configuration,
  CleaningApi,
} from '@locoplace/api-client';
import type { UpdateCleaningRepeatReminderRequest } from '@locoplace/api-client';

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
  const api = new CleaningApi(config);

  const body = {
    // number
    orderId: 789,
    // CleaningRepeatReminderRequest
    cleaningRepeatReminderRequest: ...,
  } satisfies UpdateCleaningRepeatReminderRequest;

  try {
    const data = await api.updateCleaningRepeatReminder(body);
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
| **orderId** | `number` |  | [Defaults to `undefined`] |
| **cleaningRepeatReminderRequest** | [CleaningRepeatReminderRequest](CleaningRepeatReminderRequest.md) |  | |

### Return type

[**CleaningRepeatReminder**](CleaningRepeatReminder.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Updated repeat reminder selection |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

