
# CleaningReport


## Properties

Name | Type
------------ | -------------
`status` | string
`expiresAt` | Date
`retentionDays` | number
`cleanerComment` | string
`photos` | [Array&lt;CleaningReportPhoto&gt;](CleaningReportPhoto.md)

## Example

```typescript
import type { CleaningReport } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "status": null,
  "expiresAt": null,
  "retentionDays": null,
  "cleanerComment": null,
  "photos": null,
} satisfies CleaningReport

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningReport
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


