//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'account_identity.g.dart';

/// AccountIdentity
///
/// Properties:
/// * [identityId] 
/// * [provider] 
/// * [issuer] 
/// * [linked] 
/// * [username] 
/// * [writeAccessAllowed] 
@BuiltValue()
abstract class AccountIdentity implements Built<AccountIdentity, AccountIdentityBuilder> {
  @BuiltValueField(wireName: r'identityId')
  int get identityId;

  @BuiltValueField(wireName: r'provider')
  IdentityProvider get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'issuer')
  String get issuer;

  @BuiltValueField(wireName: r'linked')
  bool get linked;

  @BuiltValueField(wireName: r'username')
  String? get username;

  @BuiltValueField(wireName: r'writeAccessAllowed')
  bool get writeAccessAllowed;

  AccountIdentity._();

  factory AccountIdentity([void updates(AccountIdentityBuilder b)]) = _$AccountIdentity;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AccountIdentityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AccountIdentity> get serializer => _$AccountIdentitySerializer();
}

class _$AccountIdentitySerializer implements PrimitiveSerializer<AccountIdentity> {
  @override
  final Iterable<Type> types = const [AccountIdentity, _$AccountIdentity];

  @override
  final String wireName = r'AccountIdentity';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AccountIdentity object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'identityId';
    yield serializers.serialize(
      object.identityId,
      specifiedType: const FullType(int),
    );
    yield r'provider';
    yield serializers.serialize(
      object.provider,
      specifiedType: const FullType(IdentityProvider),
    );
    yield r'issuer';
    yield serializers.serialize(
      object.issuer,
      specifiedType: const FullType(String),
    );
    yield r'linked';
    yield serializers.serialize(
      object.linked,
      specifiedType: const FullType(bool),
    );
    if (object.username != null) {
      yield r'username';
      yield serializers.serialize(
        object.username,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'writeAccessAllowed';
    yield serializers.serialize(
      object.writeAccessAllowed,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    AccountIdentity object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AccountIdentityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'identityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.identityId = valueDes;
          break;
        case r'provider':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(IdentityProvider),
          ) as IdentityProvider;
          result.provider = valueDes;
          break;
        case r'issuer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.issuer = valueDes;
          break;
        case r'linked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.linked = valueDes;
          break;
        case r'username':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.username = valueDes;
          break;
        case r'writeAccessAllowed':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.writeAccessAllowed = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AccountIdentity deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AccountIdentityBuilder();
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


