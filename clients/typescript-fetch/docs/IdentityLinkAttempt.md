
# IdentityLinkAttempt


## Properties

Name | Type
------------ | -------------
`id` | string
`provider` | [IdentityProvider](IdentityProvider.md)
`nonce` | string
`reauthenticationNonce` | string
`telegramDeepLink` | string
`expiresAt` | Date

## Example

```typescript
import type { IdentityLinkAttempt } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "provider": null,
  "nonce": null,
  "reauthenticationNonce": null,
  "telegramDeepLink": null,
  "expiresAt": null,
} satisfies IdentityLinkAttempt

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as IdentityLinkAttempt
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


