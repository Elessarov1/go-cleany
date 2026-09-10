
# CustomerHome


## Properties

Name | Type
------------ | -------------
`hasActivity` | boolean
`activeTransaction` | [CustomerActivityItem](CustomerActivityItem.md)
`activeTransactionCount` | number
`primaryAction` | [CustomerHomePrimaryAction](CustomerHomePrimaryAction.md)
`repeatOpportunity` | [CustomerHomeRepeatOpportunity](CustomerHomeRepeatOpportunity.md)

## Example

```typescript
import type { CustomerHome } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "hasActivity": null,
  "activeTransaction": null,
  "activeTransactionCount": null,
  "primaryAction": null,
  "repeatOpportunity": null,
} satisfies CustomerHome

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CustomerHome
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


