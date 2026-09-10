//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_identity_link_request.g.dart';

/// CreateIdentityLinkRequest
///
/// Properties:
/// * [provider] 
@BuiltValue()
abstract class CreateIdentityLinkRequest implements Built<CreateIdentityLinkRequest, CreateIdentityLinkRequestBuilder> {
  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  CreateIdentityLinkRequest._();

  factory CreateIdentityLinkRequest([void updates(CreateIdentityLinkRequestBuilder b)]) = _$CreateIdentityLinkRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateIdentityLinkRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateIdentityLinkRequest> get serializer => _$CreateIdentityLinkRequestSerializer();
}

class _$CreateIdentityLinkRequestSerializer implements PrimitiveSerializer<CreateIdentityLinkRequest> {
  @override
  final Iterable<Type> types = const [CreateIdentityLinkRequest, _$CreateIdentityLinkRequest];

  @override
  final String wireName = r'CreateIdentityLinkRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'provider';
    yield serializers.serialize(
      object.provider,
      specifiedType: const FullType(IdentityProvider),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateIdentityLinkRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateIdentityLinkRequestBuilder result,
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateIdentityLinkRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateIdentityLinkRequestBuilder();
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


