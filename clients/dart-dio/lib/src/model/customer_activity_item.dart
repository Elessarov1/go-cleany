//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/action_target.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_activity_item.g.dart';

/// CustomerActivityItem
///
/// Properties:
/// * [service] 
/// * [entityId] 
/// * [status] 
/// * [titleRu] 
/// * [titleEn] 
/// * [subtitleRu] 
/// * [subtitleEn] 
/// * [scheduledDate] 
/// * [scheduledEndDate] 
/// * [scheduledTime] 
/// * [occurredAt] 
/// * [money] 
/// * [action] 
@BuiltValue()
abstract class CustomerActivityItem implements Built<CustomerActivityItem, CustomerActivityItemBuilder> {
  @BuiltValueField(wireName: r'service')
  CustomerActivityItemServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'entityId')
  int get entityId;

  @BuiltValueField(wireName: r'status')
  String get status;

  @BuiltValueField(wireName: r'titleRu')
  String get titleRu;

  @BuiltValueField(wireName: r'titleEn')
  String get titleEn;

  @BuiltValueField(wireName: r'subtitleRu')
  String get subtitleRu;

  @BuiltValueField(wireName: r'subtitleEn')
  String get subtitleEn;

  @BuiltValueField(wireName: r'scheduledDate')
  Date get scheduledDate;

  @BuiltValueField(wireName: r'scheduledEndDate')
  Date? get scheduledEndDate;

  @BuiltValueField(wireName: r'scheduledTime')
  String? get scheduledTime;

  @BuiltValueField(wireName: r'occurredAt')
  DateTime get occurredAt;

  @BuiltValueField(wireName: r'money')
  Money get money;

  @BuiltValueField(wireName: r'action')
  ActionTarget get action;

  CustomerActivityItem._();

  factory CustomerActivityItem([void updates(CustomerActivityItemBuilder b)]) = _$CustomerActivityItem;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerActivityItemBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerActivityItem> get serializer => _$CustomerActivityItemSerializer();
}

class _$CustomerActivityItemSerializer implements PrimitiveSerializer<CustomerActivityItem> {
  @override
  final Iterable<Type> types = const [CustomerActivityItem, _$CustomerActivityItem];

  @override
  final String wireName = r'CustomerActivityItem';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerActivityItem object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(CustomerActivityItemServiceEnum),
    );
    yield r'entityId';
    yield serializers.serialize(
      object.entityId,
      specifiedType: const FullType(int),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(String),
    );
    yield r'titleRu';
    yield serializers.serialize(
      object.titleRu,
      specifiedType: const FullType(String),
    );
    yield r'titleEn';
    yield serializers.serialize(
      object.titleEn,
      specifiedType: const FullType(String),
    );
    yield r'subtitleRu';
    yield serializers.serialize(
      object.subtitleRu,
      specifiedType: const FullType(String),
    );
    yield r'subtitleEn';
    yield serializers.serialize(
      object.subtitleEn,
      specifiedType: const FullType(String),
    );
    yield r'scheduledDate';
    yield serializers.serialize(
      object.scheduledDate,
      specifiedType: const FullType(Date),
    );
    if (object.scheduledEndDate != null) {
      yield r'scheduledEndDate';
      yield serializers.serialize(
        object.scheduledEndDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.scheduledTime != null) {
      yield r'scheduledTime';
      yield serializers.serialize(
        object.scheduledTime,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'occurredAt';
    yield serializers.serialize(
      object.occurredAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'money';
    yield serializers.serialize(
      object.money,
      specifiedType: const FullType(Money),
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
    CustomerActivityItem object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerActivityItemBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerActivityItemServiceEnum),
          ) as CustomerActivityItemServiceEnum;
          result.service = valueDes;
          break;
        case r'entityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.entityId = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.status = valueDes;
          break;
        case r'titleRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.titleRu = valueDes;
          break;
        case r'titleEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.titleEn = valueDes;
          break;
        case r'subtitleRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.subtitleRu = valueDes;
          break;
        case r'subtitleEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.subtitleEn = valueDes;
          break;
        case r'scheduledDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.scheduledDate = valueDes;
          break;
        case r'scheduledEndDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.scheduledEndDate = valueDes;
          break;
        case r'scheduledTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.scheduledTime = valueDes;
          break;
        case r'occurredAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.occurredAt = valueDes;
          break;
        case r'money':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.money.replace(valueDes);
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
  CustomerActivityItem deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerActivityItemBuilder();
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


class CustomerActivityItemServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CustomerActivityItemServiceEnum CLEANING = _$customerActivityItemServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CustomerActivityItemServiceEnum RENTAL = _$customerActivityItemServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CustomerActivityItemServiceEnum TRANSFER = _$customerActivityItemServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CustomerActivityItemServiceEnum UNKNOWN = _$customerActivityItemServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CustomerActivityItemServiceEnum unknownDefaultOpenApi = _$customerActivityItemServiceEnum_unknownDefaultOpenApi;

  static Serializer<CustomerActivityItemServiceEnum> get serializer => _$customerActivityItemServiceEnumSerializer;

  const CustomerActivityItemServiceEnum._(String name): super(name);

  static BuiltSet<CustomerActivityItemServiceEnum> get values => _$customerActivityItemServiceEnumValues;
  static CustomerActivityItemServiceEnum valueOf(String name) => _$customerActivityItemServiceEnumValueOf(name);
}

