
# NotificationPreferences


## Properties

Name | Type
------------ | -------------
`telegramEnabled` | boolean
`pushEnabled` | boolean

## Example

```typescript
import type { NotificationPreferences } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "telegramEnabled": null,
  "pushEnabled": null,
} satisfies NotificationPreferences

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as NotificationPreferences
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


