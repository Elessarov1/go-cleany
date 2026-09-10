
# RentalTransferPrefill


## Properties

Name | Type
------------ | -------------
`rentalBookingId` | number
`context` | string
`direction` | string
`suggestedDate` | Date
`address` | string
`benefit` | [RentalTransferBenefit](RentalTransferBenefit.md)

## Example

```typescript
import type { RentalTransferPrefill } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "rentalBookingId": null,
  "context": null,
  "direction": null,
  "suggestedDate": null,
  "address": null,
  "benefit": null,
} satisfies RentalTransferPrefill

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalTransferPrefill
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


