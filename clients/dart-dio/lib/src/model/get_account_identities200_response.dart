//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/account_identity.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'get_account_identities200_response.g.dart';

/// GetAccountIdentities200Response
///
/// Properties:
/// * [identities] 
@BuiltValue()
abstract class GetAccountIdentities200Response implements Built<GetAccountIdentities200Response, GetAccountIdentities200ResponseBuilder> {
  @BuiltValueField(wireName: r'identities')
  BuiltList<AccountIdentity> get identities;

  GetAccountIdentities200Response._();

  factory GetAccountIdentities200Response([void updates(GetAccountIdentities200ResponseBuilder b)]) = _$GetAccountIdentities200Response;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(GetAccountIdentities200ResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<GetAccountIdentities200Response> get serializer => _$GetAccountIdentities200ResponseSerializer();
}

class _$GetAccountIdentities200ResponseSerializer implements PrimitiveSerializer<GetAccountIdentities200Response> {
  @override
  final Iterable<Type> types = const [GetAccountIdentities200Response, _$GetAccountIdentities200Response];

  @override
  final String wireName = r'GetAccountIdentities200Response';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    GetAccountIdentities200Response object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'identities';
    yield serializers.serialize(
      object.identities,
      specifiedType: const FullType(BuiltList, [FullType(AccountIdentity)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    GetAccountIdentities200Response object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required GetAccountIdentities200ResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'identities':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(AccountIdentity)]),
          ) as BuiltList<AccountIdentity>;
          result.identities.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  GetAccountIdentities200Response deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = GetAccountIdentities200ResponseBuilder();
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


