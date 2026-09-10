
# CustomerNotification


## Properties

Name | Type
------------ | -------------
`id` | number
`type` | string
`action` | [ActionTarget](ActionTarget.md)
`createdAt` | Date
`readAt` | Date

## Example

```typescript
import type { CustomerNotification } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "type": null,
  "action": null,
  "createdAt": null,
  "readAt": null,
} satisfies CustomerNotification

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CustomerNotification
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


