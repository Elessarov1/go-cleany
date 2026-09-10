//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/rental_price.dart';
import 'package:loco_place_api/src/model/rental_search_criteria.dart';
import 'package:loco_place_api/src/model/rental_booking_property.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_quote.g.dart';

/// RentalQuote
///
/// Properties:
/// * [property] 
/// * [criteria] 
/// * [price] 
@BuiltValue()
abstract class RentalQuote implements Built<RentalQuote, RentalQuoteBuilder> {
  @BuiltValueField(wireName: r'property')
  RentalBookingProperty get property;

  @BuiltValueField(wireName: r'criteria')
  RentalSearchCriteria get criteria;

  @BuiltValueField(wireName: r'price')
  RentalPrice get price;

  RentalQuote._();

  factory RentalQuote([void updates(RentalQuoteBuilder b)]) = _$RentalQuote;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalQuoteBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalQuote> get serializer => _$RentalQuoteSerializer();
}

class _$RentalQuoteSerializer implements PrimitiveSerializer<RentalQuote> {
  @override
  final Iterable<Type> types = const [RentalQuote, _$RentalQuote];

  @override
  final String wireName = r'RentalQuote';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'property';
    yield serializers.serialize(
      object.property,
      specifiedType: const FullType(RentalBookingProperty),
    );
    yield r'criteria';
    yield serializers.serialize(
      object.criteria,
      specifiedType: const FullType(RentalSearchCriteria),
    );
    yield r'price';
    yield serializers.serialize(
      object.price,
      specifiedType: const FullType(RentalPrice),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalQuoteBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'property':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalBookingProperty),
          ) as RentalBookingProperty;
          result.property.replace(valueDes);
          break;
        case r'criteria':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalSearchCriteria),
          ) as RentalSearchCriteria;
          result.criteria.replace(valueDes);
          break;
        case r'price':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalPrice),
          ) as RentalPrice;
          result.price.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalQuote deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalQuoteBuilder();
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


