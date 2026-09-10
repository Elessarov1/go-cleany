
# ReferralSummary


## Properties

Name | Type
------------ | -------------
`referralCode` | string
`availableRewards` | number
`referralProgramUnlocked` | boolean

## Example

```typescript
import type { ReferralSummary } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "referralCode": null,
  "availableRewards": null,
  "referralProgramUnlocked": null,
} satisfies ReferralSummary

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ReferralSummary
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


