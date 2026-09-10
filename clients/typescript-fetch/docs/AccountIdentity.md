
# AccountIdentity


## Properties

Name | Type
------------ | -------------
`identityId` | number
`provider` | [IdentityProvider](IdentityProvider.md)
`issuer` | string
`linked` | boolean
`username` | string
`writeAccessAllowed` | boolean

## Example

```typescript
import type { AccountIdentity } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "identityId": null,
  "provider": null,
  "issuer": null,
  "linked": null,
  "username": null,
  "writeAccessAllowed": null,
} satisfies AccountIdentity

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AccountIdentity
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


