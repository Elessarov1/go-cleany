
# CleaningOrderQuoteRequest


## Properties

Name | Type
------------ | -------------
`apartmentType` | string
`duplex` | boolean
`cleaningType` | string
`referralCode` | string
`requestedDate` | Date
`rentalCleaningPromoCode` | string

## Example

```typescript
import type { CleaningOrderQuoteRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "apartmentType": null,
  "duplex": null,
  "cleaningType": null,
  "referralCode": null,
  "requestedDate": null,
  "rentalCleaningPromoCode": null,
} satisfies CleaningOrderQuoteRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningOrderQuoteRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


