
# SessionTokens


## Properties

Name | Type
------------ | -------------
`sessionId` | string
`tokenType` | string
`accessToken` | string
`refreshToken` | string
`accessExpiresAt` | Date
`refreshExpiresAt` | Date
`absoluteExpiresAt` | Date

## Example

```typescript
import type { SessionTokens } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "sessionId": null,
  "tokenType": null,
  "accessToken": null,
  "refreshToken": null,
  "accessExpiresAt": null,
  "refreshExpiresAt": null,
  "absoluteExpiresAt": null,
} satisfies SessionTokens

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SessionTokens
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


