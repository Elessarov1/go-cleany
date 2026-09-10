
# CustomerActivityItem


## Properties

Name | Type
------------ | -------------
`service` | string
`entityId` | number
`status` | string
`titleRu` | string
`titleEn` | string
`subtitleRu` | string
`subtitleEn` | string
`scheduledDate` | Date
`scheduledEndDate` | Date
`scheduledTime` | string
`occurredAt` | Date
`money` | [Money](Money.md)
`action` | [ActionTarget](ActionTarget.md)

## Example

```typescript
import type { CustomerActivityItem } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "service": null,
  "entityId": null,
  "status": null,
  "titleRu": null,
  "titleEn": null,
  "subtitleRu": null,
  "subtitleEn": null,
  "scheduledDate": null,
  "scheduledEndDate": null,
  "scheduledTime": null,
  "occurredAt": null,
  "money": null,
  "action": null,
} satisfies CustomerActivityItem

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CustomerActivityItem
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


