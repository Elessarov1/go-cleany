//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/account_identity.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'account_identities.g.dart';

/// AccountIdentities
///
/// Properties:
/// * [identities] 
@BuiltValue()
abstract class AccountIdentities implements Built<AccountIdentities, AccountIdentitiesBuilder> {
  @BuiltValueField(wireName: r'identities')
  BuiltList<AccountIdentity> get identities;

  AccountIdentities._();

  factory AccountIdentities([void updates(AccountIdentitiesBuilder b)]) = _$AccountIdentities;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AccountIdentitiesBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AccountIdentities> get serializer => _$AccountIdentitiesSerializer();
}

class _$AccountIdentitiesSerializer implements PrimitiveSerializer<AccountIdentities> {
  @override
  final Iterable<Type> types = const [AccountIdentities, _$AccountIdentities];

  @override
  final String wireName = r'AccountIdentities';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AccountIdentities object, {
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
    AccountIdentities object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AccountIdentitiesBuilder result,
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
  AccountIdentities deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AccountIdentitiesBuilder();
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


