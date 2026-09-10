
# RentalTransferContextOption


## Properties

Name | Type
------------ | -------------
`context` | string
`availability` | string
`direction` | string
`suggestedDate` | Date
`address` | string
`availableFromDate` | Date
`benefit` | [RentalTransferBenefit](RentalTransferBenefit.md)

## Example

```typescript
import type { RentalTransferContextOption } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "context": null,
  "availability": null,
  "direction": null,
  "suggestedDate": null,
  "address": null,
  "availableFromDate": null,
  "benefit": null,
} satisfies RentalTransferContextOption

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalTransferContextOption
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


