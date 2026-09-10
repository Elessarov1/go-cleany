//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_import

import 'package:one_of_serializer/any_of_serializer.dart';
import 'package:one_of_serializer/one_of_serializer.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:built_value/standard_json_plugin.dart';
import 'package:built_value/iso_8601_date_time_serializer.dart';
import 'package:loco_place_api/src/date_serializer.dart';
import 'package:loco_place_api/src/model/date.dart';

import 'package:loco_place_api/src/model/account_identities.dart';
import 'package:loco_place_api/src/model/account_identity.dart';
import 'package:loco_place_api/src/model/acquisition_capture.dart';
import 'package:loco_place_api/src/model/action_target.dart';
import 'package:loco_place_api/src/model/apartment_price.dart';
import 'package:loco_place_api/src/model/api_error.dart';
import 'package:loco_place_api/src/model/cleaning_configuration.dart';
import 'package:loco_place_api/src/model/cleaning_order.dart';
import 'package:loco_place_api/src/model/cleaning_order_cursor_page.dart';
import 'package:loco_place_api/src/model/cleaning_order_quote.dart';
import 'package:loco_place_api/src/model/cleaning_order_quote_request.dart';
import 'package:loco_place_api/src/model/cleaning_repeat_prefill.dart';
import 'package:loco_place_api/src/model/cleaning_repeat_reminder.dart';
import 'package:loco_place_api/src/model/cleaning_repeat_reminder_request.dart';
import 'package:loco_place_api/src/model/cleaning_report.dart';
import 'package:loco_place_api/src/model/cleaning_report_photo.dart';
import 'package:loco_place_api/src/model/client_configuration.dart';
import 'package:loco_place_api/src/model/communication_endpoint.dart';
import 'package:loco_place_api/src/model/confirm_identity_link_request.dart';
import 'package:loco_place_api/src/model/create_cleaning_order_request.dart';
import 'package:loco_place_api/src/model/create_identity_link_request.dart';
import 'package:loco_place_api/src/model/create_native_challenge_request.dart';
import 'package:loco_place_api/src/model/create_rental_booking_request.dart';
import 'package:loco_place_api/src/model/create_support_case_request.dart';
import 'package:loco_place_api/src/model/create_transaction_feedback_request.dart';
import 'package:loco_place_api/src/model/create_transfer_booking_request.dart';
import 'package:loco_place_api/src/model/csrf_token.dart';
import 'package:loco_place_api/src/model/current_authentication.dart';
import 'package:loco_place_api/src/model/customer_activity.dart';
import 'package:loco_place_api/src/model/customer_activity_item.dart';
import 'package:loco_place_api/src/model/customer_home.dart';
import 'package:loco_place_api/src/model/customer_home_primary_action.dart';
import 'package:loco_place_api/src/model/customer_home_repeat_opportunity.dart';
import 'package:loco_place_api/src/model/customer_notification.dart';
import 'package:loco_place_api/src/model/customer_profile.dart';
import 'package:loco_place_api/src/model/exchange_telegram_login_attempt_request.dart';
import 'package:loco_place_api/src/model/identity_link_attempt.dart';
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:loco_place_api/src/model/login_provider_availability.dart';
import 'package:loco_place_api/src/model/login_providers.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:loco_place_api/src/model/native_challenge.dart';
import 'package:loco_place_api/src/model/native_provider_login_request.dart';
import 'package:loco_place_api/src/model/notification_cursor_page.dart';
import 'package:loco_place_api/src/model/notification_preferences.dart';
import 'package:loco_place_api/src/model/notification_unread_count.dart';
import 'package:loco_place_api/src/model/open_admin_transaction_action.dart';
import 'package:loco_place_api/src/model/open_cleaning_history_action.dart';
import 'package:loco_place_api/src/model/open_support_case_action.dart';
import 'package:loco_place_api/src/model/open_transaction_action.dart';
import 'package:loco_place_api/src/model/platform_service_state.dart';
import 'package:loco_place_api/src/model/reauthentication_challenge.dart';
import 'package:loco_place_api/src/model/referral_summary.dart';
import 'package:loco_place_api/src/model/refresh_session_request.dart';
import 'package:loco_place_api/src/model/register_communication_endpoint_request.dart';
import 'package:loco_place_api/src/model/rental_availability.dart';
import 'package:loco_place_api/src/model/rental_availability_range.dart';
import 'package:loco_place_api/src/model/rental_booking.dart';
import 'package:loco_place_api/src/model/rental_booking_cursor_page.dart';
import 'package:loco_place_api/src/model/rental_booking_property.dart';
import 'package:loco_place_api/src/model/rental_cleaning_context.dart';
import 'package:loco_place_api/src/model/rental_configuration.dart';
import 'package:loco_place_api/src/model/rental_first_card_request.dart';
import 'package:loco_place_api/src/model/rental_price.dart';
import 'package:loco_place_api/src/model/rental_property.dart';
import 'package:loco_place_api/src/model/rental_property_media.dart';
import 'package:loco_place_api/src/model/rental_quote.dart';
import 'package:loco_place_api/src/model/rental_search.dart';
import 'package:loco_place_api/src/model/rental_search_criteria.dart';
import 'package:loco_place_api/src/model/rental_search_property.dart';
import 'package:loco_place_api/src/model/rental_term_type.dart';
import 'package:loco_place_api/src/model/rental_transfer_benefit.dart';
import 'package:loco_place_api/src/model/rental_transfer_context.dart';
import 'package:loco_place_api/src/model/rental_transfer_context_option.dart';
import 'package:loco_place_api/src/model/rental_transfer_prefill.dart';
import 'package:loco_place_api/src/model/rental_transfer_source_request.dart';
import 'package:loco_place_api/src/model/repeat_cleaning_action.dart';
import 'package:loco_place_api/src/model/repeat_transfer_action.dart';
import 'package:loco_place_api/src/model/sensitive_proof.dart';
import 'package:loco_place_api/src/model/session_tokens.dart';
import 'package:loco_place_api/src/model/start_rental_cleaning_action.dart';
import 'package:loco_place_api/src/model/start_rental_transfer_action.dart';
import 'package:loco_place_api/src/model/support_case.dart';
import 'package:loco_place_api/src/model/telegram_acquisition_request.dart';
import 'package:loco_place_api/src/model/telegram_login_attempt.dart';
import 'package:loco_place_api/src/model/transaction_feedback.dart';
import 'package:loco_place_api/src/model/transaction_support.dart';
import 'package:loco_place_api/src/model/transfer_airport.dart';
import 'package:loco_place_api/src/model/transfer_booking.dart';
import 'package:loco_place_api/src/model/transfer_booking_cursor_page.dart';
import 'package:loco_place_api/src/model/transfer_configuration.dart';
import 'package:loco_place_api/src/model/transfer_price.dart';
import 'package:loco_place_api/src/model/transfer_quote.dart';
import 'package:loco_place_api/src/model/transfer_quote_request.dart';
import 'package:loco_place_api/src/model/transfer_repeat_prefill.dart';
import 'package:loco_place_api/src/model/transfer_vehicle_type.dart';
import 'package:loco_place_api/src/model/verify_identity_link_request.dart';

part 'serializers.g.dart';

@SerializersFor([
  AccountIdentities,
  AccountIdentity,
  AcquisitionCapture,
  ActionTarget,
  ApartmentPrice,
  ApiError,
  CleaningConfiguration,
  CleaningOrder,
  CleaningOrderCursorPage,
  CleaningOrderQuote,
  CleaningOrderQuoteRequest,
  CleaningRepeatPrefill,
  CleaningRepeatReminder,
  CleaningRepeatReminderRequest,
  CleaningReport,
  CleaningReportPhoto,
  ClientConfiguration,
  CommunicationEndpoint,
  ConfirmIdentityLinkRequest,
  CreateCleaningOrderRequest,
  CreateIdentityLinkRequest,
  CreateNativeChallengeRequest,
  CreateRentalBookingRequest,
  CreateSupportCaseRequest,
  CreateTransactionFeedbackRequest,
  CreateTransferBookingRequest,
  CsrfToken,
  CurrentAuthentication,
  CustomerActivity,
  CustomerActivityItem,
  CustomerHome,
  CustomerHomePrimaryAction,
  CustomerHomeRepeatOpportunity,
  CustomerNotification,
  CustomerProfile,
  ExchangeTelegramLoginAttemptRequest,
  IdentityLinkAttempt,
  IdentityProvider,
  LoginProviderAvailability,
  LoginProviders,
  Money,
  NativeChallenge,
  NativeProviderLoginRequest,
  NotificationCursorPage,
  NotificationPreferences,
  NotificationUnreadCount,
  OpenAdminTransactionAction,
  OpenCleaningHistoryAction,
  OpenSupportCaseAction,
  OpenTransactionAction,
  PlatformServiceState,
  ReauthenticationChallenge,
  ReferralSummary,
  RefreshSessionRequest,
  RegisterCommunicationEndpointRequest,
  RentalAvailability,
  RentalAvailabilityRange,
  RentalBooking,
  RentalBookingCursorPage,
  RentalBookingProperty,
  RentalCleaningContext,
  RentalConfiguration,
  RentalFirstCardRequest,
  RentalPrice,
  RentalProperty,
  RentalPropertyMedia,
  RentalQuote,
  RentalSearch,
  RentalSearchCriteria,
  RentalSearchProperty,
  RentalTermType,
  RentalTransferBenefit,
  RentalTransferContext,
  RentalTransferContextOption,
  RentalTransferPrefill,
  RentalTransferSourceRequest,
  RepeatCleaningAction,
  RepeatTransferAction,
  SensitiveProof,
  SessionTokens,
  StartRentalCleaningAction,
  StartRentalTransferAction,
  SupportCase,
  TelegramAcquisitionRequest,
  TelegramLoginAttempt,
  TransactionFeedback,
  TransactionSupport,
  TransferAirport,
  TransferBooking,
  TransferBookingCursorPage,
  TransferConfiguration,
  TransferPrice,
  TransferQuote,
  TransferQuoteRequest,
  TransferRepeatPrefill,
  TransferVehicleType,
  VerifyIdentityLinkRequest,
])
Serializers serializers = (_$serializers.toBuilder()
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(CustomerNotification)]),
        () => ListBuilder<CustomerNotification>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(CustomerActivityItem)]),
        () => ListBuilder<CustomerActivityItem>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltMap, [FullType(String), FullType(String)]),
        () => MapBuilder<String, String>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(RentalTransferContextOption)]),
        () => ListBuilder<RentalTransferContextOption>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltSet, [FullType(String)]),
        () => SetBuilder<String>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(CleaningOrder)]),
        () => ListBuilder<CleaningOrder>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(RentalPropertyMedia)]),
        () => ListBuilder<RentalPropertyMedia>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(TransferAirport)]),
        () => ListBuilder<TransferAirport>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(AccountIdentity)]),
        () => ListBuilder<AccountIdentity>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(RentalAvailabilityRange)]),
        () => ListBuilder<RentalAvailabilityRange>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(TransferPrice)]),
        () => ListBuilder<TransferPrice>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltMap, [FullType(String), FullType(num)]),
        () => MapBuilder<String, num>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(TransferVehicleType)]),
        () => ListBuilder<TransferVehicleType>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(CleaningReportPhoto)]),
        () => ListBuilder<CleaningReportPhoto>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(ApartmentPrice)]),
        () => ListBuilder<ApartmentPrice>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(TransferBooking)]),
        () => ListBuilder<TransferBooking>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(RentalBooking)]),
        () => ListBuilder<RentalBooking>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(String)]),
        () => ListBuilder<String>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(RentalSearchProperty)]),
        () => ListBuilder<RentalSearchProperty>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(PlatformServiceState)]),
        () => ListBuilder<PlatformServiceState>(),
      )
      ..add(const OneOfSerializer())
      ..add(const AnyOfSerializer())
      ..add(const DateSerializer())
      ..add(Iso8601DateTimeSerializer())
    ).build();

Serializers standardSerializers =
    (serializers.toBuilder()..addPlugin(StandardJsonPlugin())).build();
