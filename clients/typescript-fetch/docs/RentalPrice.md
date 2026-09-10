
# RentalPrice


## Properties

Name | Type
------------ | -------------
`baseDailyPrice` | number
`baseMonthlyPrice` | number
`monthlyPrice` | number
`baseAmount` | number
`discountRate` | number
`discountAmount` | number
`longTermDiscountApplied` | boolean
`totalPrice` | number
`currency` | string
`rentalMonths` | number
`durationDays` | number

## Example

```typescript
import type { RentalPrice } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "baseDailyPrice": null,
  "baseMonthlyPrice": null,
  "monthlyPrice": null,
  "baseAmount": null,
  "discountRate": null,
  "discountAmount": null,
  "longTermDiscountApplied": null,
  "totalPrice": null,
  "currency": null,
  "rentalMonths": null,
  "durationDays": null,
} satisfies RentalPrice

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalPrice
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


