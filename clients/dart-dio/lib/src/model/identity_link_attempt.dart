//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'identity_link_attempt.g.dart';

/// IdentityLinkAttempt
///
/// Properties:
/// * [id] 
/// * [provider] 
/// * [nonce] 
/// * [reauthenticationNonce] 
/// * [telegramDeepLink] 
/// * [expiresAt] 
@BuiltValue()
abstract class IdentityLinkAttempt implements Built<IdentityLinkAttempt, IdentityLinkAttemptBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'nonce')
  String? get nonce;

  @BuiltValueField(wireName: r'reauthenticationNonce')
  String? get reauthenticationNonce;

  @BuiltValueField(wireName: r'telegramDeepLink')
  String? get telegramDeepLink;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  IdentityLinkAttempt._();

  factory IdentityLinkAttempt([void updates(IdentityLinkAttemptBuilder b)]) = _$IdentityLinkAttempt;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(IdentityLinkAttemptBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<IdentityLinkAttempt> get serializer => _$IdentityLinkAttemptSerializer();
}

class _$IdentityLinkAttemptSerializer implements PrimitiveSerializer<IdentityLinkAttempt> {
  @override
  final Iterable<Type> types = const [IdentityLinkAttempt, _$IdentityLinkAttempt];

  @override
  final String wireName = r'IdentityLinkAttempt';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    IdentityLinkAttempt object, {
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
    if (object.reauthenticationNonce != null) {
      yield r'reauthenticationNonce';
      yield serializers.serialize(
        object.reauthenticationNonce,
        specifiedType: const FullType(String),
      );
    }
    if (object.telegramDeepLink != null) {
      yield r'telegramDeepLink';
      yield serializers.serialize(
        object.telegramDeepLink,
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
    IdentityLinkAttempt object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required IdentityLinkAttemptBuilder result,
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
        case r'reauthenticationNonce':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.reauthenticationNonce = valueDes;
          break;
        case r'telegramDeepLink':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.telegramDeepLink = valueDes;
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
  IdentityLinkAttempt deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = IdentityLinkAttemptBuilder();
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


