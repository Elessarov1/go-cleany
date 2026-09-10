//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'platform_service_state.g.dart';

/// PlatformServiceState
///
/// Properties:
/// * [service] 
/// * [status] 
/// * [displayOrder] 
/// * [updatedAt] 
/// * [updatedByCustomerId] 
/// * [version] 
@BuiltValue()
abstract class PlatformServiceState implements Built<PlatformServiceState, PlatformServiceStateBuilder> {
  @BuiltValueField(wireName: r'service')
  PlatformServiceStateServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'status')
  PlatformServiceStateStatusEnum get status;
  // enum statusEnum {  ENABLED,  IN_TEST,  DISABLED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'displayOrder')
  int get displayOrder;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'updatedByCustomerId')
  int? get updatedByCustomerId;

  @BuiltValueField(wireName: r'version')
  int get version;

  PlatformServiceState._();

  factory PlatformServiceState([void updates(PlatformServiceStateBuilder b)]) = _$PlatformServiceState;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PlatformServiceStateBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PlatformServiceState> get serializer => _$PlatformServiceStateSerializer();
}

class _$PlatformServiceStateSerializer implements PrimitiveSerializer<PlatformServiceState> {
  @override
  final Iterable<Type> types = const [PlatformServiceState, _$PlatformServiceState];

  @override
  final String wireName = r'PlatformServiceState';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PlatformServiceState object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(PlatformServiceStateServiceEnum),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PlatformServiceStateStatusEnum),
    );
    yield r'displayOrder';
    yield serializers.serialize(
      object.displayOrder,
      specifiedType: const FullType(int),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.updatedByCustomerId != null) {
      yield r'updatedByCustomerId';
      yield serializers.serialize(
        object.updatedByCustomerId,
        specifiedType: const FullType.nullable(int),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PlatformServiceState object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PlatformServiceStateBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PlatformServiceStateServiceEnum),
          ) as PlatformServiceStateServiceEnum;
          result.service = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PlatformServiceStateStatusEnum),
          ) as PlatformServiceStateStatusEnum;
          result.status = valueDes;
          break;
        case r'displayOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.displayOrder = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'updatedByCustomerId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.updatedByCustomerId = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PlatformServiceState deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PlatformServiceStateBuilder();
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


class PlatformServiceStateServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const PlatformServiceStateServiceEnum CLEANING = _$platformServiceStateServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const PlatformServiceStateServiceEnum RENTAL = _$platformServiceStateServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const PlatformServiceStateServiceEnum TRANSFER = _$platformServiceStateServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const PlatformServiceStateServiceEnum UNKNOWN = _$platformServiceStateServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const PlatformServiceStateServiceEnum unknownDefaultOpenApi = _$platformServiceStateServiceEnum_unknownDefaultOpenApi;

  static Serializer<PlatformServiceStateServiceEnum> get serializer => _$platformServiceStateServiceEnumSerializer;

  const PlatformServiceStateServiceEnum._(String name): super(name);

  static BuiltSet<PlatformServiceStateServiceEnum> get values => _$platformServiceStateServiceEnumValues;
  static PlatformServiceStateServiceEnum valueOf(String name) => _$platformServiceStateServiceEnumValueOf(name);
}

class PlatformServiceStateStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ENABLED')
  static const PlatformServiceStateStatusEnum ENABLED = _$platformServiceStateStatusEnum_ENABLED;
  @BuiltValueEnumConst(wireName: r'IN_TEST')
  static const PlatformServiceStateStatusEnum IN_TEST = _$platformServiceStateStatusEnum_IN_TEST;
  @BuiltValueEnumConst(wireName: r'DISABLED')
  static const PlatformServiceStateStatusEnum DISABLED = _$platformServiceStateStatusEnum_DISABLED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const PlatformServiceStateStatusEnum UNKNOWN = _$platformServiceStateStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const PlatformServiceStateStatusEnum unknownDefaultOpenApi = _$platformServiceStateStatusEnum_unknownDefaultOpenApi;

  static Serializer<PlatformServiceStateStatusEnum> get serializer => _$platformServiceStateStatusEnumSerializer;

  const PlatformServiceStateStatusEnum._(String name): super(name);

  static BuiltSet<PlatformServiceStateStatusEnum> get values => _$platformServiceStateStatusEnumValues;
  static PlatformServiceStateStatusEnum valueOf(String name) => _$platformServiceStateStatusEnumValueOf(name);
}

