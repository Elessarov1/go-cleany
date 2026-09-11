
# RentalSearch


## Properties

Name | Type
------------ | -------------
`searchExecutionId` | string
`criteria` | [RentalSearchCriteria](RentalSearchCriteria.md)
`calculatedAt` | Date
`properties` | [Array&lt;RentalSearchProperty&gt;](RentalSearchProperty.md)
`nextCursor` | string
`hasMore` | boolean

## Example

```typescript
import type { RentalSearch } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "searchExecutionId": null,
  "criteria": null,
  "calculatedAt": null,
  "properties": null,
  "nextCursor": null,
  "hasMore": null,
} satisfies RentalSearch

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RentalSearch
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


