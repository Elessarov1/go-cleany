//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_configuration.g.dart';

/// RentalConfiguration
///
/// Properties:
/// * [minStayDays] 
/// * [longTermMinDays] 
/// * [longTermDiscountRate] 
/// * [maxStayDays] 
/// * [bookingStartMonthsAhead] 
/// * [maxActiveBookingsPerCustomer] 
/// * [today] 
/// * [latestCheckInDate] 
@BuiltValue()
abstract class RentalConfiguration implements Built<RentalConfiguration, RentalConfigurationBuilder> {
  @BuiltValueField(wireName: r'minStayDays')
  int get minStayDays;

  @BuiltValueField(wireName: r'longTermMinDays')
  int get longTermMinDays;

  @BuiltValueField(wireName: r'longTermDiscountRate')
  num get longTermDiscountRate;

  @BuiltValueField(wireName: r'maxStayDays')
  int get maxStayDays;

  @BuiltValueField(wireName: r'bookingStartMonthsAhead')
  int get bookingStartMonthsAhead;

  @BuiltValueField(wireName: r'maxActiveBookingsPerCustomer')
  int get maxActiveBookingsPerCustomer;

  @BuiltValueField(wireName: r'today')
  Date get today;

  @BuiltValueField(wireName: r'latestCheckInDate')
  Date get latestCheckInDate;

  RentalConfiguration._();

  factory RentalConfiguration([void updates(RentalConfigurationBuilder b)]) = _$RentalConfiguration;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalConfigurationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalConfiguration> get serializer => _$RentalConfigurationSerializer();
}

class _$RentalConfigurationSerializer implements PrimitiveSerializer<RentalConfiguration> {
  @override
  final Iterable<Type> types = const [RentalConfiguration, _$RentalConfiguration];

  @override
  final String wireName = r'RentalConfiguration';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'minStayDays';
    yield serializers.serialize(
      object.minStayDays,
      specifiedType: const FullType(int),
    );
    yield r'longTermMinDays';
    yield serializers.serialize(
      object.longTermMinDays,
      specifiedType: const FullType(int),
    );
    yield r'longTermDiscountRate';
    yield serializers.serialize(
      object.longTermDiscountRate,
      specifiedType: const FullType(num),
    );
    yield r'maxStayDays';
    yield serializers.serialize(
      object.maxStayDays,
      specifiedType: const FullType(int),
    );
    yield r'bookingStartMonthsAhead';
    yield serializers.serialize(
      object.bookingStartMonthsAhead,
      specifiedType: const FullType(int),
    );
    yield r'maxActiveBookingsPerCustomer';
    yield serializers.serialize(
      object.maxActiveBookingsPerCustomer,
      specifiedType: const FullType(int),
    );
    yield r'today';
    yield serializers.serialize(
      object.today,
      specifiedType: const FullType(Date),
    );
    yield r'latestCheckInDate';
    yield serializers.serialize(
      object.latestCheckInDate,
      specifiedType: const FullType(Date),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalConfigurationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'minStayDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.minStayDays = valueDes;
          break;
        case r'longTermMinDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.longTermMinDays = valueDes;
          break;
        case r'longTermDiscountRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.longTermDiscountRate = valueDes;
          break;
        case r'maxStayDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxStayDays = valueDes;
          break;
        case r'bookingStartMonthsAhead':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.bookingStartMonthsAhead = valueDes;
          break;
        case r'maxActiveBookingsPerCustomer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxActiveBookingsPerCustomer = valueDes;
          break;
        case r'today':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.today = valueDes;
          break;
        case r'latestCheckInDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.latestCheckInDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalConfiguration deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalConfigurationBuilder();
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


