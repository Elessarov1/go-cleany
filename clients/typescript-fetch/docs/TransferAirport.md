
# TransferAirport


## Properties

Name | Type
------------ | -------------
`id` | number
`code` | string
`nameRu` | string
`nameEn` | string

## Example

```typescript
import type { TransferAirport } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "code": null,
  "nameRu": null,
  "nameEn": null,
} satisfies TransferAirport

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TransferAirport
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


