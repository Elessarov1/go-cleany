
# StartRentalTransferAction


## Properties

Name | Type
------------ | -------------
`type` | string
`rentalBookingId` | number
`context` | string

## Example

```typescript
import type { StartRentalTransferAction } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "type": null,
  "rentalBookingId": null,
  "context": null,
} satisfies StartRentalTransferAction

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as StartRentalTransferAction
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


