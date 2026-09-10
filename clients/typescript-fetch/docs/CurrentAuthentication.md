
# CurrentAuthentication


## Properties

Name | Type
------------ | -------------
`authenticated` | boolean
`customerId` | number
`displayName` | string
`provider` | [IdentityProvider](IdentityProvider.md)
`roles` | Set&lt;string&gt;
`loginProviders` | [LoginProviders](LoginProviders.md)

## Example

```typescript
import type { CurrentAuthentication } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "authenticated": null,
  "customerId": null,
  "displayName": null,
  "provider": null,
  "roles": null,
  "loginProviders": null,
} satisfies CurrentAuthentication

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CurrentAuthentication
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


