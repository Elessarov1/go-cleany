//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'native_provider_login_request.g.dart';

/// NativeProviderLoginRequest
///
/// Properties:
/// * [challengeId] 
/// * [clientType] 
/// * [identityToken] 
/// * [authorizationCode] 
@BuiltValue()
abstract class NativeProviderLoginRequest implements Built<NativeProviderLoginRequest, NativeProviderLoginRequestBuilder> {
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  @BuiltValueField(wireName: r'clientType')
  NativeProviderLoginRequestClientTypeEnum get clientType;
  // enum clientTypeEnum {  IOS,  ANDROID,  UNKNOWN,  };

  @BuiltValueField(wireName: r'identityToken')
  String get identityToken;

  @BuiltValueField(wireName: r'authorizationCode')
  String? get authorizationCode;

  NativeProviderLoginRequest._();

  factory NativeProviderLoginRequest([void updates(NativeProviderLoginRequestBuilder b)]) = _$NativeProviderLoginRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NativeProviderLoginRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NativeProviderLoginRequest> get serializer => _$NativeProviderLoginRequestSerializer();
}

class _$NativeProviderLoginRequestSerializer implements PrimitiveSerializer<NativeProviderLoginRequest> {
  @override
  final Iterable<Type> types = const [NativeProviderLoginRequest, _$NativeProviderLoginRequest];

  @override
  final String wireName = r'NativeProviderLoginRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NativeProviderLoginRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'challengeId';
    yield serializers.serialize(
      object.challengeId,
      specifiedType: const FullType(String),
    );
    yield r'clientType';
    yield serializers.serialize(
      object.clientType,
      specifiedType: const FullType(NativeProviderLoginRequestClientTypeEnum),
    );
    yield r'identityToken';
    yield serializers.serialize(
      object.identityToken,
      specifiedType: const FullType(String),
    );
    if (object.authorizationCode != null) {
      yield r'authorizationCode';
      yield serializers.serialize(
        object.authorizationCode,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    NativeProviderLoginRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NativeProviderLoginRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'challengeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.challengeId = valueDes;
          break;
        case r'clientType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(NativeProviderLoginRequestClientTypeEnum),
          ) as NativeProviderLoginRequestClientTypeEnum;
          result.clientType = valueDes;
          break;
        case r'identityToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.identityToken = valueDes;
          break;
        case r'authorizationCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.authorizationCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NativeProviderLoginRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NativeProviderLoginRequestBuilder();
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


class NativeProviderLoginRequestClientTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IOS')
  static const NativeProviderLoginRequestClientTypeEnum IOS = _$nativeProviderLoginRequestClientTypeEnum_IOS;
  @BuiltValueEnumConst(wireName: r'ANDROID')
  static const NativeProviderLoginRequestClientTypeEnum ANDROID = _$nativeProviderLoginRequestClientTypeEnum_ANDROID;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const NativeProviderLoginRequestClientTypeEnum UNKNOWN = _$nativeProviderLoginRequestClientTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const NativeProviderLoginRequestClientTypeEnum unknownDefaultOpenApi = _$nativeProviderLoginRequestClientTypeEnum_unknownDefaultOpenApi;

  static Serializer<NativeProviderLoginRequestClientTypeEnum> get serializer => _$nativeProviderLoginRequestClientTypeEnumSerializer;

  const NativeProviderLoginRequestClientTypeEnum._(String name): super(name);

  static BuiltSet<NativeProviderLoginRequestClientTypeEnum> get values => _$nativeProviderLoginRequestClientTypeEnumValues;
  static NativeProviderLoginRequestClientTypeEnum valueOf(String name) => _$nativeProviderLoginRequestClientTypeEnumValueOf(name);
}

