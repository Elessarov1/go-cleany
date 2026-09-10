
# RentalProperty


## Properties

Name | Type
------------ | -------------
`id` | number
`slug` | string
`titleRu` | string
`titleEn` | string
`descriptionEn` | string
`area` | string
`address` | string
`apartmentNumber` | string
`bedrooms` | number
`beds` | number
`bathrooms` | number
`maxGuests` | number
`areaSqm` | number
`floor` | number
`baseDailyPrice` | number
`currency` | string
`displayOrder` | number
`status` | string
`amenities` | Array&lt;string&gt;
`media` | [Array&lt;RentalPropertyMedia&gt;](RentalPropertyMedia.md)
`createdAt` | Date
`updatedAt` | Date

## Example

```typescript
import type { RentalProperty } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "slug": null,
  "titleRu": null,
  "titleEn": null,
  "descriptionEn": null,
  "area": null,
  "address": null,
  "apartmentNumber": null,
  "bedrooms": null,
  "beds": null,
  "bathrooms": null,
  "maxGuests": null,
  "areaSqm": null,
  "floor": null,
  "baseDailyPrice": null,
  "currency": null,
  "displayOrder": null,
  "status": null,
  "amenities": null,
  "media": null,
  "createdAt": null,
  "updatedAt": null,
} satisfies RentalProperty

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalProperty
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


