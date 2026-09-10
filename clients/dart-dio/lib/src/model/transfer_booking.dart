//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_booking.g.dart';

/// TransferBooking
///
/// Properties:
/// * [id] 
/// * [direction] 
/// * [airportCode] 
/// * [airportNameRu] 
/// * [airportNameEn] 
/// * [vehicleCode] 
/// * [vehicleNameRu] 
/// * [vehicleNameEn] 
/// * [pickupDate] 
/// * [pickupTime] 
/// * [address] 
/// * [passengerCount] 
/// * [luggageCount] 
/// * [flightNumber] 
/// * [scheduledArrivalTime] 
/// * [customerName] 
/// * [phone] 
/// * [comment] 
/// * [basePriceAmount] 
/// * [discountAmount] 
/// * [priceAmount] 
/// * [priceCurrency] 
/// * [appliedBenefit] 
/// * [benefitRate] 
/// * [status] 
/// * [driverId] 
/// * [driverName] 
/// * [createdAt] 
/// * [confirmedAt] 
/// * [completedAt] 
/// * [cancelledAt] 
/// * [rejectedAt] 
/// * [statusReason] 
/// * [money] 
@BuiltValue()
abstract class TransferBooking implements Built<TransferBooking, TransferBookingBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'direction')
  TransferBookingDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'airportCode')
  String get airportCode;

  @BuiltValueField(wireName: r'airportNameRu')
  String get airportNameRu;

  @BuiltValueField(wireName: r'airportNameEn')
  String get airportNameEn;

  @BuiltValueField(wireName: r'vehicleCode')
  String get vehicleCode;

  @BuiltValueField(wireName: r'vehicleNameRu')
  String get vehicleNameRu;

  @BuiltValueField(wireName: r'vehicleNameEn')
  String get vehicleNameEn;

  @BuiltValueField(wireName: r'pickupDate')
  Date get pickupDate;

  @BuiltValueField(wireName: r'pickupTime')
  String get pickupTime;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'passengerCount')
  int get passengerCount;

  @BuiltValueField(wireName: r'luggageCount')
  int get luggageCount;

  @BuiltValueField(wireName: r'flightNumber')
  String? get flightNumber;

  @BuiltValueField(wireName: r'scheduledArrivalTime')
  String? get scheduledArrivalTime;

  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  @BuiltValueField(wireName: r'basePriceAmount')
  num get basePriceAmount;

  @BuiltValueField(wireName: r'discountAmount')
  num get discountAmount;

  @BuiltValueField(wireName: r'priceAmount')
  num get priceAmount;

  @BuiltValueField(wireName: r'priceCurrency')
  String get priceCurrency;

  @BuiltValueField(wireName: r'appliedBenefit')
  TransferBookingAppliedBenefitEnum? get appliedBenefit;
  // enum appliedBenefitEnum {  RENTAL_FIRST_TRANSFER,  UNKNOWN,  ,  };

  @BuiltValueField(wireName: r'benefitRate')
  num? get benefitRate;

  @BuiltValueField(wireName: r'status')
  TransferBookingStatusEnum get status;
  // enum statusEnum {  REQUESTED,  CONFIRMED,  COMPLETED,  CANCELLED,  REJECTED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'driverId')
  int? get driverId;

  @BuiltValueField(wireName: r'driverName')
  String? get driverName;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'confirmedAt')
  DateTime? get confirmedAt;

  @BuiltValueField(wireName: r'completedAt')
  DateTime? get completedAt;

  @BuiltValueField(wireName: r'cancelledAt')
  DateTime? get cancelledAt;

  @BuiltValueField(wireName: r'rejectedAt')
  DateTime? get rejectedAt;

  @BuiltValueField(wireName: r'statusReason')
  String? get statusReason;

  @BuiltValueField(wireName: r'money')
  Money get money;

  TransferBooking._();

  factory TransferBooking([void updates(TransferBookingBuilder b)]) = _$TransferBooking;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferBookingBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferBooking> get serializer => _$TransferBookingSerializer();
}

class _$TransferBookingSerializer implements PrimitiveSerializer<TransferBooking> {
  @override
  final Iterable<Type> types = const [TransferBooking, _$TransferBooking];

  @override
  final String wireName = r'TransferBooking';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferBooking object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(TransferBookingDirectionEnum),
    );
    yield r'airportCode';
    yield serializers.serialize(
      object.airportCode,
      specifiedType: const FullType(String),
    );
    yield r'airportNameRu';
    yield serializers.serialize(
      object.airportNameRu,
      specifiedType: const FullType(String),
    );
    yield r'airportNameEn';
    yield serializers.serialize(
      object.airportNameEn,
      specifiedType: const FullType(String),
    );
    yield r'vehicleCode';
    yield serializers.serialize(
      object.vehicleCode,
      specifiedType: const FullType(String),
    );
    yield r'vehicleNameRu';
    yield serializers.serialize(
      object.vehicleNameRu,
      specifiedType: const FullType(String),
    );
    yield r'vehicleNameEn';
    yield serializers.serialize(
      object.vehicleNameEn,
      specifiedType: const FullType(String),
    );
    yield r'pickupDate';
    yield serializers.serialize(
      object.pickupDate,
      specifiedType: const FullType(Date),
    );
    yield r'pickupTime';
    yield serializers.serialize(
      object.pickupTime,
      specifiedType: const FullType(String),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'passengerCount';
    yield serializers.serialize(
      object.passengerCount,
      specifiedType: const FullType(int),
    );
    yield r'luggageCount';
    yield serializers.serialize(
      object.luggageCount,
      specifiedType: const FullType(int),
    );
    yield r'flightNumber';
    yield object.flightNumber == null ? null : serializers.serialize(
      object.flightNumber,
      specifiedType: const FullType.nullable(String),
    );
    yield r'scheduledArrivalTime';
    yield object.scheduledArrivalTime == null ? null : serializers.serialize(
      object.scheduledArrivalTime,
      specifiedType: const FullType.nullable(String),
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
    yield r'comment';
    yield object.comment == null ? null : serializers.serialize(
      object.comment,
      specifiedType: const FullType.nullable(String),
    );
    yield r'basePriceAmount';
    yield serializers.serialize(
      object.basePriceAmount,
      specifiedType: const FullType(num),
    );
    yield r'discountAmount';
    yield serializers.serialize(
      object.discountAmount,
      specifiedType: const FullType(num),
    );
    yield r'priceAmount';
    yield serializers.serialize(
      object.priceAmount,
      specifiedType: const FullType(num),
    );
    yield r'priceCurrency';
    yield serializers.serialize(
      object.priceCurrency,
      specifiedType: const FullType(String),
    );
    yield r'appliedBenefit';
    yield object.appliedBenefit == null ? null : serializers.serialize(
      object.appliedBenefit,
      specifiedType: const FullType.nullable(TransferBookingAppliedBenefitEnum),
    );
    yield r'benefitRate';
    yield object.benefitRate == null ? null : serializers.serialize(
      object.benefitRate,
      specifiedType: const FullType.nullable(num),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(TransferBookingStatusEnum),
    );
    yield r'driverId';
    yield object.driverId == null ? null : serializers.serialize(
      object.driverId,
      specifiedType: const FullType.nullable(int),
    );
    yield r'driverName';
    yield object.driverName == null ? null : serializers.serialize(
      object.driverName,
      specifiedType: const FullType.nullable(String),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'confirmedAt';
    yield object.confirmedAt == null ? null : serializers.serialize(
      object.confirmedAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'completedAt';
    yield object.completedAt == null ? null : serializers.serialize(
      object.completedAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'cancelledAt';
    yield object.cancelledAt == null ? null : serializers.serialize(
      object.cancelledAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'rejectedAt';
    yield object.rejectedAt == null ? null : serializers.serialize(
      object.rejectedAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'statusReason';
    yield object.statusReason == null ? null : serializers.serialize(
      object.statusReason,
      specifiedType: const FullType.nullable(String),
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
    TransferBooking object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferBookingBuilder result,
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
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransferBookingDirectionEnum),
          ) as TransferBookingDirectionEnum;
          result.direction = valueDes;
          break;
        case r'airportCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.airportCode = valueDes;
          break;
        case r'airportNameRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.airportNameRu = valueDes;
          break;
        case r'airportNameEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.airportNameEn = valueDes;
          break;
        case r'vehicleCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleCode = valueDes;
          break;
        case r'vehicleNameRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleNameRu = valueDes;
          break;
        case r'vehicleNameEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleNameEn = valueDes;
          break;
        case r'pickupDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.pickupDate = valueDes;
          break;
        case r'pickupTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.pickupTime = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'passengerCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.passengerCount = valueDes;
          break;
        case r'luggageCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.luggageCount = valueDes;
          break;
        case r'flightNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.flightNumber = valueDes;
          break;
        case r'scheduledArrivalTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.scheduledArrivalTime = valueDes;
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
        case r'comment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.comment = valueDes;
          break;
        case r'basePriceAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.basePriceAmount = valueDes;
          break;
        case r'discountAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountAmount = valueDes;
          break;
        case r'priceAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.priceAmount = valueDes;
          break;
        case r'priceCurrency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.priceCurrency = valueDes;
          break;
        case r'appliedBenefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(TransferBookingAppliedBenefitEnum),
          ) as TransferBookingAppliedBenefitEnum?;
          if (valueDes == null) continue;
          result.appliedBenefit = valueDes;
          break;
        case r'benefitRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.benefitRate = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransferBookingStatusEnum),
          ) as TransferBookingStatusEnum;
          result.status = valueDes;
          break;
        case r'driverId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.driverId = valueDes;
          break;
        case r'driverName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.driverName = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'confirmedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.confirmedAt = valueDes;
          break;
        case r'completedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.completedAt = valueDes;
          break;
        case r'cancelledAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.cancelledAt = valueDes;
          break;
        case r'rejectedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.rejectedAt = valueDes;
          break;
        case r'statusReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.statusReason = valueDes;
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
  TransferBooking deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferBookingBuilder();
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


class TransferBookingDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const TransferBookingDirectionEnum TO_AIRPORT = _$transferBookingDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const TransferBookingDirectionEnum FROM_AIRPORT = _$transferBookingDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferBookingDirectionEnum UNKNOWN = _$transferBookingDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferBookingDirectionEnum unknownDefaultOpenApi = _$transferBookingDirectionEnum_unknownDefaultOpenApi;

  static Serializer<TransferBookingDirectionEnum> get serializer => _$transferBookingDirectionEnumSerializer;

  const TransferBookingDirectionEnum._(String name): super(name);

  static BuiltSet<TransferBookingDirectionEnum> get values => _$transferBookingDirectionEnumValues;
  static TransferBookingDirectionEnum valueOf(String name) => _$transferBookingDirectionEnumValueOf(name);
}

class TransferBookingAppliedBenefitEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_FIRST_TRANSFER')
  static const TransferBookingAppliedBenefitEnum RENTAL_FIRST_TRANSFER = _$transferBookingAppliedBenefitEnum_RENTAL_FIRST_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferBookingAppliedBenefitEnum UNKNOWN = _$transferBookingAppliedBenefitEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferBookingAppliedBenefitEnum unknownDefaultOpenApi = _$transferBookingAppliedBenefitEnum_unknownDefaultOpenApi;

  static Serializer<TransferBookingAppliedBenefitEnum> get serializer => _$transferBookingAppliedBenefitEnumSerializer;

  const TransferBookingAppliedBenefitEnum._(String name): super(name);

  static BuiltSet<TransferBookingAppliedBenefitEnum> get values => _$transferBookingAppliedBenefitEnumValues;
  static TransferBookingAppliedBenefitEnum valueOf(String name) => _$transferBookingAppliedBenefitEnumValueOf(name);
}

class TransferBookingStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REQUESTED')
  static const TransferBookingStatusEnum REQUESTED = _$transferBookingStatusEnum_REQUESTED;
  @BuiltValueEnumConst(wireName: r'CONFIRMED')
  static const TransferBookingStatusEnum CONFIRMED = _$transferBookingStatusEnum_CONFIRMED;
  @BuiltValueEnumConst(wireName: r'COMPLETED')
  static const TransferBookingStatusEnum COMPLETED = _$transferBookingStatusEnum_COMPLETED;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const TransferBookingStatusEnum CANCELLED = _$transferBookingStatusEnum_CANCELLED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const TransferBookingStatusEnum REJECTED = _$transferBookingStatusEnum_REJECTED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferBookingStatusEnum UNKNOWN = _$transferBookingStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferBookingStatusEnum unknownDefaultOpenApi = _$transferBookingStatusEnum_unknownDefaultOpenApi;

  static Serializer<TransferBookingStatusEnum> get serializer => _$transferBookingStatusEnumSerializer;

  const TransferBookingStatusEnum._(String name): super(name);

  static BuiltSet<TransferBookingStatusEnum> get values => _$transferBookingStatusEnumValues;
  static TransferBookingStatusEnum valueOf(String name) => _$transferBookingStatusEnumValueOf(name);
}

