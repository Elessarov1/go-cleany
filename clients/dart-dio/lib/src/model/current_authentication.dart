//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/identity_provider.dart';
import 'package:loco_place_api/src/model/login_providers.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'current_authentication.g.dart';

/// CurrentAuthentication
///
/// Properties:
/// * [authenticated] 
/// * [customerId] 
/// * [displayName] 
/// * [provider] 
/// * [roles] 
/// * [loginProviders] 
@BuiltValue()
abstract class CurrentAuthentication implements Built<CurrentAuthentication, CurrentAuthenticationBuilder> {
  @BuiltValueField(wireName: r'authenticated')
  bool get authenticated;

  @BuiltValueField(wireName: r'customerId')
  int? get customerId;

  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'provider')
  IdentityProvider? get provider;
  // enum providerEnum {  GOOGLE,  APPLE,  TELEGRAM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'roles')
  BuiltSet<CurrentAuthenticationRolesEnum> get roles;
  // enum rolesEnum {  ADMIN,  UNKNOWN,  };

  @BuiltValueField(wireName: r'loginProviders')
  LoginProviders get loginProviders;

  CurrentAuthentication._();

  factory CurrentAuthentication([void updates(CurrentAuthenticationBuilder b)]) = _$CurrentAuthentication;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CurrentAuthenticationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CurrentAuthentication> get serializer => _$CurrentAuthenticationSerializer();
}

class _$CurrentAuthenticationSerializer implements PrimitiveSerializer<CurrentAuthentication> {
  @override
  final Iterable<Type> types = const [CurrentAuthentication, _$CurrentAuthentication];

  @override
  final String wireName = r'CurrentAuthentication';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CurrentAuthentication object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'authenticated';
    yield serializers.serialize(
      object.authenticated,
      specifiedType: const FullType(bool),
    );
    if (object.customerId != null) {
      yield r'customerId';
      yield serializers.serialize(
        object.customerId,
        specifiedType: const FullType.nullable(int),
      );
    }
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.provider != null) {
      yield r'provider';
      yield serializers.serialize(
        object.provider,
        specifiedType: const FullType.nullable(IdentityProvider),
      );
    }
    yield r'roles';
    yield serializers.serialize(
      object.roles,
      specifiedType: const FullType(BuiltSet, [FullType(CurrentAuthenticationRolesEnum)]),
    );
    yield r'loginProviders';
    yield serializers.serialize(
      object.loginProviders,
      specifiedType: const FullType(LoginProviders),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CurrentAuthentication object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CurrentAuthenticationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'authenticated':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.authenticated = valueDes;
          break;
        case r'customerId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.customerId = valueDes;
          break;
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.displayName = valueDes;
          break;
        case r'provider':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(IdentityProvider),
          ) as IdentityProvider?;
          if (valueDes == null) continue;
          result.provider = valueDes;
          break;
        case r'roles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltSet, [FullType(CurrentAuthenticationRolesEnum)]),
          ) as BuiltSet<CurrentAuthenticationRolesEnum>;
          result.roles.replace(valueDes);
          break;
        case r'loginProviders':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LoginProviders),
          ) as LoginProviders;
          result.loginProviders.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CurrentAuthentication deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CurrentAuthenticationBuilder();
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


class CurrentAuthenticationRolesEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ADMIN')
  static const CurrentAuthenticationRolesEnum ADMIN = _$currentAuthenticationRolesEnum_ADMIN;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CurrentAuthenticationRolesEnum UNKNOWN = _$currentAuthenticationRolesEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CurrentAuthenticationRolesEnum unknownDefaultOpenApi = _$currentAuthenticationRolesEnum_unknownDefaultOpenApi;

  static Serializer<CurrentAuthenticationRolesEnum> get serializer => _$currentAuthenticationRolesEnumSerializer;

  const CurrentAuthenticationRolesEnum._(String name): super(name);

  static BuiltSet<CurrentAuthenticationRolesEnum> get values => _$currentAuthenticationRolesEnumValues;
  static CurrentAuthenticationRolesEnum valueOf(String name) => _$currentAuthenticationRolesEnumValueOf(name);
}

