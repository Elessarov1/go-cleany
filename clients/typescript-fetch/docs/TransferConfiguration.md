
# TransferConfiguration


## Properties

Name | Type
------------ | -------------
`earliestBookingDate` | Date
`latestBookingDate` | Date
`timeSlotMinutes` | number
`airports` | [Array&lt;TransferAirport&gt;](TransferAirport.md)
`vehicleTypes` | [Array&lt;TransferVehicleType&gt;](TransferVehicleType.md)
`prices` | [Array&lt;TransferPrice&gt;](TransferPrice.md)

## Example

```typescript
import type { TransferConfiguration } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "earliestBookingDate": null,
  "latestBookingDate": null,
  "timeSlotMinutes": null,
  "airports": null,
  "vehicleTypes": null,
  "prices": null,
} satisfies TransferConfiguration

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferConfiguration
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


