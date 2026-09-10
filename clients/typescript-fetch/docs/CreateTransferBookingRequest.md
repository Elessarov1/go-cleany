
# CreateTransferBookingRequest


## Properties

Name | Type
------------ | -------------
`direction` | string
`airportId` | number
`vehicleTypeId` | number
`pickupDate` | Date
`pickupTime` | string
`address` | string
`passengerCount` | number
`luggageCount` | number
`flightNumber` | string
`scheduledArrivalTime` | string
`phone` | string
`comment` | string
`repeatFromBookingId` | number
`rentalSource` | [RentalTransferSourceRequest](RentalTransferSourceRequest.md)
`benefit` | string

## Example

```typescript
import type { CreateTransferBookingRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "direction": null,
  "airportId": null,
  "vehicleTypeId": null,
  "pickupDate": null,
  "pickupTime": null,
  "address": null,
  "passengerCount": null,
  "luggageCount": null,
  "flightNumber": null,
  "scheduledArrivalTime": null,
  "phone": null,
  "comment": null,
  "repeatFromBookingId": null,
  "rentalSource": null,
  "benefit": null,
} satisfies CreateTransferBookingRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateTransferBookingRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


