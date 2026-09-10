//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_availability_range.g.dart';

/// RentalAvailabilityRange
///
/// Properties:
/// * [startDate] 
/// * [endDate] 
@BuiltValue()
abstract class RentalAvailabilityRange implements Built<RentalAvailabilityRange, RentalAvailabilityRangeBuilder> {
  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  @BuiltValueField(wireName: r'endDate')
  Date get endDate;

  RentalAvailabilityRange._();

  factory RentalAvailabilityRange([void updates(RentalAvailabilityRangeBuilder b)]) = _$RentalAvailabilityRange;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalAvailabilityRangeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalAvailabilityRange> get serializer => _$RentalAvailabilityRangeSerializer();
}

class _$RentalAvailabilityRangeSerializer implements PrimitiveSerializer<RentalAvailabilityRange> {
  @override
  final Iterable<Type> types = const [RentalAvailabilityRange, _$RentalAvailabilityRange];

  @override
  final String wireName = r'RentalAvailabilityRange';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalAvailabilityRange object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    yield r'endDate';
    yield serializers.serialize(
      object.endDate,
      specifiedType: const FullType(Date),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalAvailabilityRange object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalAvailabilityRangeBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
          break;
        case r'endDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.endDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalAvailabilityRange deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalAvailabilityRangeBuilder();
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


