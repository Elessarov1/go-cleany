
# CustomerHomePrimaryAction


## Properties

Name | Type
------------ | -------------
`type` | string
`sourceService` | string
`sourceEntityId` | number
`targetService` | string
`relevantDate` | Date
`eligibleFrom` | Date
`expiresOn` | Date
`action` | [ActionTarget](ActionTarget.md)
`benefit` | [RentalTransferBenefit](RentalTransferBenefit.md)

## Example

```typescript
import type { CustomerHomePrimaryAction } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "type": null,
  "sourceService": null,
  "sourceEntityId": null,
  "targetService": null,
  "relevantDate": null,
  "eligibleFrom": null,
  "expiresOn": null,
  "action": null,
  "benefit": null,
} satisfies CustomerHomePrimaryAction

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CustomerHomePrimaryAction
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


