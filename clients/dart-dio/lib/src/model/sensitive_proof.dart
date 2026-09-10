//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'sensitive_proof.g.dart';

/// SensitiveProof
///
/// Properties:
/// * [challengeId] 
/// * [identityToken] 
/// * [telegramInitData] 
@BuiltValue()
abstract class SensitiveProof implements Built<SensitiveProof, SensitiveProofBuilder> {
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  @BuiltValueField(wireName: r'identityToken')
  String? get identityToken;

  @BuiltValueField(wireName: r'telegramInitData')
  String? get telegramInitData;

  SensitiveProof._();

  factory SensitiveProof([void updates(SensitiveProofBuilder b)]) = _$SensitiveProof;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SensitiveProofBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SensitiveProof> get serializer => _$SensitiveProofSerializer();
}

class _$SensitiveProofSerializer implements PrimitiveSerializer<SensitiveProof> {
  @override
  final Iterable<Type> types = const [SensitiveProof, _$SensitiveProof];

  @override
  final String wireName = r'SensitiveProof';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SensitiveProof object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'challengeId';
    yield serializers.serialize(
      object.challengeId,
      specifiedType: const FullType(String),
    );
    if (object.identityToken != null) {
      yield r'identityToken';
      yield serializers.serialize(
        object.identityToken,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.telegramInitData != null) {
      yield r'telegramInitData';
      yield serializers.serialize(
        object.telegramInitData,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    SensitiveProof object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SensitiveProofBuilder result,
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
        case r'identityToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.identityToken = valueDes;
          break;
        case r'telegramInitData':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.telegramInitData = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SensitiveProof deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SensitiveProofBuilder();
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


