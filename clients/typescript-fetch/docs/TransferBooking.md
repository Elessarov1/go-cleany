
# TransferBooking


## Properties

Name | Type
------------ | -------------
`id` | number
`direction` | string
`airportCode` | string
`airportNameRu` | string
`airportNameEn` | string
`vehicleCode` | string
`vehicleNameRu` | string
`vehicleNameEn` | string
`pickupDate` | Date
`pickupTime` | string
`address` | string
`passengerCount` | number
`luggageCount` | number
`flightNumber` | string
`scheduledArrivalTime` | string
`customerName` | string
`phone` | string
`comment` | string
`basePriceAmount` | number
`discountAmount` | number
`priceAmount` | number
`priceCurrency` | string
`appliedBenefit` | string
`benefitRate` | number
`status` | string
`driverId` | number
`driverName` | string
`createdAt` | Date
`confirmedAt` | Date
`completedAt` | Date
`cancelledAt` | Date
`rejectedAt` | Date
`statusReason` | string
`money` | [Money](Money.md)

## Example

```typescript
import type { TransferBooking } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "direction": null,
  "airportCode": null,
  "airportNameRu": null,
  "airportNameEn": null,
  "vehicleCode": null,
  "vehicleNameRu": null,
  "vehicleNameEn": null,
  "pickupDate": null,
  "pickupTime": null,
  "address": null,
  "passengerCount": null,
  "luggageCount": null,
  "flightNumber": null,
  "scheduledArrivalTime": null,
  "customerName": null,
  "phone": null,
  "comment": null,
  "basePriceAmount": null,
  "discountAmount": null,
  "priceAmount": null,
  "priceCurrency": null,
  "appliedBenefit": null,
  "benefitRate": null,
  "status": null,
  "driverId": null,
  "driverName": null,
  "createdAt": null,
  "confirmedAt": null,
  "completedAt": null,
  "cancelledAt": null,
  "rejectedAt": null,
  "statusReason": null,
  "money": null,
} satisfies TransferBooking

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferBooking
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


