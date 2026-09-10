
# RentalConfiguration


## Properties

Name | Type
------------ | -------------
`minStayDays` | number
`longTermMinDays` | number
`longTermDiscountRate` | number
`maxStayDays` | number
`bookingStartMonthsAhead` | number
`maxActiveBookingsPerCustomer` | number
`today` | Date
`latestCheckInDate` | Date

## Example

```typescript
import type { RentalConfiguration } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "minStayDays": null,
  "longTermMinDays": null,
  "longTermDiscountRate": null,
  "maxStayDays": null,
  "bookingStartMonthsAhead": null,
  "maxActiveBookingsPerCustomer": null,
  "today": null,
  "latestCheckInDate": null,
} satisfies RentalConfiguration

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalConfiguration
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


