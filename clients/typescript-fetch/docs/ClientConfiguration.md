
# ClientConfiguration


## Properties

Name | Type
------------ | -------------
`environment` | string
`iosApplicationId` | string
`androidApplicationId` | string
`minimumIosVersion` | string
`minimumAndroidVersion` | string
`apiRevision` | string

## Example

```typescript
import type { ClientConfiguration } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "environment": null,
  "iosApplicationId": null,
  "androidApplicationId": null,
  "minimumIosVersion": null,
  "minimumAndroidVersion": null,
  "apiRevision": null,
} satisfies ClientConfiguration

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ClientConfiguration
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


