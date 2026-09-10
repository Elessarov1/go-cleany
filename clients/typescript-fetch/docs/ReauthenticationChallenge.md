
# ReauthenticationChallenge


## Properties

Name | Type
------------ | -------------
`id` | string
`provider` | [IdentityProvider](IdentityProvider.md)
`nonce` | string
`expiresAt` | Date

## Example

```typescript
import type { ReauthenticationChallenge } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "provider": null,
  "nonce": null,
  "expiresAt": null,
} satisfies ReauthenticationChallenge

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ReauthenticationChallenge
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


