
# RentalTransferContext


## Properties

Name | Type
------------ | -------------
`rentalBookingId` | number
`transferFlowAvailable` | boolean
`options` | [Array&lt;RentalTransferContextOption&gt;](RentalTransferContextOption.md)

## Example

```typescript
import type { RentalTransferContext } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "rentalBookingId": null,
  "transferFlowAvailable": null,
  "options": null,
} satisfies RentalTransferContext

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalTransferContext
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


