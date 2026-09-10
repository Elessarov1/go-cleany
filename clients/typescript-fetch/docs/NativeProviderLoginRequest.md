
# NativeProviderLoginRequest


## Properties

Name | Type
------------ | -------------
`challengeId` | string
`clientType` | string
`identityToken` | string
`authorizationCode` | string

## Example

```typescript
import type { NativeProviderLoginRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "challengeId": null,
  "clientType": null,
  "identityToken": null,
  "authorizationCode": null,
} satisfies NativeProviderLoginRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as NativeProviderLoginRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


