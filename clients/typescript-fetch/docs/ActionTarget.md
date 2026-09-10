
# ActionTarget


## Properties

Name | Type
------------ | -------------
`type` | string
`service` | string
`entityId` | number
`sourceOrderId` | number
`sourceBookingId` | number
`rentalBookingId` | number
`context` | string
`caseId` | number

## Example

```typescript
import type { ActionTarget } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "type": null,
  "service": null,
  "entityId": null,
  "sourceOrderId": null,
  "sourceBookingId": null,
  "rentalBookingId": null,
  "context": null,
  "caseId": null,
} satisfies ActionTarget

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ActionTarget
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


