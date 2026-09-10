
# TransactionSupport


## Properties

Name | Type
------------ | -------------
`service` | string
`sourceEntityId` | number
`feedbackEligible` | boolean
`feedback` | [TransactionFeedback](TransactionFeedback.md)
`latestCase` | [SupportCase](SupportCase.md)

## Example

```typescript
import type { TransactionSupport } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "service": null,
  "sourceEntityId": null,
  "feedbackEligible": null,
  "feedback": null,
  "latestCase": null,
} satisfies TransactionSupport

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransactionSupport
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


