
# CreateTransactionFeedbackRequest


## Properties

Name | Type
------------ | -------------
`service` | string
`sourceEntityId` | number
`outcome` | string
`category` | string
`comment` | string

## Example

```typescript
import type { CreateTransactionFeedbackRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "service": null,
  "sourceEntityId": null,
  "outcome": null,
  "category": null,
  "comment": null,
} satisfies CreateTransactionFeedbackRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateTransactionFeedbackRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


