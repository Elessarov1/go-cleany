
# SensitiveProof


## Properties

Name | Type
------------ | -------------
`challengeId` | string
`identityToken` | string
`telegramInitData` | string

## Example

```typescript
import type { SensitiveProof } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "challengeId": null,
  "identityToken": null,
  "telegramInitData": null,
} satisfies SensitiveProof

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SensitiveProof
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


