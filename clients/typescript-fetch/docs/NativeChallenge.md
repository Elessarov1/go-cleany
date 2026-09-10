
# NativeChallenge


## Properties

Name | Type
------------ | -------------
`challengeId` | string
`provider` | [IdentityProvider](IdentityProvider.md)
`nonce` | string
`expiresAt` | Date

## Example

```typescript
import type { NativeChallenge } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "challengeId": null,
  "provider": null,
  "nonce": null,
  "expiresAt": null,
} satisfies NativeChallenge

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as NativeChallenge
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


