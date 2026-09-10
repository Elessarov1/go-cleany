//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_term_type.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:loco_place_api/src/model/rental_booking_property.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_booking.g.dart';

/// RentalBooking
///
/// Properties:
/// * [id] 
/// * [property] 
/// * [termType] 
/// * [checkInDate] 
/// * [checkOutDate] 
/// * [rentalMonths] 
/// * [durationDays] 
/// * [customerName] 
/// * [phone] 
/// * [guests] 
/// * [comment] 
/// * [baseDailyPriceSnapshot] 
/// * [baseMonthlyPriceSnapshot] 
/// * [monthlyPriceSnapshot] 
/// * [longTermDiscountRateSnapshot] 
/// * [discountAmount] 
/// * [totalPrice] 
/// * [currency] 
/// * [status] 
/// * [createdAt] 
/// * [cancelledAt] 
/// * [cancellationReason] 
/// * [completedAt] 
/// * [money] 
@BuiltValue()
abstract class RentalBooking implements Built<RentalBooking, RentalBookingBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'property')
  RentalBookingProperty get property;

  @BuiltValueField(wireName: r'termType')
  RentalTermType get termType;
  // enum termTypeEnum {  DATE_RANGE,  MONTHLY,  UNKNOWN,  };

  @BuiltValueField(wireName: r'checkInDate')
  Date get checkInDate;

  @BuiltValueField(wireName: r'checkOutDate')
  Date get checkOutDate;

  @BuiltValueField(wireName: r'rentalMonths')
  int? get rentalMonths;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'guests')
  int get guests;

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  @BuiltValueField(wireName: r'baseDailyPriceSnapshot')
  num get baseDailyPriceSnapshot;

  @BuiltValueField(wireName: r'baseMonthlyPriceSnapshot')
  num? get baseMonthlyPriceSnapshot;

  @BuiltValueField(wireName: r'monthlyPriceSnapshot')
  num? get monthlyPriceSnapshot;

  @BuiltValueField(wireName: r'longTermDiscountRateSnapshot')
  num get longTermDiscountRateSnapshot;

  @BuiltValueField(wireName: r'discountAmount')
  num get discountAmount;

  @BuiltValueField(wireName: r'totalPrice')
  num get totalPrice;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  @BuiltValueField(wireName: r'status')
  RentalBookingStatusEnum get status;
  // enum statusEnum {  CONFIRMED,  CANCELLED_BY_CUSTOMER,  CANCELLED_BY_ADMIN,  COMPLETED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'cancelledAt')
  DateTime? get cancelledAt;

  @BuiltValueField(wireName: r'cancellationReason')
  String? get cancellationReason;

  @BuiltValueField(wireName: r'completedAt')
  DateTime? get completedAt;

  @BuiltValueField(wireName: r'money')
  Money get money;

  RentalBooking._();

  factory RentalBooking([void updates(RentalBookingBuilder b)]) = _$RentalBooking;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalBookingBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalBooking> get serializer => _$RentalBookingSerializer();
}

class _$RentalBookingSerializer implements PrimitiveSerializer<RentalBooking> {
  @override
  final Iterable<Type> types = const [RentalBooking, _$RentalBooking];

  @override
  final String wireName = r'RentalBooking';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalBooking object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'property';
    yield serializers.serialize(
      object.property,
      specifiedType: const FullType(RentalBookingProperty),
    );
    yield r'termType';
    yield serializers.serialize(
      object.termType,
      specifiedType: const FullType(RentalTermType),
    );
    yield r'checkInDate';
    yield serializers.serialize(
      object.checkInDate,
      specifiedType: const FullType(Date),
    );
    yield r'checkOutDate';
    yield serializers.serialize(
      object.checkOutDate,
      specifiedType: const FullType(Date),
    );
    yield r'rentalMonths';
    yield object.rentalMonths == null ? null : serializers.serialize(
      object.rentalMonths,
      specifiedType: const FullType.nullable(int),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'customerName';
    yield serializers.serialize(
      object.customerName,
      specifiedType: const FullType(String),
    );
    yield r'phone';
    yield serializers.serialize(
      object.phone,
      specifiedType: const FullType(String),
    );
    yield r'guests';
    yield serializers.serialize(
      object.guests,
      specifiedType: const FullType(int),
    );
    yield r'comment';
    yield object.comment == null ? null : serializers.serialize(
      object.comment,
      specifiedType: const FullType.nullable(String),
    );
    yield r'baseDailyPriceSnapshot';
    yield serializers.serialize(
      object.baseDailyPriceSnapshot,
      specifiedType: const FullType(num),
    );
    yield r'baseMonthlyPriceSnapshot';
    yield object.baseMonthlyPriceSnapshot == null ? null : serializers.serialize(
      object.baseMonthlyPriceSnapshot,
      specifiedType: const FullType.nullable(num),
    );
    yield r'monthlyPriceSnapshot';
    yield object.monthlyPriceSnapshot == null ? null : serializers.serialize(
      object.monthlyPriceSnapshot,
      specifiedType: const FullType.nullable(num),
    );
    yield r'longTermDiscountRateSnapshot';
    yield serializers.serialize(
      object.longTermDiscountRateSnapshot,
      specifiedType: const FullType(num),
    );
    yield r'discountAmount';
    yield serializers.serialize(
      object.discountAmount,
      specifiedType: const FullType(num),
    );
    yield r'totalPrice';
    yield serializers.serialize(
      object.totalPrice,
      specifiedType: const FullType(num),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(RentalBookingStatusEnum),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'cancelledAt';
    yield object.cancelledAt == null ? null : serializers.serialize(
      object.cancelledAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'cancellationReason';
    yield object.cancellationReason == null ? null : serializers.serialize(
      object.cancellationReason,
      specifiedType: const FullType.nullable(String),
    );
    yield r'completedAt';
    yield object.completedAt == null ? null : serializers.serialize(
      object.completedAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'money';
    yield serializers.serialize(
      object.money,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalBooking object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalBookingBuilder result,
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
        case r'property':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalBookingProperty),
          ) as RentalBookingProperty;
          result.property.replace(valueDes);
          break;
        case r'termType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTermType),
          ) as RentalTermType;
          result.termType = valueDes;
          break;
        case r'checkInDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.checkInDate = valueDes;
          break;
        case r'checkOutDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.checkOutDate = valueDes;
          break;
        case r'rentalMonths':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.rentalMonths = valueDes;
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'customerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerName = valueDes;
          break;
        case r'phone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phone = valueDes;
          break;
        case r'guests':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.guests = valueDes;
          break;
        case r'comment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.comment = valueDes;
          break;
        case r'baseDailyPriceSnapshot':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseDailyPriceSnapshot = valueDes;
          break;
        case r'baseMonthlyPriceSnapshot':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.baseMonthlyPriceSnapshot = valueDes;
          break;
        case r'monthlyPriceSnapshot':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.monthlyPriceSnapshot = valueDes;
          break;
        case r'longTermDiscountRateSnapshot':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.longTermDiscountRateSnapshot = valueDes;
          break;
        case r'discountAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountAmount = valueDes;
          break;
        case r'totalPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.totalPrice = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalBookingStatusEnum),
          ) as RentalBookingStatusEnum;
          result.status = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'cancelledAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.cancelledAt = valueDes;
          break;
        case r'cancellationReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.cancellationReason = valueDes;
          break;
        case r'completedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.completedAt = valueDes;
          break;
        case r'money':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.money.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalBooking deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalBookingBuilder();
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


class RentalBookingStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CONFIRMED')
  static const RentalBookingStatusEnum CONFIRMED = _$rentalBookingStatusEnum_CONFIRMED;
  @BuiltValueEnumConst(wireName: r'CANCELLED_BY_CUSTOMER')
  static const RentalBookingStatusEnum CANCELLED_BY_CUSTOMER = _$rentalBookingStatusEnum_CANCELLED_BY_CUSTOMER;
  @BuiltValueEnumConst(wireName: r'CANCELLED_BY_ADMIN')
  static const RentalBookingStatusEnum CANCELLED_BY_ADMIN = _$rentalBookingStatusEnum_CANCELLED_BY_ADMIN;
  @BuiltValueEnumConst(wireName: r'COMPLETED')
  static const RentalBookingStatusEnum COMPLETED = _$rentalBookingStatusEnum_COMPLETED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalBookingStatusEnum UNKNOWN = _$rentalBookingStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalBookingStatusEnum unknownDefaultOpenApi = _$rentalBookingStatusEnum_unknownDefaultOpenApi;

  static Serializer<RentalBookingStatusEnum> get serializer => _$rentalBookingStatusEnumSerializer;

  const RentalBookingStatusEnum._(String name): super(name);

  static BuiltSet<RentalBookingStatusEnum> get values => _$rentalBookingStatusEnumValues;
  static RentalBookingStatusEnum valueOf(String name) => _$rentalBookingStatusEnumValueOf(name);
}

