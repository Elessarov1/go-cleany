
# TransferPrice


## Properties

Name | Type
------------ | -------------
`airportId` | number
`vehicleTypeId` | number
`direction` | string
`amount` | number
`currency` | string

## Example

```typescript
import type { TransferPrice } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "airportId": null,
  "vehicleTypeId": null,
  "direction": null,
  "amount": null,
  "currency": null,
} satisfies TransferPrice

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferPrice
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


