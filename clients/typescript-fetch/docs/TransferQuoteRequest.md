
# TransferQuoteRequest


## Properties

Name | Type
------------ | -------------
`direction` | string
`airportId` | number
`vehicleTypeId` | number
`rentalSource` | [RentalTransferSourceRequest](RentalTransferSourceRequest.md)
`benefit` | string

## Example

```typescript
import type { TransferQuoteRequest } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "direction": null,
  "airportId": null,
  "vehicleTypeId": null,
  "rentalSource": null,
  "benefit": null,
} satisfies TransferQuoteRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferQuoteRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


