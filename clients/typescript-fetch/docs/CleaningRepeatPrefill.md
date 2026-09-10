
# CleaningRepeatPrefill


## Properties

Name | Type
------------ | -------------
`sourceOrderId` | number
`area` | string
`address` | string
`apartmentType` | string
`duplex` | boolean
`cleaningType` | string

## Example

```typescript
import type { CleaningRepeatPrefill } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "sourceOrderId": null,
  "area": null,
  "address": null,
  "apartmentType": null,
  "duplex": null,
  "cleaningType": null,
} satisfies CleaningRepeatPrefill

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningRepeatPrefill
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


