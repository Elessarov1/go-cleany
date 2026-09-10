
# RentalBooking


## Properties

Name | Type
------------ | -------------
`id` | number
`property` | [RentalBookingProperty](RentalBookingProperty.md)
`termType` | [RentalTermType](RentalTermType.md)
`checkInDate` | Date
`checkOutDate` | Date
`rentalMonths` | number
`durationDays` | number
`customerName` | string
`phone` | string
`guests` | number
`comment` | string
`baseDailyPriceSnapshot` | number
`baseMonthlyPriceSnapshot` | number
`monthlyPriceSnapshot` | number
`longTermDiscountRateSnapshot` | number
`discountAmount` | number
`totalPrice` | number
`currency` | string
`status` | string
`createdAt` | Date
`cancelledAt` | Date
`cancellationReason` | string
`completedAt` | Date
`money` | [Money](Money.md)

## Example

```typescript
import type { RentalBooking } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "property": null,
  "termType": null,
  "checkInDate": null,
  "checkOutDate": null,
  "rentalMonths": null,
  "durationDays": null,
  "customerName": null,
  "phone": null,
  "guests": null,
  "comment": null,
  "baseDailyPriceSnapshot": null,
  "baseMonthlyPriceSnapshot": null,
  "monthlyPriceSnapshot": null,
  "longTermDiscountRateSnapshot": null,
  "discountAmount": null,
  "totalPrice": null,
  "currency": null,
  "status": null,
  "createdAt": null,
  "cancelledAt": null,
  "cancellationReason": null,
  "completedAt": null,
  "money": null,
} satisfies RentalBooking

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalBooking
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


