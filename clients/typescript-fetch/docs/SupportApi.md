# SupportApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createSupportCase**](SupportApi.md#createsupportcaseoperation) | **POST** /api/v1/account/support/cases |  |
| [**getTransactionSupport**](SupportApi.md#gettransactionsupport) | **GET** /api/v1/account/support/sources/{service}/{sourceEntityId} |  |
| [**submitTransactionFeedback**](SupportApi.md#submittransactionfeedback) | **POST** /api/v1/account/support/feedback |  |



## createSupportCase

> SupportCase createSupportCase(createSupportCaseRequest)



### Example

```ts
import {
  Configuration,
  SupportApi,
} from '@locoplace/api-client';
import type { CreateSupportCaseOperationRequest } from '@locoplace/api-client';

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
  const api = new SupportApi(config);

  const body = {
    // CreateSupportCaseRequest
    createSupportCaseRequest: ...,
  } satisfies CreateSupportCaseOperationRequest;

  try {
    const data = await api.createSupportCase(body);
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
| **createSupportCaseRequest** | [CreateSupportCaseRequest](CreateSupportCaseRequest.md) |  | |

### Return type

[**SupportCase**](SupportCase.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Existing open support case returned idempotently |  -  |
| **201** | Support case created |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTransactionSupport

> TransactionSupport getTransactionSupport(service, sourceEntityId)



### Example

```ts
import {
  Configuration,
  SupportApi,
} from '@locoplace/api-client';
import type { GetTransactionSupportRequest } from '@locoplace/api-client';

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
  const api = new SupportApi(config);

  const body = {
    // 'CLEANING' | 'RENTAL' | 'TRANSFER'
    service: service_example,
    // number
    sourceEntityId: 789,
  } satisfies GetTransactionSupportRequest;

  try {
    const data = await api.getTransactionSupport(body);
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
| **service** | `CLEANING`, `RENTAL`, `TRANSFER` |  | [Defaults to `undefined`] [Enum: CLEANING, RENTAL, TRANSFER] |
| **sourceEntityId** | `number` |  | [Defaults to `undefined`] |

### Return type

[**TransactionSupport**](TransactionSupport.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Feedback and support state attached to an owned transaction |  -  |
| **404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## submitTransactionFeedback

> TransactionSupport submitTransactionFeedback(createTransactionFeedbackRequest)



### Example

```ts
import {
  Configuration,
  SupportApi,
} from '@locoplace/api-client';
import type { SubmitTransactionFeedbackRequest } from '@locoplace/api-client';

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
  const api = new SupportApi(config);

  const body = {
    // CreateTransactionFeedbackRequest
    createTransactionFeedbackRequest: ...,
  } satisfies SubmitTransactionFeedbackRequest;

  try {
    const data = await api.submitTransactionFeedback(body);
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
| **createTransactionFeedbackRequest** | [CreateTransactionFeedbackRequest](CreateTransactionFeedbackRequest.md) |  | |

### Return type

[**TransactionSupport**](TransactionSupport.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Updated transaction support state |  -  |
| **409** | Business or security conflict |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

