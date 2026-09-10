
# CleaningOrderQuote


## Properties

Name | Type
------------ | -------------
`basePrice` | number
`customerDiscount` | number
`finalCustomerPrice` | number
`customerDiscountType` | string
`currency` | string

## Example

```typescript
import type { CleaningOrderQuote } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "basePrice": null,
  "customerDiscount": null,
  "finalCustomerPrice": null,
  "customerDiscountType": null,
  "currency": null,
} satisfies CleaningOrderQuote

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningOrderQuote
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


