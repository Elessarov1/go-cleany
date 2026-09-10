//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'native_challenge.g.dart';

/// NativeChallenge
///
/// Properties:
/// * [challengeId] 
/// * [provider] 
/// * [nonce] 
/// * [expiresAt] 
@BuiltValue()
abstract class NativeChallenge implements Built<NativeChallenge, NativeChallengeBuilder> {
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'nonce')
  String get nonce;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  NativeChallenge._();

  factory NativeChallenge([void updates(NativeChallengeBuilder b)]) = _$NativeChallenge;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NativeChallengeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NativeChallenge> get serializer => _$NativeChallengeSerializer();
}

class _$NativeChallengeSerializer implements PrimitiveSerializer<NativeChallenge> {
  @override
  final Iterable<Type> types = const [NativeChallenge, _$NativeChallenge];

  @override
  final String wireName = r'NativeChallenge';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NativeChallenge object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'challengeId';
    yield serializers.serialize(
      object.challengeId,
      specifiedType: const FullType(String),
    );
    yield r'provider';
    yield serializers.serialize(
      object.provider,
      specifiedType: const FullType(IdentityProvider),
    );
    yield r'nonce';
    yield serializers.serialize(
      object.nonce,
      specifiedType: const FullType(String),
    );
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NativeChallenge object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NativeChallengeBuilder result,
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
        case r'provider':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(IdentityProvider),
          ) as IdentityProvider;
          result.provider = valueDes;
          break;
        case r'nonce':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nonce = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NativeChallenge deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NativeChallengeBuilder();
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


