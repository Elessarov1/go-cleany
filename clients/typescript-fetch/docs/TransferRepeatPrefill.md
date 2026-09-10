
# TransferRepeatPrefill


## Properties

Name | Type
------------ | -------------
`sourceBookingId` | number
`direction` | string
`airportId` | number
`vehicleTypeId` | number
`address` | string
`passengerCount` | number
`luggageCount` | number

## Example

```typescript
import type { TransferRepeatPrefill } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "sourceBookingId": null,
  "direction": null,
  "airportId": null,
  "vehicleTypeId": null,
  "address": null,
  "passengerCount": null,
  "luggageCount": null,
} satisfies TransferRepeatPrefill

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferRepeatPrefill
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


