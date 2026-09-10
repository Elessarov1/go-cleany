//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_native_challenge_request.g.dart';

/// CreateNativeChallengeRequest
///
/// Properties:
/// * [provider] 
/// * [clientType] 
@BuiltValue()
abstract class CreateNativeChallengeRequest implements Built<CreateNativeChallengeRequest, CreateNativeChallengeRequestBuilder> {
  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'clientType')
  CreateNativeChallengeRequestClientTypeEnum get clientType;
  // enum clientTypeEnum {  IOS,  ANDROID,  UNKNOWN,  };

  CreateNativeChallengeRequest._();

  factory CreateNativeChallengeRequest([void updates(CreateNativeChallengeRequestBuilder b)]) = _$CreateNativeChallengeRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateNativeChallengeRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateNativeChallengeRequest> get serializer => _$CreateNativeChallengeRequestSerializer();
}

class _$CreateNativeChallengeRequestSerializer implements PrimitiveSerializer<CreateNativeChallengeRequest> {
  @override
  final Iterable<Type> types = const [CreateNativeChallengeRequest, _$CreateNativeChallengeRequest];

  @override
  final String wireName = r'CreateNativeChallengeRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateNativeChallengeRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'provider';
    yield serializers.serialize(
      object.provider,
      specifiedType: const FullType(IdentityProvider),
    );
    yield r'clientType';
    yield serializers.serialize(
      object.clientType,
      specifiedType: const FullType(CreateNativeChallengeRequestClientTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateNativeChallengeRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateNativeChallengeRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'provider':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(IdentityProvider),
          ) as IdentityProvider;
          result.provider = valueDes;
          break;
        case r'clientType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateNativeChallengeRequestClientTypeEnum),
          ) as CreateNativeChallengeRequestClientTypeEnum;
          result.clientType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateNativeChallengeRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateNativeChallengeRequestBuilder();
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


class CreateNativeChallengeRequestClientTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IOS')
  static const CreateNativeChallengeRequestClientTypeEnum IOS = _$createNativeChallengeRequestClientTypeEnum_IOS;
  @BuiltValueEnumConst(wireName: r'ANDROID')
  static const CreateNativeChallengeRequestClientTypeEnum ANDROID = _$createNativeChallengeRequestClientTypeEnum_ANDROID;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateNativeChallengeRequestClientTypeEnum UNKNOWN = _$createNativeChallengeRequestClientTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateNativeChallengeRequestClientTypeEnum unknownDefaultOpenApi = _$createNativeChallengeRequestClientTypeEnum_unknownDefaultOpenApi;

  static Serializer<CreateNativeChallengeRequestClientTypeEnum> get serializer => _$createNativeChallengeRequestClientTypeEnumSerializer;

  const CreateNativeChallengeRequestClientTypeEnum._(String name): super(name);

  static BuiltSet<CreateNativeChallengeRequestClientTypeEnum> get values => _$createNativeChallengeRequestClientTypeEnumValues;
  static CreateNativeChallengeRequestClientTypeEnum valueOf(String name) => _$createNativeChallengeRequestClientTypeEnumValueOf(name);
}

