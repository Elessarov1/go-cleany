
# RentalPropertyMedia


## Properties

Name | Type
------------ | -------------
`id` | number
`mediaAssetId` | number
`sortOrder` | number
`cover` | boolean
`url` | string
`cardUrl` | string
`thumbnailUrl` | string

## Example

```typescript
import type { RentalPropertyMedia } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "mediaAssetId": null,
  "sortOrder": null,
  "cover": null,
  "url": null,
  "cardUrl": null,
  "thumbnailUrl": null,
} satisfies RentalPropertyMedia

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalPropertyMedia
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


