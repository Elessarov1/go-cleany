//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_availability_range.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_availability.g.dart';

/// RentalAvailability
///
/// Properties:
/// * [propertyId] 
/// * [fromDate] 
/// * [toDate] 
/// * [unavailableRanges] 
@BuiltValue()
abstract class RentalAvailability implements Built<RentalAvailability, RentalAvailabilityBuilder> {
  @BuiltValueField(wireName: r'propertyId')
  int get propertyId;

  @BuiltValueField(wireName: r'fromDate')
  Date get fromDate;

  @BuiltValueField(wireName: r'toDate')
  Date get toDate;

  @BuiltValueField(wireName: r'unavailableRanges')
  BuiltList<RentalAvailabilityRange> get unavailableRanges;

  RentalAvailability._();

  factory RentalAvailability([void updates(RentalAvailabilityBuilder b)]) = _$RentalAvailability;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalAvailabilityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalAvailability> get serializer => _$RentalAvailabilitySerializer();
}

class _$RentalAvailabilitySerializer implements PrimitiveSerializer<RentalAvailability> {
  @override
  final Iterable<Type> types = const [RentalAvailability, _$RentalAvailability];

  @override
  final String wireName = r'RentalAvailability';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalAvailability object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'propertyId';
    yield serializers.serialize(
      object.propertyId,
      specifiedType: const FullType(int),
    );
    yield r'fromDate';
    yield serializers.serialize(
      object.fromDate,
      specifiedType: const FullType(Date),
    );
    yield r'toDate';
    yield serializers.serialize(
      object.toDate,
      specifiedType: const FullType(Date),
    );
    yield r'unavailableRanges';
    yield serializers.serialize(
      object.unavailableRanges,
      specifiedType: const FullType(BuiltList, [FullType(RentalAvailabilityRange)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalAvailability object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalAvailabilityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'propertyId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.propertyId = valueDes;
          break;
        case r'fromDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.fromDate = valueDes;
          break;
        case r'toDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.toDate = valueDes;
          break;
        case r'unavailableRanges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalAvailabilityRange)]),
          ) as BuiltList<RentalAvailabilityRange>;
          result.unavailableRanges.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalAvailability deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalAvailabilityBuilder();
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


