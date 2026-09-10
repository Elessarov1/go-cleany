//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/login_provider_availability.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'login_providers.g.dart';

/// LoginProviders
///
/// Properties:
/// * [google] 
@BuiltValue()
abstract class LoginProviders implements Built<LoginProviders, LoginProvidersBuilder> {
  @BuiltValueField(wireName: r'google')
  LoginProviderAvailability get google;

  LoginProviders._();

  factory LoginProviders([void updates(LoginProvidersBuilder b)]) = _$LoginProviders;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(LoginProvidersBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<LoginProviders> get serializer => _$LoginProvidersSerializer();
}

class _$LoginProvidersSerializer implements PrimitiveSerializer<LoginProviders> {
  @override
  final Iterable<Type> types = const [LoginProviders, _$LoginProviders];

  @override
  final String wireName = r'LoginProviders';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    LoginProviders object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'google';
    yield serializers.serialize(
      object.google,
      specifiedType: const FullType(LoginProviderAvailability),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    LoginProviders object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required LoginProvidersBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'google':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LoginProviderAvailability),
          ) as LoginProviderAvailability;
          result.google.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  LoginProviders deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = LoginProvidersBuilder();
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


