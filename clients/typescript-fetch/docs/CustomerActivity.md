
# CustomerActivity


## Properties

Name | Type
------------ | -------------
`activeAndUpcoming` | [Array&lt;CustomerActivityItem&gt;](CustomerActivityItem.md)
`history` | [Array&lt;CustomerActivityItem&gt;](CustomerActivityItem.md)

## Example

```typescript
import type { CustomerActivity } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "activeAndUpcoming": null,
  "history": null,
} satisfies CustomerActivity

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CustomerActivity
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


