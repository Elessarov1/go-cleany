//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_profile.g.dart';

/// CustomerProfile
///
/// Properties:
/// * [phone] 
@BuiltValue()
abstract class CustomerProfile implements Built<CustomerProfile, CustomerProfileBuilder> {
  @BuiltValueField(wireName: r'phone')
  String? get phone;

  CustomerProfile._();

  factory CustomerProfile([void updates(CustomerProfileBuilder b)]) = _$CustomerProfile;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerProfileBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerProfile> get serializer => _$CustomerProfileSerializer();
}

class _$CustomerProfileSerializer implements PrimitiveSerializer<CustomerProfile> {
  @override
  final Iterable<Type> types = const [CustomerProfile, _$CustomerProfile];

  @override
  final String wireName = r'CustomerProfile';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerProfile object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'phone';
    yield object.phone == null ? null : serializers.serialize(
      object.phone,
      specifiedType: const FullType.nullable(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CustomerProfile object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerProfileBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'phone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.phone = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CustomerProfile deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerProfileBuilder();
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


