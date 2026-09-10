
# TelegramLoginAttempt


## Properties

Name | Type
------------ | -------------
`attemptId` | string
`botUrl` | string
`verifier` | string
`expiresAt` | Date

## Example

```typescript
import type { TelegramLoginAttempt } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "attemptId": null,
  "botUrl": null,
  "verifier": null,
  "expiresAt": null,
} satisfies TelegramLoginAttempt

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TelegramLoginAttempt
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


