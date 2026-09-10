//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/open_admin_transaction_action.dart';
import 'package:loco_place_api/src/model/open_support_case_action.dart';
import 'package:loco_place_api/src/model/start_rental_cleaning_action.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/repeat_cleaning_action.dart';
import 'package:loco_place_api/src/model/repeat_transfer_action.dart';
import 'package:loco_place_api/src/model/open_cleaning_history_action.dart';
import 'package:loco_place_api/src/model/start_rental_transfer_action.dart';
import 'package:loco_place_api/src/model/open_transaction_action.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';
import 'package:one_of/one_of.dart';

part 'action_target.g.dart';

/// ActionTarget
///
/// Properties:
/// * [type] 
/// * [service] 
/// * [entityId] 
/// * [sourceOrderId] 
/// * [sourceBookingId] 
/// * [rentalBookingId] 
/// * [context] 
/// * [caseId] 
@BuiltValue()
abstract class ActionTarget implements Built<ActionTarget, ActionTargetBuilder> {
  /// One Of [OpenAdminTransactionAction], [OpenCleaningHistoryAction], [OpenSupportCaseAction], [OpenTransactionAction], [RepeatCleaningAction], [RepeatTransferAction], [StartRentalCleaningAction], [StartRentalTransferAction]
  OneOf get oneOf;

  static const String discriminatorFieldName = r'type';

  static const Map<String, Type> discriminatorMapping = {
    r'OPEN_ADMIN_TRANSACTION': OpenAdminTransactionAction,
    r'OPEN_CLEANING_HISTORY': OpenCleaningHistoryAction,
    r'OPEN_SUPPORT_CASE': OpenSupportCaseAction,
    r'OPEN_TRANSACTION': OpenTransactionAction,
    r'REPEAT_CLEANING': RepeatCleaningAction,
    r'REPEAT_TRANSFER': RepeatTransferAction,
    r'START_RENTAL_CLEANING': StartRentalCleaningAction,
    r'START_RENTAL_TRANSFER': StartRentalTransferAction,
  };

  ActionTarget._();

  factory ActionTarget([void updates(ActionTargetBuilder b)]) = _$ActionTarget;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ActionTargetBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ActionTarget> get serializer => _$ActionTargetSerializer();
}

extension ActionTargetDiscriminatorExt on ActionTarget {
    String? get discriminatorValue {
        if (this is OpenAdminTransactionAction) {
            return r'OPEN_ADMIN_TRANSACTION';
        }
        if (this is OpenCleaningHistoryAction) {
            return r'OPEN_CLEANING_HISTORY';
        }
        if (this is OpenSupportCaseAction) {
            return r'OPEN_SUPPORT_CASE';
        }
        if (this is OpenTransactionAction) {
            return r'OPEN_TRANSACTION';
        }
        if (this is RepeatCleaningAction) {
            return r'REPEAT_CLEANING';
        }
        if (this is RepeatTransferAction) {
            return r'REPEAT_TRANSFER';
        }
        if (this is StartRentalCleaningAction) {
            return r'START_RENTAL_CLEANING';
        }
        if (this is StartRentalTransferAction) {
            return r'START_RENTAL_TRANSFER';
        }
        return null;
    }
}
extension ActionTargetBuilderDiscriminatorExt on ActionTargetBuilder {
    String? get discriminatorValue {
        if (this is OpenAdminTransactionActionBuilder) {
            return r'OPEN_ADMIN_TRANSACTION';
        }
        if (this is OpenCleaningHistoryActionBuilder) {
            return r'OPEN_CLEANING_HISTORY';
        }
        if (this is OpenSupportCaseActionBuilder) {
            return r'OPEN_SUPPORT_CASE';
        }
        if (this is OpenTransactionActionBuilder) {
            return r'OPEN_TRANSACTION';
        }
        if (this is RepeatCleaningActionBuilder) {
            return r'REPEAT_CLEANING';
        }
        if (this is RepeatTransferActionBuilder) {
            return r'REPEAT_TRANSFER';
        }
        if (this is StartRentalCleaningActionBuilder) {
            return r'START_RENTAL_CLEANING';
        }
        if (this is StartRentalTransferActionBuilder) {
            return r'START_RENTAL_TRANSFER';
        }
        return null;
    }
}

class _$ActionTargetSerializer implements PrimitiveSerializer<ActionTarget> {
  @override
  final Iterable<Type> types = const [ActionTarget, _$ActionTarget];

  @override
  final String wireName = r'ActionTarget';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ActionTarget object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
  }

  @override
  Object serialize(
    Serializers serializers,
    ActionTarget object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final oneOf = object.oneOf;
    return serializers.serialize(oneOf.value, specifiedType: FullType(oneOf.valueType))!;
  }

  @override
  ActionTarget deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ActionTargetBuilder();
    Object? oneOfDataSrc;
    final serializedList = (serialized as Iterable<Object?>).toList();
    final discIndex = serializedList.indexOf(ActionTarget.discriminatorFieldName) + 1;
    final discValue = serializers.deserialize(serializedList[discIndex], specifiedType: FullType(String)) as String;
    oneOfDataSrc = serialized;
    final oneOfTypes = [OpenAdminTransactionAction, OpenCleaningHistoryAction, OpenSupportCaseAction, OpenTransactionAction, RepeatCleaningAction, RepeatTransferAction, StartRentalCleaningAction, StartRentalTransferAction, ];
    Object oneOfResult;
    Type oneOfType;
    switch (discValue) {
      case r'OPEN_ADMIN_TRANSACTION':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(OpenAdminTransactionAction),
        ) as OpenAdminTransactionAction;
        oneOfType = OpenAdminTransactionAction;
        break;
      case r'OPEN_CLEANING_HISTORY':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(OpenCleaningHistoryAction),
        ) as OpenCleaningHistoryAction;
        oneOfType = OpenCleaningHistoryAction;
        break;
      case r'OPEN_SUPPORT_CASE':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(OpenSupportCaseAction),
        ) as OpenSupportCaseAction;
        oneOfType = OpenSupportCaseAction;
        break;
      case r'OPEN_TRANSACTION':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(OpenTransactionAction),
        ) as OpenTransactionAction;
        oneOfType = OpenTransactionAction;
        break;
      case r'REPEAT_CLEANING':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(RepeatCleaningAction),
        ) as RepeatCleaningAction;
        oneOfType = RepeatCleaningAction;
        break;
      case r'REPEAT_TRANSFER':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(RepeatTransferAction),
        ) as RepeatTransferAction;
        oneOfType = RepeatTransferAction;
        break;
      case r'START_RENTAL_CLEANING':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(StartRentalCleaningAction),
        ) as StartRentalCleaningAction;
        oneOfType = StartRentalCleaningAction;
        break;
      case r'START_RENTAL_TRANSFER':
        oneOfResult = serializers.deserialize(
          oneOfDataSrc,
          specifiedType: FullType(StartRentalTransferAction),
        ) as StartRentalTransferAction;
        oneOfType = StartRentalTransferAction;
        break;
      default:
        throw UnsupportedError("Couldn't deserialize oneOf for the discriminator value: ${discValue}");
    }
    result.oneOf = OneOfDynamic(typeIndex: oneOfTypes.indexOf(oneOfType), types: oneOfTypes, value: oneOfResult);
    return result.build();
  }
}


class ActionTargetTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN_TRANSACTION')
  static const ActionTargetTypeEnum OPEN_TRANSACTION = _$actionTargetTypeEnum_OPEN_TRANSACTION;
  @BuiltValueEnumConst(wireName: r'OPEN_CLEANING_HISTORY')
  static const ActionTargetTypeEnum OPEN_CLEANING_HISTORY = _$actionTargetTypeEnum_OPEN_CLEANING_HISTORY;
  @BuiltValueEnumConst(wireName: r'REPEAT_CLEANING')
  static const ActionTargetTypeEnum REPEAT_CLEANING = _$actionTargetTypeEnum_REPEAT_CLEANING;
  @BuiltValueEnumConst(wireName: r'REPEAT_TRANSFER')
  static const ActionTargetTypeEnum REPEAT_TRANSFER = _$actionTargetTypeEnum_REPEAT_TRANSFER;
  @BuiltValueEnumConst(wireName: r'START_RENTAL_CLEANING')
  static const ActionTargetTypeEnum START_RENTAL_CLEANING = _$actionTargetTypeEnum_START_RENTAL_CLEANING;
  @BuiltValueEnumConst(wireName: r'START_RENTAL_TRANSFER')
  static const ActionTargetTypeEnum START_RENTAL_TRANSFER = _$actionTargetTypeEnum_START_RENTAL_TRANSFER;
  @BuiltValueEnumConst(wireName: r'OPEN_ADMIN_TRANSACTION')
  static const ActionTargetTypeEnum OPEN_ADMIN_TRANSACTION = _$actionTargetTypeEnum_OPEN_ADMIN_TRANSACTION;
  @BuiltValueEnumConst(wireName: r'OPEN_SUPPORT_CASE')
  static const ActionTargetTypeEnum OPEN_SUPPORT_CASE = _$actionTargetTypeEnum_OPEN_SUPPORT_CASE;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const ActionTargetTypeEnum unknownDefaultOpenApi = _$actionTargetTypeEnum_unknownDefaultOpenApi;

  static Serializer<ActionTargetTypeEnum> get serializer => _$actionTargetTypeEnumSerializer;

  const ActionTargetTypeEnum._(String name): super(name);

  static BuiltSet<ActionTargetTypeEnum> get values => _$actionTargetTypeEnumValues;
  static ActionTargetTypeEnum valueOf(String name) => _$actionTargetTypeEnumValueOf(name);
}

class ActionTargetServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const ActionTargetServiceEnum CLEANING = _$actionTargetServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const ActionTargetServiceEnum RENTAL = _$actionTargetServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const ActionTargetServiceEnum TRANSFER = _$actionTargetServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const ActionTargetServiceEnum UNKNOWN = _$actionTargetServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const ActionTargetServiceEnum unknownDefaultOpenApi = _$actionTargetServiceEnum_unknownDefaultOpenApi;

  static Serializer<ActionTargetServiceEnum> get serializer => _$actionTargetServiceEnumSerializer;

  const ActionTargetServiceEnum._(String name): super(name);

  static BuiltSet<ActionTargetServiceEnum> get values => _$actionTargetServiceEnumValues;
  static ActionTargetServiceEnum valueOf(String name) => _$actionTargetServiceEnumValueOf(name);
}

class ActionTargetContextEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ARRIVAL')
  static const ActionTargetContextEnum ARRIVAL = _$actionTargetContextEnum_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'CHECKOUT')
  static const ActionTargetContextEnum CHECKOUT = _$actionTargetContextEnum_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const ActionTargetContextEnum UNKNOWN = _$actionTargetContextEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const ActionTargetContextEnum unknownDefaultOpenApi = _$actionTargetContextEnum_unknownDefaultOpenApi;

  static Serializer<ActionTargetContextEnum> get serializer => _$actionTargetContextEnumSerializer;

  const ActionTargetContextEnum._(String name): super(name);

  static BuiltSet<ActionTargetContextEnum> get values => _$actionTargetContextEnumValues;
  static ActionTargetContextEnum valueOf(String name) => _$actionTargetContextEnumValueOf(name);
}

