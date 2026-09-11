# RentalApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**cancelRentalBooking**](RentalApi.md#cancelrentalbooking) | **POST** /api/v1/rental/bookings/{bookingId}/cancel |  |
| [**createRentalBooking**](RentalApi.md#createrentalbookingoperation) | **POST** /api/v1/rental/bookings |  |
| [**getRentalAvailability**](RentalApi.md#getrentalavailability) | **GET** /api/v1/rental/properties/{propertyId}/availability |  |
| [**getRentalBooking**](RentalApi.md#getrentalbooking) | **GET** /api/v1/rental/bookings/{bookingId} |  |
| [**getRentalBookings**](RentalApi.md#getrentalbookings) | **GET** /api/v1/rental/bookings |  |
| [**getRentalCleaningContext**](RentalApi.md#getrentalcleaningcontext) | **GET** /api/v1/rental/bookings/{bookingId}/cleaning-context |  |
| [**getRentalConfiguration**](RentalApi.md#getrentalconfiguration) | **GET** /api/v1/rental/configuration |  |
| [**getRentalProperty**](RentalApi.md#getrentalproperty) | **GET** /api/v1/rental/properties/{slug} |  |
| [**getRentalPropertyMedia**](RentalApi.md#getrentalpropertymedia) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId} |  |
| [**getRentalPropertyMediaVariant**](RentalApi.md#getrentalpropertymediavariant) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId}/{variant} |  |
| [**getRentalTransferContext**](RentalApi.md#getrentaltransfercontext) | **GET** /api/v1/rental/bookings/{bookingId}/transfer-context |  |
| [**getRentalTransferPrefill**](RentalApi.md#getrentaltransferprefill) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/prefill |  |
| [**quoteRentalProperty**](RentalApi.md#quoterentalproperty) | **GET** /api/v1/rental/properties/{propertyId}/quote |  |
| [**recordRentalFirstCard**](RentalApi.md#recordrentalfirstcard) | **POST** /api/v1/rental/searches/{executionId}/first-card |  |
| [**recordRentalSearchOpened**](RentalApi.md#recordrentalsearchopened) | **POST** /api/v1/rental/searches/{executionId}/opened |  |
| [**recordRentalTransferContextShown**](RentalApi.md#recordrentaltransfercontextshown) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/shown |  |
| [**searchRentalProperties**](RentalApi.md#searchrentalproperties) | **GET** /api/v1/rental/search |  |



## cancelRentalBooking

> RentalBooking cancelRentalBooking(bookingId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { CancelRentalBookingRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies CancelRentalBookingRequest;

  try {
    const data = await api.cancelRentalBooking(body);
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

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Cancelled Rental booking |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createRentalBooking

> RentalBooking createRentalBooking(idempotencyKey, createRentalBookingRequest)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { CreateRentalBookingOperationRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // string
    idempotencyKey: idempotencyKey_example,
    // CreateRentalBookingRequest
    createRentalBookingRequest: ...,
  } satisfies CreateRentalBookingOperationRequest;

  try {
    const data = await api.createRentalBooking(body);
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
| **createRentalBookingRequest** | [CreateRentalBookingRequest](CreateRentalBookingRequest.md) |  | |

### Return type

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Rental booking created |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalAvailability

> RentalAvailability getRentalAvailability(propertyId, fromDate, toDate)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalAvailabilityRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    propertyId: 789,
    // Date
    fromDate: 2013-10-20,
    // Date
    toDate: 2013-10-20,
  } satisfies GetRentalAvailabilityRequest;

  try {
    const data = await api.getRentalAvailability(body);
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
| **propertyId** | `number` |  | [Defaults to `undefined`] |
| **fromDate** | `Date` |  | [Defaults to `undefined`] |
| **toDate** | `Date` |  | [Defaults to `undefined`] |

### Return type

[**RentalAvailability**](RentalAvailability.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Unavailable date ranges within the requested interval |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalBooking

> RentalBooking getRentalBooking(bookingId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalBookingRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies GetRentalBookingRequest;

  try {
    const data = await api.getRentalBooking(body);
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

[**RentalBooking**](RentalBooking.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Customer-owned Rental booking |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalBookings

> RentalBookingCursorPage getRentalBookings(cursor, size)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalBookingsRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // string (optional)
    cursor: cursor_example,
    // number (optional)
    size: 56,
  } satisfies GetRentalBookingsRequest;

  try {
    const data = await api.getRentalBookings(body);
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

[**RentalBookingCursorPage**](RentalBookingCursorPage.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Stable newest-first Rental booking page |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalCleaningContext

> RentalCleaningContext getRentalCleaningContext(bookingId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalCleaningContextRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies GetRentalCleaningContextRequest;

  try {
    const data = await api.getRentalCleaningContext(body);
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

[**RentalCleaningContext**](RentalCleaningContext.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verified Rental-to-Cleaning context and benefit |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalConfiguration

> RentalConfiguration getRentalConfiguration()



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalConfigurationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new RentalApi();

  try {
    const data = await api.getRentalConfiguration();
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

[**RentalConfiguration**](RentalConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Rental stay policy and client limits |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalProperty

> RentalProperty getRentalProperty(slug)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalPropertyRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // string
    slug: slug_example,
  } satisfies GetRentalPropertyRequest;

  try {
    const data = await api.getRentalProperty(body);
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
| **slug** | `string` |  | [Defaults to `undefined`] |

### Return type

[**RentalProperty**](RentalProperty.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Published Rental property |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalPropertyMedia

> Blob getRentalPropertyMedia(propertyId, mediaId, v)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalPropertyMediaRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new RentalApi();

  const body = {
    // number
    propertyId: 789,
    // number
    mediaId: 789,
    // number (optional)
    v: 789,
  } satisfies GetRentalPropertyMediaRequest;

  try {
    const data = await api.getRentalPropertyMedia(body);
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
| **propertyId** | `number` |  | [Defaults to `undefined`] |
| **mediaId** | `number` |  | [Defaults to `undefined`] |
| **v** | `number` |  | [Optional] [Defaults to `undefined`] |

### Return type

**Blob**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `image/*`, `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public full-size Rental media |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalPropertyMediaVariant

> Blob getRentalPropertyMediaVariant(propertyId, mediaId, variant, v)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalPropertyMediaVariantRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new RentalApi();

  const body = {
    // number
    propertyId: 789,
    // number
    mediaId: 789,
    // 'card' | 'thumbnail'
    variant: variant_example,
    // number (optional)
    v: 789,
  } satisfies GetRentalPropertyMediaVariantRequest;

  try {
    const data = await api.getRentalPropertyMediaVariant(body);
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
| **propertyId** | `number` |  | [Defaults to `undefined`] |
| **mediaId** | `number` |  | [Defaults to `undefined`] |
| **variant** | `card`, `thumbnail` |  | [Defaults to `undefined`] [Enum: card, thumbnail] |
| **v** | `number` |  | [Optional] [Defaults to `undefined`] |

### Return type

**Blob**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `image/*`, `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public card or thumbnail Rental media |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalTransferContext

> RentalTransferContext getRentalTransferContext(bookingId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalTransferContextRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
  } satisfies GetRentalTransferContextRequest;

  try {
    const data = await api.getRentalTransferContext(body);
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

[**RentalTransferContext**](RentalTransferContext.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verified Rental-to-Transfer options |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getRentalTransferPrefill

> RentalTransferPrefill getRentalTransferPrefill(bookingId, context)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { GetRentalTransferPrefillRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
    // 'ARRIVAL' | 'CHECKOUT'
    context: context_example,
  } satisfies GetRentalTransferPrefillRequest;

  try {
    const data = await api.getRentalTransferPrefill(body);
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
| **context** | `ARRIVAL`, `CHECKOUT` |  | [Defaults to `undefined`] [Enum: ARRIVAL, CHECKOUT] |

### Return type

[**RentalTransferPrefill**](RentalTransferPrefill.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verified Transfer prefill from Rental |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## quoteRentalProperty

> RentalQuote quoteRentalProperty(propertyId, termType, checkInDate, guests, checkOutDate, months)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { QuoteRentalPropertyRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    propertyId: 789,
    // RentalTermType
    termType: ...,
    // Date
    checkInDate: 2013-10-20,
    // number
    guests: 56,
    // Date (optional)
    checkOutDate: 2013-10-20,
    // number (optional)
    months: 56,
  } satisfies QuoteRentalPropertyRequest;

  try {
    const data = await api.quoteRentalProperty(body);
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
| **propertyId** | `number` |  | [Defaults to `undefined`] |
| **termType** | `RentalTermType` |  | [Defaults to `undefined`] [Enum: DATE_RANGE, MONTHLY, UNKNOWN] |
| **checkInDate** | `Date` |  | [Defaults to `undefined`] |
| **guests** | `number` |  | [Defaults to `undefined`] |
| **checkOutDate** | `Date` |  | [Optional] [Defaults to `undefined`] |
| **months** | `number` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**RentalQuote**](RentalQuote.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Authoritative Rental quote; response is not cacheable |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## recordRentalFirstCard

> recordRentalFirstCard(executionId, rentalFirstCardRequest)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { RecordRentalFirstCardRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new RentalApi();

  const body = {
    // string
    executionId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // RentalFirstCardRequest
    rentalFirstCardRequest: ...,
  } satisfies RecordRentalFirstCardRequest;

  try {
    const data = await api.recordRentalFirstCard(body);
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
| **executionId** | `string` |  | [Defaults to `undefined`] |
| **rentalFirstCardRequest** | [RentalFirstCardRequest](RentalFirstCardRequest.md) |  | |

### Return type

`void` (Empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | First-card performance signal accepted |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## recordRentalSearchOpened

> recordRentalSearchOpened(executionId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { RecordRentalSearchOpenedRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new RentalApi();

  const body = {
    // string
    executionId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies RecordRentalSearchOpenedRequest;

  try {
    const data = await api.recordRentalSearchOpened(body);
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
| **executionId** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Search-opened signal accepted |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## recordRentalTransferContextShown

> recordRentalTransferContextShown(bookingId, context)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { RecordRentalTransferContextShownRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // number
    bookingId: 789,
    // 'ARRIVAL' | 'CHECKOUT'
    context: context_example,
  } satisfies RecordRentalTransferContextShownRequest;

  try {
    const data = await api.recordRentalTransferContextShown(body);
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
| **context** | `ARRIVAL`, `CHECKOUT` |  | [Defaults to `undefined`] [Enum: ARRIVAL, CHECKOUT] |

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
| **204** | Contextual action impression recorded |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## searchRentalProperties

> RentalSearch searchRentalProperties(termType, checkInDate, checkOutDate, months, guests, cursor, size, xRentalPreviousSearchId)



### Example

```ts
import {
  Configuration,
  RentalApi,
} from '@locoplace/api-client';
import type { SearchRentalPropertiesRequest } from '@locoplace/api-client';

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
  const api = new RentalApi(config);

  const body = {
    // RentalTermType (optional)
    termType: ...,
    // Date (optional)
    checkInDate: 2013-10-20,
    // Date (optional)
    checkOutDate: 2013-10-20,
    // number (optional)
    months: 56,
    // number (optional)
    guests: 56,
    // string (optional)
    cursor: cursor_example,
    // number (optional)
    size: 56,
    // string (optional)
    xRentalPreviousSearchId: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies SearchRentalPropertiesRequest;

  try {
    const data = await api.searchRentalProperties(body);
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
| **termType** | `RentalTermType` |  | [Optional] [Defaults to `undefined`] [Enum: DATE_RANGE, MONTHLY, UNKNOWN] |
| **checkInDate** | `Date` |  | [Optional] [Defaults to `undefined`] |
| **checkOutDate** | `Date` |  | [Optional] [Defaults to `undefined`] |
| **months** | `number` |  | [Optional] [Defaults to `undefined`] |
| **guests** | `number` |  | [Optional] [Defaults to `undefined`] |
| **cursor** | `string` |  | [Optional] [Defaults to `undefined`] |
| **size** | `number` |  | [Optional] [Defaults to `20`] |
| **xRentalPreviousSearchId** | `string` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**RentalSearch**](RentalSearch.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public Rental search result; response is not cacheable |  -  |
| **400** | Invalid request |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)
