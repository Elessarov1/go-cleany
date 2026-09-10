
# TransactionFeedback


## Properties

Name | Type
------------ | -------------
`id` | number
`outcome` | string
`category` | string
`comment` | string
`supportCaseId` | number
`createdAt` | Date

## Example

```typescript
import type { TransactionFeedback } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "outcome": null,
  "category": null,
  "comment": null,
  "supportCaseId": null,
  "createdAt": null,
} satisfies TransactionFeedback

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransactionFeedback
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


