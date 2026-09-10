
# RentalBookingProperty


## Properties

Name | Type
------------ | -------------
`id` | number
`slug` | string
`titleRu` | string
`titleEn` | string
`area` | string

## Example

```typescript
import type { RentalBookingProperty } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "slug": null,
  "titleRu": null,
  "titleEn": null,
  "area": null,
} satisfies RentalBookingProperty

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalBookingProperty
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


