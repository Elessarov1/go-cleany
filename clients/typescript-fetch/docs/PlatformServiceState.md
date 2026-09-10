
# PlatformServiceState


## Properties

Name | Type
------------ | -------------
`service` | string
`status` | string
`displayOrder` | number
`updatedAt` | Date
`updatedByCustomerId` | number
`version` | number

## Example

```typescript
import type { PlatformServiceState } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "service": null,
  "status": null,
  "displayOrder": null,
  "updatedAt": null,
  "updatedByCustomerId": null,
  "version": null,
} satisfies PlatformServiceState

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PlatformServiceState
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


