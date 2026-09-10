
# RentalCleaningContext


## Properties

Name | Type
------------ | -------------
`rentalBookingId` | number
`address` | string
`phone` | string
`checkOutDate` | Date
`earliestBenefitCleaningDate` | Date
`benefitStatus` | string
`promoCode` | string
`cleaningFlowAvailable` | boolean

## Example

```typescript
import type { RentalCleaningContext } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "rentalBookingId": null,
  "address": null,
  "phone": null,
  "checkOutDate": null,
  "earliestBenefitCleaningDate": null,
  "benefitStatus": null,
  "promoCode": null,
  "cleaningFlowAvailable": null,
} satisfies RentalCleaningContext

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalCleaningContext
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


