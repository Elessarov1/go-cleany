
# CleaningConfiguration


## Properties

Name | Type
------------ | -------------
`areas` | Array&lt;string&gt;
`apartmentTypes` | [Array&lt;ApartmentPrice&gt;](ApartmentPrice.md)
`duplexSurcharges` | { [key: string]: number; }
`bookingDaysAhead` | number
`currency` | string

## Example

```typescript
import type { CleaningConfiguration } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "areas": null,
  "apartmentTypes": null,
  "duplexSurcharges": null,
  "bookingDaysAhead": null,
  "currency": null,
} satisfies CleaningConfiguration

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningConfiguration
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


