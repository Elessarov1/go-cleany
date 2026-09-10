//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'login_provider_availability.g.dart';

/// LoginProviderAvailability
///
/// Properties:
/// * [available] 
@BuiltValue()
abstract class LoginProviderAvailability implements Built<LoginProviderAvailability, LoginProviderAvailabilityBuilder> {
  @BuiltValueField(wireName: r'available')
  bool get available;

  LoginProviderAvailability._();

  factory LoginProviderAvailability([void updates(LoginProviderAvailabilityBuilder b)]) = _$LoginProviderAvailability;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(LoginProviderAvailabilityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<LoginProviderAvailability> get serializer => _$LoginProviderAvailabilitySerializer();
}

class _$LoginProviderAvailabilitySerializer implements PrimitiveSerializer<LoginProviderAvailability> {
  @override
  final Iterable<Type> types = const [LoginProviderAvailability, _$LoginProviderAvailability];

  @override
  final String wireName = r'LoginProviderAvailability';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    LoginProviderAvailability object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'available';
    yield serializers.serialize(
      object.available,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    LoginProviderAvailability object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required LoginProviderAvailabilityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'available':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.available = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  LoginProviderAvailability deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = LoginProviderAvailabilityBuilder();
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


