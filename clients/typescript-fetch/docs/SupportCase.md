
# SupportCase


## Properties

Name | Type
------------ | -------------
`id` | number
`service` | string
`sourceEntityId` | number
`category` | string
`status` | string
`description` | string
`createdAt` | Date
`resolvedAt` | Date
`resolutionComment` | string

## Example

```typescript
import type { SupportCase } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "service": null,
  "sourceEntityId": null,
  "category": null,
  "status": null,
  "description": null,
  "createdAt": null,
  "resolvedAt": null,
  "resolutionComment": null,
} satisfies SupportCase

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SupportCase
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


