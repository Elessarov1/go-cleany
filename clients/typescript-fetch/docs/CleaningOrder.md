
# CleaningOrder


## Properties

Name | Type
------------ | -------------
`id` | number
`communicationIdentityId` | number
`customerName` | string
`phone` | string
`area` | string
`address` | string
`apartmentType` | string
`duplex` | boolean
`cleaningType` | string
`price` | number
`basePrice` | number
`customerDiscount` | number
`finalCustomerPrice` | number
`customerDiscountType` | string
`currency` | string
`requestedDate` | Date
`customerComment` | string
`cleanerComment` | string
`cleanerTelegramUserId` | number
`status` | string
`createdAt` | Date
`acceptedAt` | Date
`completedAt` | Date
`report` | [CleaningReport](CleaningReport.md)
`money` | [Money](Money.md)

## Example

```typescript
import type { CleaningOrder } from '@locoplace/api-client'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "communicationIdentityId": null,
  "customerName": null,
  "phone": null,
  "area": null,
  "address": null,
  "apartmentType": null,
  "duplex": null,
  "cleaningType": null,
  "price": null,
  "basePrice": null,
  "customerDiscount": null,
  "finalCustomerPrice": null,
  "customerDiscountType": null,
  "currency": null,
  "requestedDate": null,
  "customerComment": null,
  "cleanerComment": null,
  "cleanerTelegramUserId": null,
  "status": null,
  "createdAt": null,
  "acceptedAt": null,
  "completedAt": null,
  "report": null,
  "money": null,
} satisfies CleaningOrder

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CleaningOrder
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


