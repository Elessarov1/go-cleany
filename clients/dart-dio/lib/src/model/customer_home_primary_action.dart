//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/action_target.dart';
import 'package:loco_place_api/src/model/rental_transfer_benefit.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_home_primary_action.g.dart';

/// CustomerHomePrimaryAction
///
/// Properties:
/// * [type] 
/// * [sourceService] 
/// * [sourceEntityId] 
/// * [targetService] 
/// * [relevantDate] 
/// * [eligibleFrom] 
/// * [expiresOn] 
/// * [action] 
/// * [benefit] 
@BuiltValue()
abstract class CustomerHomePrimaryAction implements Built<CustomerHomePrimaryAction, CustomerHomePrimaryActionBuilder> {
  @BuiltValueField(wireName: r'type')
  CustomerHomePrimaryActionTypeEnum get type;
  // enum typeEnum {  RENTAL_TRANSFER_ARRIVAL,  RENTAL_TRANSFER_CHECKOUT,  RENTAL_CLEANING,  UNKNOWN,  };

  @BuiltValueField(wireName: r'sourceService')
  CustomerHomePrimaryActionSourceServiceEnum get sourceService;
  // enum sourceServiceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'targetService')
  CustomerHomePrimaryActionTargetServiceEnum get targetService;
  // enum targetServiceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'relevantDate')
  Date get relevantDate;

  @BuiltValueField(wireName: r'eligibleFrom')
  Date? get eligibleFrom;

  @BuiltValueField(wireName: r'expiresOn')
  Date? get expiresOn;

  @BuiltValueField(wireName: r'action')
  ActionTarget get action;

  @BuiltValueField(wireName: r'benefit')
  RentalTransferBenefit? get benefit;

  CustomerHomePrimaryAction._();

  factory CustomerHomePrimaryAction([void updates(CustomerHomePrimaryActionBuilder b)]) = _$CustomerHomePrimaryAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerHomePrimaryActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerHomePrimaryAction> get serializer => _$CustomerHomePrimaryActionSerializer();
}

class _$CustomerHomePrimaryActionSerializer implements PrimitiveSerializer<CustomerHomePrimaryAction> {
  @override
  final Iterable<Type> types = const [CustomerHomePrimaryAction, _$CustomerHomePrimaryAction];

  @override
  final String wireName = r'CustomerHomePrimaryAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerHomePrimaryAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(CustomerHomePrimaryActionTypeEnum),
    );
    yield r'sourceService';
    yield serializers.serialize(
      object.sourceService,
      specifiedType: const FullType(CustomerHomePrimaryActionSourceServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'targetService';
    yield serializers.serialize(
      object.targetService,
      specifiedType: const FullType(CustomerHomePrimaryActionTargetServiceEnum),
    );
    yield r'relevantDate';
    yield serializers.serialize(
      object.relevantDate,
      specifiedType: const FullType(Date),
    );
    if (object.eligibleFrom != null) {
      yield r'eligibleFrom';
      yield serializers.serialize(
        object.eligibleFrom,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.expiresOn != null) {
      yield r'expiresOn';
      yield serializers.serialize(
        object.expiresOn,
        specifiedType: const FullType.nullable(Date),
      );
    }
    yield r'action';
    yield serializers.serialize(
      object.action,
      specifiedType: const FullType(ActionTarget),
    );
    if (object.benefit != null) {
      yield r'benefit';
      yield serializers.serialize(
        object.benefit,
        specifiedType: const FullType.nullable(RentalTransferBenefit),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CustomerHomePrimaryAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerHomePrimaryActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerHomePrimaryActionTypeEnum),
          ) as CustomerHomePrimaryActionTypeEnum;
          result.type = valueDes;
          break;
        case r'sourceService':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerHomePrimaryActionSourceServiceEnum),
          ) as CustomerHomePrimaryActionSourceServiceEnum;
          result.sourceService = valueDes;
          break;
        case r'sourceEntityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceEntityId = valueDes;
          break;
        case r'targetService':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerHomePrimaryActionTargetServiceEnum),
          ) as CustomerHomePrimaryActionTargetServiceEnum;
          result.targetService = valueDes;
          break;
        case r'relevantDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.relevantDate = valueDes;
          break;
        case r'eligibleFrom':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.eligibleFrom = valueDes;
          break;
        case r'expiresOn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.expiresOn = valueDes;
          break;
        case r'action':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ActionTarget),
          ) as ActionTarget;
          result.action.replace(valueDes);
          break;
        case r'benefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalTransferBenefit),
          ) as RentalTransferBenefit?;
          if (valueDes == null) continue;
          result.benefit.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CustomerHomePrimaryAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerHomePrimaryActionBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}


class CustomerHomePrimaryActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_TRANSFER_ARRIVAL')
  static const CustomerHomePrimaryActionTypeEnum RENTAL_TRANSFER_ARRIVAL = _$customerHomePrimaryActionTypeEnum_RENTAL_TRANSFER_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'RENTAL_TRANSFER_CHECKOUT')
  static const CustomerHomePrimaryActionTypeEnum RENTAL_TRANSFER_CHECKOUT = _$customerHomePrimaryActionTypeEnum_RENTAL_TRANSFER_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'RENTAL_CLEANING')
  static const CustomerHomePrimaryActionTypeEnum RENTAL_CLEANING = _$customerHomePrimaryActionTypeEnum_RENTAL_CLEANING;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CustomerHomePrimaryActionTypeEnum UNKNOWN = _$customerHomePrimaryActionTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CustomerHomePrimaryActionTypeEnum unknownDefaultOpenApi = _$customerHomePrimaryActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<CustomerHomePrimaryActionTypeEnum> get serializer => _$customerHomePrimaryActionTypeEnumSerializer;

  const CustomerHomePrimaryActionTypeEnum._(String name): super(name);

  static BuiltSet<CustomerHomePrimaryActionTypeEnum> get values => _$customerHomePrimaryActionTypeEnumValues;
  static CustomerHomePrimaryActionTypeEnum valueOf(String name) => _$customerHomePrimaryActionTypeEnumValueOf(name);
}

class CustomerHomePrimaryActionSourceServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CustomerHomePrimaryActionSourceServiceEnum CLEANING = _$customerHomePrimaryActionSourceServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CustomerHomePrimaryActionSourceServiceEnum RENTAL = _$customerHomePrimaryActionSourceServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CustomerHomePrimaryActionSourceServiceEnum TRANSFER = _$customerHomePrimaryActionSourceServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CustomerHomePrimaryActionSourceServiceEnum UNKNOWN = _$customerHomePrimaryActionSourceServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CustomerHomePrimaryActionSourceServiceEnum unknownDefaultOpenApi = _$customerHomePrimaryActionSourceServiceEnum_unknownDefaultOpenApi;

  static Serializer<CustomerHomePrimaryActionSourceServiceEnum> get serializer => _$customerHomePrimaryActionSourceServiceEnumSerializer;

  const CustomerHomePrimaryActionSourceServiceEnum._(String name): super(name);

  static BuiltSet<CustomerHomePrimaryActionSourceServiceEnum> get values => _$customerHomePrimaryActionSourceServiceEnumValues;
  static CustomerHomePrimaryActionSourceServiceEnum valueOf(String name) => _$customerHomePrimaryActionSourceServiceEnumValueOf(name);
}

class CustomerHomePrimaryActionTargetServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CustomerHomePrimaryActionTargetServiceEnum CLEANING = _$customerHomePrimaryActionTargetServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CustomerHomePrimaryActionTargetServiceEnum RENTAL = _$customerHomePrimaryActionTargetServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CustomerHomePrimaryActionTargetServiceEnum TRANSFER = _$customerHomePrimaryActionTargetServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CustomerHomePrimaryActionTargetServiceEnum UNKNOWN = _$customerHomePrimaryActionTargetServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CustomerHomePrimaryActionTargetServiceEnum unknownDefaultOpenApi = _$customerHomePrimaryActionTargetServiceEnum_unknownDefaultOpenApi;

  static Serializer<CustomerHomePrimaryActionTargetServiceEnum> get serializer => _$customerHomePrimaryActionTargetServiceEnumSerializer;

  const CustomerHomePrimaryActionTargetServiceEnum._(String name): super(name);

  static BuiltSet<CustomerHomePrimaryActionTargetServiceEnum> get values => _$customerHomePrimaryActionTargetServiceEnumValues;
  static CustomerHomePrimaryActionTargetServiceEnum valueOf(String name) => _$customerHomePrimaryActionTargetServiceEnumValueOf(name);
}

