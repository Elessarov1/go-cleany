//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/action_target.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_home_repeat_opportunity.g.dart';

/// CustomerHomeRepeatOpportunity
///
/// Properties:
/// * [service] 
/// * [sourceEntityId] 
/// * [sourceCompletedAt] 
/// * [action] 
@BuiltValue()
abstract class CustomerHomeRepeatOpportunity implements Built<CustomerHomeRepeatOpportunity, CustomerHomeRepeatOpportunityBuilder> {
  @BuiltValueField(wireName: r'service')
  CustomerHomeRepeatOpportunityServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'sourceCompletedAt')
  DateTime get sourceCompletedAt;

  @BuiltValueField(wireName: r'action')
  ActionTarget get action;

  CustomerHomeRepeatOpportunity._();

  factory CustomerHomeRepeatOpportunity([void updates(CustomerHomeRepeatOpportunityBuilder b)]) = _$CustomerHomeRepeatOpportunity;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerHomeRepeatOpportunityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerHomeRepeatOpportunity> get serializer => _$CustomerHomeRepeatOpportunitySerializer();
}

class _$CustomerHomeRepeatOpportunitySerializer implements PrimitiveSerializer<CustomerHomeRepeatOpportunity> {
  @override
  final Iterable<Type> types = const [CustomerHomeRepeatOpportunity, _$CustomerHomeRepeatOpportunity];

  @override
  final String wireName = r'CustomerHomeRepeatOpportunity';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerHomeRepeatOpportunity object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(CustomerHomeRepeatOpportunityServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'sourceCompletedAt';
    yield serializers.serialize(
      object.sourceCompletedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'action';
    yield serializers.serialize(
      object.action,
      specifiedType: const FullType(ActionTarget),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CustomerHomeRepeatOpportunity object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerHomeRepeatOpportunityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerHomeRepeatOpportunityServiceEnum),
          ) as CustomerHomeRepeatOpportunityServiceEnum;
          result.service = valueDes;
          break;
        case r'sourceEntityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceEntityId = valueDes;
          break;
        case r'sourceCompletedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.sourceCompletedAt = valueDes;
          break;
        case r'action':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ActionTarget),
          ) as ActionTarget;
          result.action.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CustomerHomeRepeatOpportunity deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerHomeRepeatOpportunityBuilder();
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


class CustomerHomeRepeatOpportunityServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CustomerHomeRepeatOpportunityServiceEnum CLEANING = _$customerHomeRepeatOpportunityServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CustomerHomeRepeatOpportunityServiceEnum RENTAL = _$customerHomeRepeatOpportunityServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CustomerHomeRepeatOpportunityServiceEnum TRANSFER = _$customerHomeRepeatOpportunityServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CustomerHomeRepeatOpportunityServiceEnum UNKNOWN = _$customerHomeRepeatOpportunityServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CustomerHomeRepeatOpportunityServiceEnum unknownDefaultOpenApi = _$customerHomeRepeatOpportunityServiceEnum_unknownDefaultOpenApi;

  static Serializer<CustomerHomeRepeatOpportunityServiceEnum> get serializer => _$customerHomeRepeatOpportunityServiceEnumSerializer;

  const CustomerHomeRepeatOpportunityServiceEnum._(String name): super(name);

  static BuiltSet<CustomerHomeRepeatOpportunityServiceEnum> get values => _$customerHomeRepeatOpportunityServiceEnumValues;
  static CustomerHomeRepeatOpportunityServiceEnum valueOf(String name) => _$customerHomeRepeatOpportunityServiceEnumValueOf(name);
}

