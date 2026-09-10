
# CreateCleaningOrderRequest


## Properties

Name | Type
------------ | -------------
`area` | string
`address` | string
`apartmentType` | string
`duplex` | boolean
`cleaningType` | string
`requestedDate` | Date
`phone` | string
`comment` | string
`referralCode` | string
`rentalCleaningPromoCode` | string
`repeatFromOrderId` | number

## Example

```typescript
import type { CreateCleaningOrderRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "area": null,
  "address": null,
  "apartmentType": null,
  "duplex": null,
  "cleaningType": null,
  "requestedDate": null,
  "phone": null,
  "comment": null,
  "referralCode": null,
  "rentalCleaningPromoCode": null,
  "repeatFromOrderId": null,
} satisfies CreateCleaningOrderRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateCleaningOrderRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


