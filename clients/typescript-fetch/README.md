# @locoplace/api-client@1.0.0-rc.1

A TypeScript SDK client for the loco-place.com API.

## Usage

First, install the SDK from npm.

```bash
npm install @locoplace/api-client --save
```

Next, try it out.


```ts
import {
  Configuration,
  AccountApi,
} from '@locoplace/api-client';
import type { ConfirmAccountDeletionRequest } from '@locoplace/api-client';

async function example() {
  console.log("🚀 Testing @locoplace/api-client SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
    // To configure API key authorization: cookieSession
    apiKey: "YOUR API KEY",
  });
  const api = new AccountApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // SensitiveProof
    sensitiveProof: ...,
  } satisfies ConfirmAccountDeletionRequest;

  try {
    const data = await api.confirmAccountDeletion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```


## Documentation

### API Endpoints

All URIs are relative to *https://loco-place.com*

| Class | Method | HTTP request | Description
| ----- | ------ | ------------ | -------------
*AccountApi* | [**confirmAccountDeletion**](docs/AccountApi.md#confirmaccountdeletion) | **POST** /api/v1/account/deletion-requests/{id}/confirm | 
*AccountApi* | [**confirmIdentityLink**](docs/AccountApi.md#confirmidentitylinkoperation) | **POST** /api/v1/account/identity-links/{id}/confirm | 
*AccountApi* | [**createAccountDeletionRequest**](docs/AccountApi.md#createaccountdeletionrequest) | **POST** /api/v1/account/deletion-requests | 
*AccountApi* | [**createIdentityLink**](docs/AccountApi.md#createidentitylinkoperation) | **POST** /api/v1/account/identity-links | 
*AccountApi* | [**getAccountIdentities**](docs/AccountApi.md#getaccountidentities) | **GET** /api/v1/account/identities | 
*AccountApi* | [**getCustomerActivity**](docs/AccountApi.md#getcustomeractivity) | **GET** /api/v1/account/activity | 
*AccountApi* | [**getCustomerHome**](docs/AccountApi.md#getcustomerhome) | **GET** /api/v1/account/home | 
*AccountApi* | [**getCustomerProfile**](docs/AccountApi.md#getcustomerprofile) | **GET** /api/v1/customers/me | 
*AccountApi* | [**getNotificationPreferences**](docs/AccountApi.md#getnotificationpreferences) | **GET** /api/v1/account/notification-preferences | 
*AccountApi* | [**getNotificationUnreadCount**](docs/AccountApi.md#getnotificationunreadcount) | **GET** /api/v1/account/notifications/unread-count | 
*AccountApi* | [**getNotifications**](docs/AccountApi.md#getnotifications) | **GET** /api/v1/account/notifications | 
*AccountApi* | [**markAllNotificationsRead**](docs/AccountApi.md#markallnotificationsread) | **POST** /api/v1/account/notifications/read-all | 
*AccountApi* | [**markNotificationRead**](docs/AccountApi.md#marknotificationread) | **POST** /api/v1/account/notifications/{notificationId}/read | 
*AccountApi* | [**registerCommunicationEndpoint**](docs/AccountApi.md#registercommunicationendpointoperation) | **POST** /api/v1/account/communication-endpoints | 
*AccountApi* | [**unlinkIdentity**](docs/AccountApi.md#unlinkidentity) | **DELETE** /api/v1/account/identities/{identityId} | 
*AccountApi* | [**unregisterCommunicationEndpoint**](docs/AccountApi.md#unregistercommunicationendpoint) | **DELETE** /api/v1/account/communication-endpoints | 
*AccountApi* | [**unregisterCommunicationEndpointById**](docs/AccountApi.md#unregistercommunicationendpointbyid) | **DELETE** /api/v1/account/communication-endpoints/{id} | 
*AccountApi* | [**updateNotificationPreferences**](docs/AccountApi.md#updatenotificationpreferences) | **PATCH** /api/v1/account/notification-preferences | 
*AccountApi* | [**verifyIdentityLink**](docs/AccountApi.md#verifyidentitylinkoperation) | **POST** /api/v1/account/identity-links/{id}/verify | 
*AuthenticationApi* | [**createNativeChallenge**](docs/AuthenticationApi.md#createnativechallengeoperation) | **POST** /api/v1/auth/native/challenges | 
*AuthenticationApi* | [**createTelegramLoginAttempt**](docs/AuthenticationApi.md#createtelegramloginattempt) | **POST** /api/v1/auth/native/telegram/attempts | 
*AuthenticationApi* | [**createTmaSession**](docs/AuthenticationApi.md#createtmasession) | **POST** /api/v1/auth/tma/session | 
*AuthenticationApi* | [**exchangeTelegramLoginAttempt**](docs/AuthenticationApi.md#exchangetelegramloginattemptoperation) | **POST** /api/v1/auth/native/telegram/attempts/{attemptId}/exchange | 
*AuthenticationApi* | [**getCsrfToken**](docs/AuthenticationApi.md#getcsrftoken) | **GET** /api/v1/auth/csrf | 
*AuthenticationApi* | [**getCurrentAuthentication**](docs/AuthenticationApi.md#getcurrentauthentication) | **GET** /api/v1/auth/me | 
*AuthenticationApi* | [**loginWithApple**](docs/AuthenticationApi.md#loginwithapple) | **POST** /api/v1/auth/native/apple | 
*AuthenticationApi* | [**loginWithGoogle**](docs/AuthenticationApi.md#loginwithgoogle) | **POST** /api/v1/auth/native/google | 
*AuthenticationApi* | [**logoutCurrentSession**](docs/AuthenticationApi.md#logoutcurrentsession) | **DELETE** /api/v1/auth/sessions/current | 
*AuthenticationApi* | [**refreshSession**](docs/AuthenticationApi.md#refreshsessionoperation) | **POST** /api/v1/auth/sessions/refresh | 
*AuthenticationApi* | [**revokeAllSessions**](docs/AuthenticationApi.md#revokeallsessions) | **DELETE** /api/v1/auth/sessions | 
*CleaningApi* | [**cancelCleaningOrder**](docs/CleaningApi.md#cancelcleaningorder) | **POST** /api/v1/cleaning/orders/{orderId}/cancel | 
*CleaningApi* | [**createCleaningOrder**](docs/CleaningApi.md#createcleaningorderoperation) | **POST** /api/v1/cleaning/orders | 
*CleaningApi* | [**getCleaningConfiguration**](docs/CleaningApi.md#getcleaningconfiguration) | **GET** /api/v1/cleaning/configuration | 
*CleaningApi* | [**getCleaningOrder**](docs/CleaningApi.md#getcleaningorder) | **GET** /api/v1/cleaning/orders/{orderId} | 
*CleaningApi* | [**getCleaningOrders**](docs/CleaningApi.md#getcleaningorders) | **GET** /api/v1/cleaning/orders | 
*CleaningApi* | [**getCleaningReferralSummary**](docs/CleaningApi.md#getcleaningreferralsummary) | **GET** /api/v1/referrals/me | 
*CleaningApi* | [**getCleaningRepeatPrefill**](docs/CleaningApi.md#getcleaningrepeatprefill) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-prefill | 
*CleaningApi* | [**getCleaningRepeatReminder**](docs/CleaningApi.md#getcleaningrepeatreminder) | **GET** /api/v1/cleaning/orders/{orderId}/repeat-reminder | 
*CleaningApi* | [**getCleaningReport**](docs/CleaningApi.md#getcleaningreport) | **GET** /api/v1/cleaning/orders/{orderId}/report | 
*CleaningApi* | [**getCleaningReportPhoto**](docs/CleaningApi.md#getcleaningreportphoto) | **GET** /api/v1/cleaning/orders/{orderId}/report/photos/{mediaId} | 
*CleaningApi* | [**quoteCleaningOrder**](docs/CleaningApi.md#quotecleaningorder) | **POST** /api/v1/cleaning/orders/quote | 
*CleaningApi* | [**recordCleaningRepeatShown**](docs/CleaningApi.md#recordcleaningrepeatshown) | **POST** /api/v1/cleaning/orders/{orderId}/repeat-shown | 
*CleaningApi* | [**updateCleaningRepeatReminder**](docs/CleaningApi.md#updatecleaningrepeatreminder) | **PUT** /api/v1/cleaning/orders/{orderId}/repeat-reminder | 
*ClientApi* | [**captureTelegramAcquisition**](docs/ClientApi.md#capturetelegramacquisition) | **POST** /api/v1/acquisition/telegram | 
*ClientApi* | [**getClientConfiguration**](docs/ClientApi.md#getclientconfiguration) | **GET** /api/v1/client/configuration | 
*PlatformApi* | [**getPlatformServices**](docs/PlatformApi.md#getplatformservices) | **GET** /api/v1/catalog/services | 
*RentalApi* | [**cancelRentalBooking**](docs/RentalApi.md#cancelrentalbooking) | **POST** /api/v1/rental/bookings/{bookingId}/cancel | 
*RentalApi* | [**createRentalBooking**](docs/RentalApi.md#createrentalbookingoperation) | **POST** /api/v1/rental/bookings | 
*RentalApi* | [**getRentalAvailability**](docs/RentalApi.md#getrentalavailability) | **GET** /api/v1/rental/properties/{propertyId}/availability | 
*RentalApi* | [**getRentalBooking**](docs/RentalApi.md#getrentalbooking) | **GET** /api/v1/rental/bookings/{bookingId} | 
*RentalApi* | [**getRentalBookings**](docs/RentalApi.md#getrentalbookings) | **GET** /api/v1/rental/bookings | 
*RentalApi* | [**getRentalCleaningContext**](docs/RentalApi.md#getrentalcleaningcontext) | **GET** /api/v1/rental/bookings/{bookingId}/cleaning-context | 
*RentalApi* | [**getRentalConfiguration**](docs/RentalApi.md#getrentalconfiguration) | **GET** /api/v1/rental/configuration | 
*RentalApi* | [**getRentalProperty**](docs/RentalApi.md#getrentalproperty) | **GET** /api/v1/rental/properties/{slug} | 
*RentalApi* | [**getRentalPropertyMedia**](docs/RentalApi.md#getrentalpropertymedia) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId} | 
*RentalApi* | [**getRentalPropertyMediaVariant**](docs/RentalApi.md#getrentalpropertymediavariant) | **GET** /api/v1/rental/properties/{propertyId}/media/{mediaId}/{variant} | 
*RentalApi* | [**getRentalTransferContext**](docs/RentalApi.md#getrentaltransfercontext) | **GET** /api/v1/rental/bookings/{bookingId}/transfer-context | 
*RentalApi* | [**getRentalTransferPrefill**](docs/RentalApi.md#getrentaltransferprefill) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/prefill | 
*RentalApi* | [**quoteRentalProperty**](docs/RentalApi.md#quoterentalproperty) | **GET** /api/v1/rental/properties/{propertyId}/quote | 
*RentalApi* | [**recordRentalFirstCard**](docs/RentalApi.md#recordrentalfirstcard) | **POST** /api/v1/rental/searches/{executionId}/first-card | 
*RentalApi* | [**recordRentalSearchOpened**](docs/RentalApi.md#recordrentalsearchopened) | **POST** /api/v1/rental/searches/{executionId}/opened | 
*RentalApi* | [**recordRentalTransferContextShown**](docs/RentalApi.md#recordrentaltransfercontextshown) | **POST** /api/v1/rental/bookings/{bookingId}/transfer-context/{context}/shown | 
*RentalApi* | [**searchRentalProperties**](docs/RentalApi.md#searchrentalproperties) | **GET** /api/v1/rental/search | 
*SupportApi* | [**createSupportCase**](docs/SupportApi.md#createsupportcaseoperation) | **POST** /api/v1/account/support/cases | 
*SupportApi* | [**getTransactionSupport**](docs/SupportApi.md#gettransactionsupport) | **GET** /api/v1/account/support/sources/{service}/{sourceEntityId} | 
*SupportApi* | [**submitTransactionFeedback**](docs/SupportApi.md#submittransactionfeedback) | **POST** /api/v1/account/support/feedback | 
*TransferApi* | [**cancelTransferBooking**](docs/TransferApi.md#canceltransferbooking) | **POST** /api/v1/transfer/bookings/{bookingId}/cancel | 
*TransferApi* | [**createTransferBooking**](docs/TransferApi.md#createtransferbookingoperation) | **POST** /api/v1/transfer/bookings | 
*TransferApi* | [**getTransferBooking**](docs/TransferApi.md#gettransferbooking) | **GET** /api/v1/transfer/bookings/{bookingId} | 
*TransferApi* | [**getTransferBookings**](docs/TransferApi.md#gettransferbookings) | **GET** /api/v1/transfer/bookings | 
*TransferApi* | [**getTransferConfiguration**](docs/TransferApi.md#gettransferconfiguration) | **GET** /api/v1/transfer/configuration | 
*TransferApi* | [**getTransferRepeatPrefill**](docs/TransferApi.md#gettransferrepeatprefill) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-prefill | 
*TransferApi* | [**quoteTransfer**](docs/TransferApi.md#quotetransfer) | **POST** /api/v1/transfer/quote | 
*TransferApi* | [**recordTransferRepeatShown**](docs/TransferApi.md#recordtransferrepeatshown) | **POST** /api/v1/transfer/bookings/{bookingId}/repeat-shown | 


### Models

- [AccountIdentities](docs/AccountIdentities.md)
- [AccountIdentity](docs/AccountIdentity.md)
- [AcquisitionCapture](docs/AcquisitionCapture.md)
- [ActionTarget](docs/ActionTarget.md)
- [ApartmentPrice](docs/ApartmentPrice.md)
- [ApiError](docs/ApiError.md)
- [CleaningConfiguration](docs/CleaningConfiguration.md)
- [CleaningOrder](docs/CleaningOrder.md)
- [CleaningOrderCursorPage](docs/CleaningOrderCursorPage.md)
- [CleaningOrderQuote](docs/CleaningOrderQuote.md)
- [CleaningOrderQuoteRequest](docs/CleaningOrderQuoteRequest.md)
- [CleaningRepeatPrefill](docs/CleaningRepeatPrefill.md)
- [CleaningRepeatReminder](docs/CleaningRepeatReminder.md)
- [CleaningRepeatReminderRequest](docs/CleaningRepeatReminderRequest.md)
- [CleaningReport](docs/CleaningReport.md)
- [CleaningReportPhoto](docs/CleaningReportPhoto.md)
- [ClientConfiguration](docs/ClientConfiguration.md)
- [CommunicationEndpoint](docs/CommunicationEndpoint.md)
- [ConfirmIdentityLinkRequest](docs/ConfirmIdentityLinkRequest.md)
- [CreateCleaningOrderRequest](docs/CreateCleaningOrderRequest.md)
- [CreateIdentityLinkRequest](docs/CreateIdentityLinkRequest.md)
- [CreateNativeChallengeRequest](docs/CreateNativeChallengeRequest.md)
- [CreateRentalBookingRequest](docs/CreateRentalBookingRequest.md)
- [CreateSupportCaseRequest](docs/CreateSupportCaseRequest.md)
- [CreateTransactionFeedbackRequest](docs/CreateTransactionFeedbackRequest.md)
- [CreateTransferBookingRequest](docs/CreateTransferBookingRequest.md)
- [CsrfToken](docs/CsrfToken.md)
- [CurrentAuthentication](docs/CurrentAuthentication.md)
- [CustomerActivity](docs/CustomerActivity.md)
- [CustomerActivityItem](docs/CustomerActivityItem.md)
- [CustomerHome](docs/CustomerHome.md)
- [CustomerHomePrimaryAction](docs/CustomerHomePrimaryAction.md)
- [CustomerHomeRepeatOpportunity](docs/CustomerHomeRepeatOpportunity.md)
- [CustomerNotification](docs/CustomerNotification.md)
- [CustomerProfile](docs/CustomerProfile.md)
- [ExchangeTelegramLoginAttemptRequest](docs/ExchangeTelegramLoginAttemptRequest.md)
- [IdentityLinkAttempt](docs/IdentityLinkAttempt.md)
- [IdentityProvider](docs/IdentityProvider.md)
- [LoginProviderAvailability](docs/LoginProviderAvailability.md)
- [LoginProviders](docs/LoginProviders.md)
- [Money](docs/Money.md)
- [NativeChallenge](docs/NativeChallenge.md)
- [NativeProviderLoginRequest](docs/NativeProviderLoginRequest.md)
- [NotificationCursorPage](docs/NotificationCursorPage.md)
- [NotificationPreferences](docs/NotificationPreferences.md)
- [NotificationUnreadCount](docs/NotificationUnreadCount.md)
- [OpenAdminTransactionAction](docs/OpenAdminTransactionAction.md)
- [OpenCleaningHistoryAction](docs/OpenCleaningHistoryAction.md)
- [OpenSupportCaseAction](docs/OpenSupportCaseAction.md)
- [OpenTransactionAction](docs/OpenTransactionAction.md)
- [PlatformServiceState](docs/PlatformServiceState.md)
- [ReauthenticationChallenge](docs/ReauthenticationChallenge.md)
- [ReferralSummary](docs/ReferralSummary.md)
- [RefreshSessionRequest](docs/RefreshSessionRequest.md)
- [RegisterCommunicationEndpointRequest](docs/RegisterCommunicationEndpointRequest.md)
- [RentalAvailability](docs/RentalAvailability.md)
- [RentalAvailabilityRange](docs/RentalAvailabilityRange.md)
- [RentalBooking](docs/RentalBooking.md)
- [RentalBookingCursorPage](docs/RentalBookingCursorPage.md)
- [RentalBookingProperty](docs/RentalBookingProperty.md)
- [RentalCleaningContext](docs/RentalCleaningContext.md)
- [RentalConfiguration](docs/RentalConfiguration.md)
- [RentalFirstCardRequest](docs/RentalFirstCardRequest.md)
- [RentalPrice](docs/RentalPrice.md)
- [RentalProperty](docs/RentalProperty.md)
- [RentalPropertyMedia](docs/RentalPropertyMedia.md)
- [RentalQuote](docs/RentalQuote.md)
- [RentalSearch](docs/RentalSearch.md)
- [RentalSearchCriteria](docs/RentalSearchCriteria.md)
- [RentalSearchProperty](docs/RentalSearchProperty.md)
- [RentalTermType](docs/RentalTermType.md)
- [RentalTransferBenefit](docs/RentalTransferBenefit.md)
- [RentalTransferContext](docs/RentalTransferContext.md)
- [RentalTransferContextOption](docs/RentalTransferContextOption.md)
- [RentalTransferPrefill](docs/RentalTransferPrefill.md)
- [RentalTransferSourceRequest](docs/RentalTransferSourceRequest.md)
- [RepeatCleaningAction](docs/RepeatCleaningAction.md)
- [RepeatTransferAction](docs/RepeatTransferAction.md)
- [SensitiveProof](docs/SensitiveProof.md)
- [SessionTokens](docs/SessionTokens.md)
- [StartRentalCleaningAction](docs/StartRentalCleaningAction.md)
- [StartRentalTransferAction](docs/StartRentalTransferAction.md)
- [SupportCase](docs/SupportCase.md)
- [TelegramAcquisitionRequest](docs/TelegramAcquisitionRequest.md)
- [TelegramLoginAttempt](docs/TelegramLoginAttempt.md)
- [TransactionFeedback](docs/TransactionFeedback.md)
- [TransactionSupport](docs/TransactionSupport.md)
- [TransferAirport](docs/TransferAirport.md)
- [TransferBooking](docs/TransferBooking.md)
- [TransferBookingCursorPage](docs/TransferBookingCursorPage.md)
- [TransferConfiguration](docs/TransferConfiguration.md)
- [TransferPrice](docs/TransferPrice.md)
- [TransferQuote](docs/TransferQuote.md)
- [TransferQuoteRequest](docs/TransferQuoteRequest.md)
- [TransferRepeatPrefill](docs/TransferRepeatPrefill.md)
- [TransferVehicleType](docs/TransferVehicleType.md)
- [VerifyIdentityLinkRequest](docs/VerifyIdentityLinkRequest.md)

### Authorization


Authentication schemes defined for the API:
<a id="bearerAuth"></a>
#### bearerAuth


- **Type**: HTTP Bearer Token authentication (opaque)
<a id="cookieSession"></a>
#### cookieSession


- **Type**: API key
- **API key parameter name**: `SESSION`
- **Location**: 
<a id="tmaBootstrap"></a>
#### tmaBootstrap


- **Type**: API key
- **API key parameter name**: `Authorization`
- **Location**: HTTP header

## About

This TypeScript SDK client supports the [Fetch API](https://fetch.spec.whatwg.org/)
and is automatically generated by the
[OpenAPI Generator](https://openapi-generator.tech) project:

- API version: `1.0.0-rc.1`
- Package version: `1.0.0-rc.1`
- Generator version: `7.25.0`
- Build package: `org.openapitools.codegen.languages.TypeScriptFetchClientCodegen`

The generated npm module supports the following:

- Environments
  * Node.js
  * Webpack
  * Browserify
- Language levels
  * ES5 - you must have a Promises/A+ library installed
  * ES6
- Module systems
  * CommonJS
  * ES6 module system


## Development

### Building

To build the TypeScript source code, you need to have Node.js and npm installed.
After cloning the repository, navigate to the project directory and run:

```bash
npm install
npm run build
```

### Publishing

Once you've built the package, you can publish it to npm:

```bash
npm publish
```

## License

[]()
