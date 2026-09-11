# ClientApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**captureTelegramAcquisition**](ClientApi.md#capturetelegramacquisition) | **POST** /api/v1/acquisition/telegram |  |
| [**getClientConfiguration**](ClientApi.md#getclientconfiguration) | **GET** /api/v1/client/configuration |  |



## captureTelegramAcquisition

> AcquisitionCapture captureTelegramAcquisition(telegramAcquisitionRequest)



### Example

```ts
import {
  Configuration,
  ClientApi,
} from '@locoplace/api-client';
import type { CaptureTelegramAcquisitionRequest } from '@locoplace/api-client';

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
  const api = new ClientApi(config);

  const body = {
    // TelegramAcquisitionRequest
    telegramAcquisitionRequest: ...,
  } satisfies CaptureTelegramAcquisitionRequest;

  try {
    const data = await api.captureTelegramAcquisition(body);
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
| **telegramAcquisitionRequest** | [TelegramAcquisitionRequest](TelegramAcquisitionRequest.md) |  | |

### Return type

[**AcquisitionCapture**](AcquisitionCapture.md)

### Authorization

[tmaAuth](../README.md#tmaAuth), [bearerAuth](../README.md#bearerAuth), [cookieSession](../README.md#cookieSession)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Safe local entry target for the captured campaign |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getClientConfiguration

> ClientConfiguration getClientConfiguration()



### Example

```ts
import {
  Configuration,
  ClientApi,
} from '@locoplace/api-client';
import type { GetClientConfigurationRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new ClientApi();

  try {
    const data = await api.getClientConfiguration();
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

[**ClientConfiguration**](ClientConfiguration.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Public client compatibility settings |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

