//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'register_communication_endpoint_request.g.dart';

/// RegisterCommunicationEndpointRequest
///
/// Properties:
/// * [platform] 
/// * [registrationToken] 
/// * [installationId] 
/// * [locale] 
/// * [appVersion] 
@BuiltValue()
abstract class RegisterCommunicationEndpointRequest implements Built<RegisterCommunicationEndpointRequest, RegisterCommunicationEndpointRequestBuilder> {
  @BuiltValueField(wireName: r'platform')
  RegisterCommunicationEndpointRequestPlatformEnum get platform;
  // enum platformEnum {  IOS,  ANDROID,  UNKNOWN,  };

  @BuiltValueField(wireName: r'registrationToken')
  String get registrationToken;

  @BuiltValueField(wireName: r'installationId')
  String get installationId;

  @BuiltValueField(wireName: r'locale')
  String? get locale;

  @BuiltValueField(wireName: r'appVersion')
  String? get appVersion;

  RegisterCommunicationEndpointRequest._();

  factory RegisterCommunicationEndpointRequest([void updates(RegisterCommunicationEndpointRequestBuilder b)]) = _$RegisterCommunicationEndpointRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RegisterCommunicationEndpointRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RegisterCommunicationEndpointRequest> get serializer => _$RegisterCommunicationEndpointRequestSerializer();
}

class _$RegisterCommunicationEndpointRequestSerializer implements PrimitiveSerializer<RegisterCommunicationEndpointRequest> {
  @override
  final Iterable<Type> types = const [RegisterCommunicationEndpointRequest, _$RegisterCommunicationEndpointRequest];

  @override
  final String wireName = r'RegisterCommunicationEndpointRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RegisterCommunicationEndpointRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'platform';
    yield serializers.serialize(
      object.platform,
      specifiedType: const FullType(RegisterCommunicationEndpointRequestPlatformEnum),
    );
    yield r'registrationToken';
    yield serializers.serialize(
      object.registrationToken,
      specifiedType: const FullType(String),
    );
    yield r'installationId';
    yield serializers.serialize(
      object.installationId,
      specifiedType: const FullType(String),
    );
    if (object.locale != null) {
      yield r'locale';
      yield serializers.serialize(
        object.locale,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.appVersion != null) {
      yield r'appVersion';
      yield serializers.serialize(
        object.appVersion,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    RegisterCommunicationEndpointRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RegisterCommunicationEndpointRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'platform':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RegisterCommunicationEndpointRequestPlatformEnum),
          ) as RegisterCommunicationEndpointRequestPlatformEnum;
          result.platform = valueDes;
          break;
        case r'registrationToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.registrationToken = valueDes;
          break;
        case r'installationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.installationId = valueDes;
          break;
        case r'locale':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.locale = valueDes;
          break;
        case r'appVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.appVersion = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RegisterCommunicationEndpointRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RegisterCommunicationEndpointRequestBuilder();
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


class RegisterCommunicationEndpointRequestPlatformEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IOS')
  static const RegisterCommunicationEndpointRequestPlatformEnum IOS = _$registerCommunicationEndpointRequestPlatformEnum_IOS;
  @BuiltValueEnumConst(wireName: r'ANDROID')
  static const RegisterCommunicationEndpointRequestPlatformEnum ANDROID = _$registerCommunicationEndpointRequestPlatformEnum_ANDROID;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RegisterCommunicationEndpointRequestPlatformEnum UNKNOWN = _$registerCommunicationEndpointRequestPlatformEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RegisterCommunicationEndpointRequestPlatformEnum unknownDefaultOpenApi = _$registerCommunicationEndpointRequestPlatformEnum_unknownDefaultOpenApi;

  static Serializer<RegisterCommunicationEndpointRequestPlatformEnum> get serializer => _$registerCommunicationEndpointRequestPlatformEnumSerializer;

  const RegisterCommunicationEndpointRequestPlatformEnum._(String name): super(name);

  static BuiltSet<RegisterCommunicationEndpointRequestPlatformEnum> get values => _$registerCommunicationEndpointRequestPlatformEnumValues;
  static RegisterCommunicationEndpointRequestPlatformEnum valueOf(String name) => _$registerCommunicationEndpointRequestPlatformEnumValueOf(name);
}

