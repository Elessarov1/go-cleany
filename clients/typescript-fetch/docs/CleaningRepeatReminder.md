
# CleaningRepeatReminder


## Properties

Name | Type
------------ | -------------
`selection` | string
`status` | string
`scheduledDate` | Date
`notifiedAt` | Date
`editable` | boolean

## Example

```typescript
import type { CleaningRepeatReminder } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "selection": null,
  "status": null,
  "scheduledDate": null,
  "notifiedAt": null,
  "editable": null,
} satisfies CleaningRepeatReminder

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningRepeatReminder
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


