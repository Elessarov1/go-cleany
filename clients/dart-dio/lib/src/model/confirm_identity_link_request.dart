//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'confirm_identity_link_request.g.dart';

/// ConfirmIdentityLinkRequest
///
/// Properties:
/// * [identityToken] 
/// * [telegramInitData] 
@BuiltValue()
abstract class ConfirmIdentityLinkRequest implements Built<ConfirmIdentityLinkRequest, ConfirmIdentityLinkRequestBuilder> {
  @BuiltValueField(wireName: r'identityToken')
  String? get identityToken;

  @BuiltValueField(wireName: r'telegramInitData')
  String? get telegramInitData;

  ConfirmIdentityLinkRequest._();

  factory ConfirmIdentityLinkRequest([void updates(ConfirmIdentityLinkRequestBuilder b)]) = _$ConfirmIdentityLinkRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ConfirmIdentityLinkRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ConfirmIdentityLinkRequest> get serializer => _$ConfirmIdentityLinkRequestSerializer();
}

class _$ConfirmIdentityLinkRequestSerializer implements PrimitiveSerializer<ConfirmIdentityLinkRequest> {
  @override
  final Iterable<Type> types = const [ConfirmIdentityLinkRequest, _$ConfirmIdentityLinkRequest];

  @override
  final String wireName = r'ConfirmIdentityLinkRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ConfirmIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    ConfirmIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ConfirmIdentityLinkRequestBuilder result,
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
  ConfirmIdentityLinkRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ConfirmIdentityLinkRequestBuilder();
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


