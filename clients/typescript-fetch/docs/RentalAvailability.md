
# RentalAvailability


## Properties

Name | Type
------------ | -------------
`propertyId` | number
`fromDate` | Date
`toDate` | Date
`unavailableRanges` | [Array&lt;RentalAvailabilityRange&gt;](RentalAvailabilityRange.md)

## Example

```typescript
import type { RentalAvailability } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "propertyId": null,
  "fromDate": null,
  "toDate": null,
  "unavailableRanges": null,
} satisfies RentalAvailability

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalAvailability
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


