
# RentalSearchProperty


## Properties

Name | Type
------------ | -------------
`id` | number
`slug` | string
`titleRu` | string
`titleEn` | string
`descriptionEn` | string
`area` | string
`bedrooms` | number
`maxGuests` | number
`areaSqm` | number
`baseDailyPrice` | number
`currency` | string
`coverUrl` | string
`price` | [RentalPrice](RentalPrice.md)

## Example

```typescript
import type { RentalSearchProperty } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "slug": null,
  "titleRu": null,
  "titleEn": null,
  "descriptionEn": null,
  "area": null,
  "bedrooms": null,
  "maxGuests": null,
  "areaSqm": null,
  "baseDailyPrice": null,
  "currency": null,
  "coverUrl": null,
  "price": null,
} satisfies RentalSearchProperty

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalSearchProperty
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


