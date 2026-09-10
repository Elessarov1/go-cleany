# PlatformApi

All URIs are relative to *https://loco-place.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getPlatformServices**](PlatformApi.md#getplatformservices) | **GET** /api/v1/catalog/services |  |



## getPlatformServices

> Array&lt;PlatformServiceState&gt; getPlatformServices()



### Example

```ts
import {
  Configuration,
  PlatformApi,
} from '@locoplace/api-client';
import type { GetPlatformServicesRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const api = new PlatformApi();

  try {
    const data = await api.getPlatformServices();
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

[**Array&lt;PlatformServiceState&gt;**](PlatformServiceState.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Services visible to the current customer context |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

