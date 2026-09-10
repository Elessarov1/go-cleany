//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_transfer_source_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_transfer_booking_request.g.dart';

/// CreateTransferBookingRequest
///
/// Properties:
/// * [direction] 
/// * [airportId] 
/// * [vehicleTypeId] 
/// * [pickupDate] 
/// * [pickupTime] 
/// * [address] 
/// * [passengerCount] 
/// * [luggageCount] 
/// * [flightNumber] 
/// * [scheduledArrivalTime] 
/// * [phone] 
/// * [comment] 
/// * [repeatFromBookingId] 
/// * [rentalSource] 
/// * [benefit] 
@BuiltValue()
abstract class CreateTransferBookingRequest implements Built<CreateTransferBookingRequest, CreateTransferBookingRequestBuilder> {
  @BuiltValueField(wireName: r'direction')
  CreateTransferBookingRequestDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'airportId')
  int get airportId;

  @BuiltValueField(wireName: r'vehicleTypeId')
  int get vehicleTypeId;

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

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  @BuiltValueField(wireName: r'repeatFromBookingId')
  int? get repeatFromBookingId;

  @BuiltValueField(wireName: r'rentalSource')
  RentalTransferSourceRequest? get rentalSource;

  @BuiltValueField(wireName: r'benefit')
  CreateTransferBookingRequestBenefitEnum? get benefit;
  // enum benefitEnum {  RENTAL_FIRST_TRANSFER,  UNKNOWN,  ,  };

  CreateTransferBookingRequest._();

  factory CreateTransferBookingRequest([void updates(CreateTransferBookingRequestBuilder b)]) = _$CreateTransferBookingRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateTransferBookingRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateTransferBookingRequest> get serializer => _$CreateTransferBookingRequestSerializer();
}

class _$CreateTransferBookingRequestSerializer implements PrimitiveSerializer<CreateTransferBookingRequest> {
  @override
  final Iterable<Type> types = const [CreateTransferBookingRequest, _$CreateTransferBookingRequest];

  @override
  final String wireName = r'CreateTransferBookingRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateTransferBookingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(CreateTransferBookingRequestDirectionEnum),
    );
    yield r'airportId';
    yield serializers.serialize(
      object.airportId,
      specifiedType: const FullType(int),
    );
    yield r'vehicleTypeId';
    yield serializers.serialize(
      object.vehicleTypeId,
      specifiedType: const FullType(int),
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
    if (object.flightNumber != null) {
      yield r'flightNumber';
      yield serializers.serialize(
        object.flightNumber,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.scheduledArrivalTime != null) {
      yield r'scheduledArrivalTime';
      yield serializers.serialize(
        object.scheduledArrivalTime,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'phone';
    yield serializers.serialize(
      object.phone,
      specifiedType: const FullType(String),
    );
    if (object.comment != null) {
      yield r'comment';
      yield serializers.serialize(
        object.comment,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.repeatFromBookingId != null) {
      yield r'repeatFromBookingId';
      yield serializers.serialize(
        object.repeatFromBookingId,
        specifiedType: const FullType.nullable(int),
      );
    }
    if (object.rentalSource != null) {
      yield r'rentalSource';
      yield serializers.serialize(
        object.rentalSource,
        specifiedType: const FullType.nullable(RentalTransferSourceRequest),
      );
    }
    if (object.benefit != null) {
      yield r'benefit';
      yield serializers.serialize(
        object.benefit,
        specifiedType: const FullType.nullable(CreateTransferBookingRequestBenefitEnum),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateTransferBookingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateTransferBookingRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateTransferBookingRequestDirectionEnum),
          ) as CreateTransferBookingRequestDirectionEnum;
          result.direction = valueDes;
          break;
        case r'airportId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.airportId = valueDes;
          break;
        case r'vehicleTypeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.vehicleTypeId = valueDes;
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
        case r'repeatFromBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.repeatFromBookingId = valueDes;
          break;
        case r'rentalSource':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalTransferSourceRequest),
          ) as RentalTransferSourceRequest?;
          if (valueDes == null) continue;
          result.rentalSource.replace(valueDes);
          break;
        case r'benefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CreateTransferBookingRequestBenefitEnum),
          ) as CreateTransferBookingRequestBenefitEnum?;
          if (valueDes == null) continue;
          result.benefit = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateTransferBookingRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateTransferBookingRequestBuilder();
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


class CreateTransferBookingRequestDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const CreateTransferBookingRequestDirectionEnum TO_AIRPORT = _$createTransferBookingRequestDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const CreateTransferBookingRequestDirectionEnum FROM_AIRPORT = _$createTransferBookingRequestDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateTransferBookingRequestDirectionEnum UNKNOWN = _$createTransferBookingRequestDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateTransferBookingRequestDirectionEnum unknownDefaultOpenApi = _$createTransferBookingRequestDirectionEnum_unknownDefaultOpenApi;

  static Serializer<CreateTransferBookingRequestDirectionEnum> get serializer => _$createTransferBookingRequestDirectionEnumSerializer;

  const CreateTransferBookingRequestDirectionEnum._(String name): super(name);

  static BuiltSet<CreateTransferBookingRequestDirectionEnum> get values => _$createTransferBookingRequestDirectionEnumValues;
  static CreateTransferBookingRequestDirectionEnum valueOf(String name) => _$createTransferBookingRequestDirectionEnumValueOf(name);
}

class CreateTransferBookingRequestBenefitEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_FIRST_TRANSFER')
  static const CreateTransferBookingRequestBenefitEnum RENTAL_FIRST_TRANSFER = _$createTransferBookingRequestBenefitEnum_RENTAL_FIRST_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateTransferBookingRequestBenefitEnum UNKNOWN = _$createTransferBookingRequestBenefitEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateTransferBookingRequestBenefitEnum unknownDefaultOpenApi = _$createTransferBookingRequestBenefitEnum_unknownDefaultOpenApi;

  static Serializer<CreateTransferBookingRequestBenefitEnum> get serializer => _$createTransferBookingRequestBenefitEnumSerializer;

  const CreateTransferBookingRequestBenefitEnum._(String name): super(name);

  static BuiltSet<CreateTransferBookingRequestBenefitEnum> get values => _$createTransferBookingRequestBenefitEnumValues;
  static CreateTransferBookingRequestBenefitEnum valueOf(String name) => _$createTransferBookingRequestBenefitEnumValueOf(name);
}

