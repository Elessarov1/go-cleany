//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reauthentication_challenge.g.dart';

/// ReauthenticationChallenge
///
/// Properties:
/// * [id] 
/// * [provider] 
/// * [nonce] 
/// * [expiresAt] 
@BuiltValue()
abstract class ReauthenticationChallenge implements Built<ReauthenticationChallenge, ReauthenticationChallengeBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'nonce')
  String? get nonce;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  ReauthenticationChallenge._();

  factory ReauthenticationChallenge([void updates(ReauthenticationChallengeBuilder b)]) = _$ReauthenticationChallenge;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReauthenticationChallengeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReauthenticationChallenge> get serializer => _$ReauthenticationChallengeSerializer();
}

class _$ReauthenticationChallengeSerializer implements PrimitiveSerializer<ReauthenticationChallenge> {
  @override
  final Iterable<Type> types = const [ReauthenticationChallenge, _$ReauthenticationChallenge];

  @override
  final String wireName = r'ReauthenticationChallenge';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReauthenticationChallenge object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'provider';
    yield serializers.serialize(
      object.provider,
      specifiedType: const FullType(IdentityProvider),
    );
    if (object.nonce != null) {
      yield r'nonce';
      yield serializers.serialize(
        object.nonce,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReauthenticationChallenge object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReauthenticationChallengeBuilder result,
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
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
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
  ReauthenticationChallenge deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReauthenticationChallengeBuilder();
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


