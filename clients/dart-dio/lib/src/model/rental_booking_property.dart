//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_booking_property.g.dart';

/// RentalBookingProperty
///
/// Properties:
/// * [id] 
/// * [slug] 
/// * [titleRu] 
/// * [titleEn] 
/// * [area] 
@BuiltValue()
abstract class RentalBookingProperty implements Built<RentalBookingProperty, RentalBookingPropertyBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'slug')
  String get slug;

  @BuiltValueField(wireName: r'titleRu')
  String? get titleRu;

  @BuiltValueField(wireName: r'titleEn')
  String get titleEn;

  @BuiltValueField(wireName: r'area')
  String get area;

  RentalBookingProperty._();

  factory RentalBookingProperty([void updates(RentalBookingPropertyBuilder b)]) = _$RentalBookingProperty;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalBookingPropertyBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalBookingProperty> get serializer => _$RentalBookingPropertySerializer();
}

class _$RentalBookingPropertySerializer implements PrimitiveSerializer<RentalBookingProperty> {
  @override
  final Iterable<Type> types = const [RentalBookingProperty, _$RentalBookingProperty];

  @override
  final String wireName = r'RentalBookingProperty';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalBookingProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'slug';
    yield serializers.serialize(
      object.slug,
      specifiedType: const FullType(String),
    );
    yield r'titleRu';
    yield object.titleRu == null ? null : serializers.serialize(
      object.titleRu,
      specifiedType: const FullType.nullable(String),
    );
    yield r'titleEn';
    yield serializers.serialize(
      object.titleEn,
      specifiedType: const FullType(String),
    );
    yield r'area';
    yield serializers.serialize(
      object.area,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalBookingProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalBookingPropertyBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.id = valueDes;
          break;
        case r'slug':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.slug = valueDes;
          break;
        case r'titleRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.titleRu = valueDes;
          break;
        case r'titleEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.titleEn = valueDes;
          break;
        case r'area':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.area = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalBookingProperty deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalBookingPropertyBuilder();
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


