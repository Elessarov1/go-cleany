# TransferApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**cancelTransferBooking**](TransferApi.md#canceltransferbooking) | **POST** /api/v1/transfer/bookings/{bookingId}/cancel |  |
| [**createTransferBooking**](TransferApi.md#createtransferbookingoperation) | **POST** /api/v1/transfer/bookings |  |
| [**getTransferBooking**](TransferApi.md#gettransferbooking) | **GET** /api/v1/transfer/bookings/{bookingId} |  |
| [**getTransferBookings**](TransferApi.md#gettransferbookings) | **GET** /api/v1/transfer/bookings |  |
| [**getTransferConfiguration**](TransferApi.md#gettransferconfiguration) | **GET** /api/v1/transfer/configuration |  |
| [**getTransferRepeatPrefill**](TransferApi.md#gettransferrepeatprefill) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-prefill |  |
| [**quoteTransfer**](TransferApi.md#quotetransfer) | **POST** /api/v1/transfer/quote |  |
| [**recordTransferRepeatShown**](TransferApi.md#recordtransferrepeatshown) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-shown |  |



## cancelTransferBooking

> TransferBooking cancelTransferBooking(bookingId)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { CancelTransferBookingRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies CancelTransferBookingRequest;

  try {
    const data = await api.cancelTransferBooking(body);
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
| **bookingId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cancelled Transfer booking |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createTransferBooking

> TransferBooking createTransferBooking(idempotencyKey, createTransferBookingRequest)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { CreateTransferBookingOperationRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // string
    idempotencyKey: idempotencyKey_example,
    // CreateTransferBookingRequest
    createTransferBookingRequest: ...,
  } satisfies CreateTransferBookingOperationRequest;

  try {
    const data = await api.createTransferBooking(body);
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
| **createTransferBookingRequest** | [CreateTransferBookingRequest](CreateTransferBookingRequest.md) |  | |

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Transfer booking created |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTransferBooking

> TransferBooking getTransferBooking(bookingId)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { GetTransferBookingRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies GetTransferBookingRequest;

  try {
    const data = await api.getTransferBooking(body);
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
| **bookingId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**TransferBooking**](TransferBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Customer-owned Transfer booking |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTransferBookings

> TransferBookingCursorPage getTransferBookings(cursor, size)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { GetTransferBookingsRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // string (optional)
    cursor: cursor_example,
    // number (optional)
    size: 56,
  } satisfies GetTransferBookingsRequest;

  try {
    const data = await api.getTransferBookings(body);
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

[**TransferBookingCursorPage**](TransferBookingCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Stable newest-first Transfer booking page |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTransferConfiguration

> TransferConfiguration getTransferConfiguration()



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { GetTransferConfigurationRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  try {
    const data = await api.getTransferConfiguration();
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

[**TransferConfiguration**](TransferConfiguration.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Transfer airports, vehicles, prices and date policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTransferRepeatPrefill

> TransferRepeatPrefill getTransferRepeatPrefill(bookingId)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { GetTransferRepeatPrefillRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies GetTransferRepeatPrefillRequest;

  try {
    const data = await api.getTransferRepeatPrefill(body);
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
| **bookingId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**TransferRepeatPrefill**](TransferRepeatPrefill.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verified repeat Transfer context |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## quoteTransfer

> TransferQuote quoteTransfer(transferQuoteRequest)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { QuoteTransferRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // TransferQuoteRequest
    transferQuoteRequest: ...,
  } satisfies QuoteTransferRequest;

  try {
    const data = await api.quoteTransfer(body);
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
| **transferQuoteRequest** | [TransferQuoteRequest](TransferQuoteRequest.md) |  | |

### Return type

[**TransferQuote**](TransferQuote.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Authoritative Transfer quote |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## recordTransferRepeatShown

> recordTransferRepeatShown(bookingId)



### Example

```ts
import {
  Configuration,
  TransferApi,
} from '@locoplace/api-client';
import type { RecordTransferRepeatShownRequest } from '@locoplace/api-client';

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
  const api = new TransferApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies RecordTransferRepeatShownRequest;

  try {
    const data = await api.recordTransferRepeatShown(body);
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
| **bookingId** | `number` |  | [Defaults to `undefined`] |

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

