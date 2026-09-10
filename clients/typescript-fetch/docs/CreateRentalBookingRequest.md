
# CreateRentalBookingRequest


## Properties

Name | Type
------------ | -------------
`propertyId` | number
`termType` | [RentalTermType](RentalTermType.md)
`checkInDate` | Date
`checkOutDate` | Date
`months` | number
`guests` | number
`phone` | string
`comment` | string
`expectedTotalPrice` | number
`expectedCurrency` | string
`searchExecutionId` | string

## Example

```typescript
import type { CreateRentalBookingRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "propertyId": null,
  "termType": null,
  "checkInDate": null,
  "checkOutDate": null,
  "months": null,
  "guests": null,
  "phone": null,
  "comment": null,
  "expectedTotalPrice": null,
  "expectedCurrency": null,
  "searchExecutionId": null,
} satisfies CreateRentalBookingRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateRentalBookingRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


