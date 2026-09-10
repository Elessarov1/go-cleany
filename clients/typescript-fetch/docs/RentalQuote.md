
# RentalQuote


## Properties

Name | Type
------------ | -------------
`property` | [RentalBookingProperty](RentalBookingProperty.md)
`criteria` | [RentalSearchCriteria](RentalSearchCriteria.md)
`price` | [RentalPrice](RentalPrice.md)

## Example

```typescript
import type { RentalQuote } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "property": null,
  "criteria": null,
  "price": null,
} satisfies RentalQuote

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalQuote
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


