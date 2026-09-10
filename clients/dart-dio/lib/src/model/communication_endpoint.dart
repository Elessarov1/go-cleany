//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'communication_endpoint.g.dart';

/// CommunicationEndpoint
///
/// Properties:
/// * [id] 
/// * [type] 
/// * [platform] 
/// * [installationId] 
/// * [active] 
@BuiltValue()
abstract class CommunicationEndpoint implements Built<CommunicationEndpoint, CommunicationEndpointBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'type')
  CommunicationEndpointTypeEnum get type;
  // enum typeEnum {  FCM,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'platform')
  CommunicationEndpointPlatformEnum get platform;
  // enum platformEnum {  IOS,  ANDROID,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'installationId')
  String? get installationId;

  @BuiltValueField(wireName: r'active')
  bool get active;

  CommunicationEndpoint._();

  factory CommunicationEndpoint([void updates(CommunicationEndpointBuilder b)]) = _$CommunicationEndpoint;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CommunicationEndpointBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CommunicationEndpoint> get serializer => _$CommunicationEndpointSerializer();
}

class _$CommunicationEndpointSerializer implements PrimitiveSerializer<CommunicationEndpoint> {
  @override
  final Iterable<Type> types = const [CommunicationEndpoint, _$CommunicationEndpoint];

  @override
  final String wireName = r'CommunicationEndpoint';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CommunicationEndpoint object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(CommunicationEndpointTypeEnum),
    );
    yield r'platform';
    yield serializers.serialize(
      object.platform,
      specifiedType: const FullType(CommunicationEndpointPlatformEnum),
    );
    if (object.installationId != null) {
      yield r'installationId';
      yield serializers.serialize(
        object.installationId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'active';
    yield serializers.serialize(
      object.active,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CommunicationEndpoint object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CommunicationEndpointBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CommunicationEndpointTypeEnum),
          ) as CommunicationEndpointTypeEnum;
          result.type = valueDes;
          break;
        case r'platform':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CommunicationEndpointPlatformEnum),
          ) as CommunicationEndpointPlatformEnum;
          result.platform = valueDes;
          break;
        case r'installationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.installationId = valueDes;
          break;
        case r'active':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.active = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CommunicationEndpoint deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CommunicationEndpointBuilder();
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


class CommunicationEndpointTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FCM')
  static const CommunicationEndpointTypeEnum FCM = _$communicationEndpointTypeEnum_FCM;
  @BuiltValueEnumConst(wireName: r'TELEGRAM')
  static const CommunicationEndpointTypeEnum TELEGRAM = _$communicationEndpointTypeEnum_TELEGRAM;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CommunicationEndpointTypeEnum UNKNOWN = _$communicationEndpointTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CommunicationEndpointTypeEnum unknownDefaultOpenApi = _$communicationEndpointTypeEnum_unknownDefaultOpenApi;

  static Serializer<CommunicationEndpointTypeEnum> get serializer => _$communicationEndpointTypeEnumSerializer;

  const CommunicationEndpointTypeEnum._(String name): super(name);

  static BuiltSet<CommunicationEndpointTypeEnum> get values => _$communicationEndpointTypeEnumValues;
  static CommunicationEndpointTypeEnum valueOf(String name) => _$communicationEndpointTypeEnumValueOf(name);
}

class CommunicationEndpointPlatformEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IOS')
  static const CommunicationEndpointPlatformEnum IOS = _$communicationEndpointPlatformEnum_IOS;
  @BuiltValueEnumConst(wireName: r'ANDROID')
  static const CommunicationEndpointPlatformEnum ANDROID = _$communicationEndpointPlatformEnum_ANDROID;
  @BuiltValueEnumConst(wireName: r'TELEGRAM')
  static const CommunicationEndpointPlatformEnum TELEGRAM = _$communicationEndpointPlatformEnum_TELEGRAM;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CommunicationEndpointPlatformEnum UNKNOWN = _$communicationEndpointPlatformEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CommunicationEndpointPlatformEnum unknownDefaultOpenApi = _$communicationEndpointPlatformEnum_unknownDefaultOpenApi;

  static Serializer<CommunicationEndpointPlatformEnum> get serializer => _$communicationEndpointPlatformEnumSerializer;

  const CommunicationEndpointPlatformEnum._(String name): super(name);

  static BuiltSet<CommunicationEndpointPlatformEnum> get values => _$communicationEndpointPlatformEnumValues;
  static CommunicationEndpointPlatformEnum valueOf(String name) => _$communicationEndpointPlatformEnumValueOf(name);
}

