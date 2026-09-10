
# CreatedTransaction


## Properties

Name | Type
------------ | -------------
`id` | number
`money` | [Money](Money.md)

## Example

```typescript
import type { CreatedTransaction } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "money": null,
} satisfies CreatedTransaction

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreatedTransaction
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


