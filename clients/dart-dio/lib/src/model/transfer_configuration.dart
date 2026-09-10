//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/transfer_airport.dart';
import 'package:loco_place_api/src/model/transfer_price.dart';
import 'package:loco_place_api/src/model/transfer_vehicle_type.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_configuration.g.dart';

/// TransferConfiguration
///
/// Properties:
/// * [earliestBookingDate] 
/// * [latestBookingDate] 
/// * [timeSlotMinutes] 
/// * [airports] 
/// * [vehicleTypes] 
/// * [prices] 
@BuiltValue()
abstract class TransferConfiguration implements Built<TransferConfiguration, TransferConfigurationBuilder> {
  @BuiltValueField(wireName: r'earliestBookingDate')
  Date get earliestBookingDate;

  @BuiltValueField(wireName: r'latestBookingDate')
  Date get latestBookingDate;

  @BuiltValueField(wireName: r'timeSlotMinutes')
  int get timeSlotMinutes;

  @BuiltValueField(wireName: r'airports')
  BuiltList<TransferAirport> get airports;

  @BuiltValueField(wireName: r'vehicleTypes')
  BuiltList<TransferVehicleType> get vehicleTypes;

  @BuiltValueField(wireName: r'prices')
  BuiltList<TransferPrice> get prices;

  TransferConfiguration._();

  factory TransferConfiguration([void updates(TransferConfigurationBuilder b)]) = _$TransferConfiguration;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferConfigurationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferConfiguration> get serializer => _$TransferConfigurationSerializer();
}

class _$TransferConfigurationSerializer implements PrimitiveSerializer<TransferConfiguration> {
  @override
  final Iterable<Type> types = const [TransferConfiguration, _$TransferConfiguration];

  @override
  final String wireName = r'TransferConfiguration';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'earliestBookingDate';
    yield serializers.serialize(
      object.earliestBookingDate,
      specifiedType: const FullType(Date),
    );
    yield r'latestBookingDate';
    yield serializers.serialize(
      object.latestBookingDate,
      specifiedType: const FullType(Date),
    );
    yield r'timeSlotMinutes';
    yield serializers.serialize(
      object.timeSlotMinutes,
      specifiedType: const FullType(int),
    );
    yield r'airports';
    yield serializers.serialize(
      object.airports,
      specifiedType: const FullType(BuiltList, [FullType(TransferAirport)]),
    );
    yield r'vehicleTypes';
    yield serializers.serialize(
      object.vehicleTypes,
      specifiedType: const FullType(BuiltList, [FullType(TransferVehicleType)]),
    );
    yield r'prices';
    yield serializers.serialize(
      object.prices,
      specifiedType: const FullType(BuiltList, [FullType(TransferPrice)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferConfigurationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'earliestBookingDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.earliestBookingDate = valueDes;
          break;
        case r'latestBookingDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.latestBookingDate = valueDes;
          break;
        case r'timeSlotMinutes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.timeSlotMinutes = valueDes;
          break;
        case r'airports':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(TransferAirport)]),
          ) as BuiltList<TransferAirport>;
          result.airports.replace(valueDes);
          break;
        case r'vehicleTypes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(TransferVehicleType)]),
          ) as BuiltList<TransferVehicleType>;
          result.vehicleTypes.replace(valueDes);
          break;
        case r'prices':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(TransferPrice)]),
          ) as BuiltList<TransferPrice>;
          result.prices.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferConfiguration deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferConfigurationBuilder();
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


