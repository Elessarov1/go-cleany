//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'verify_identity_link_request.g.dart';

/// VerifyIdentityLinkRequest
///
/// Properties:
/// * [identityToken] 
/// * [authorizationCode] 
@BuiltValue()
abstract class VerifyIdentityLinkRequest implements Built<VerifyIdentityLinkRequest, VerifyIdentityLinkRequestBuilder> {
  @BuiltValueField(wireName: r'identityToken')
  String? get identityToken;

  @BuiltValueField(wireName: r'authorizationCode')
  String? get authorizationCode;

  VerifyIdentityLinkRequest._();

  factory VerifyIdentityLinkRequest([void updates(VerifyIdentityLinkRequestBuilder b)]) = _$VerifyIdentityLinkRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VerifyIdentityLinkRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VerifyIdentityLinkRequest> get serializer => _$VerifyIdentityLinkRequestSerializer();
}

class _$VerifyIdentityLinkRequestSerializer implements PrimitiveSerializer<VerifyIdentityLinkRequest> {
  @override
  final Iterable<Type> types = const [VerifyIdentityLinkRequest, _$VerifyIdentityLinkRequest];

  @override
  final String wireName = r'VerifyIdentityLinkRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VerifyIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.identityToken != null) {
      yield r'identityToken';
      yield serializers.serialize(
        object.identityToken,
        specifiedType: const FullType.nullable(String),
      );
    }
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
    VerifyIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VerifyIdentityLinkRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'identityToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
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
  VerifyIdentityLinkRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VerifyIdentityLinkRequestBuilder();
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


