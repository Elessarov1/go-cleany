//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_cleaning_context.g.dart';

/// RentalCleaningContext
///
/// Properties:
/// * [rentalBookingId] 
/// * [address] 
/// * [phone] 
/// * [checkOutDate] 
/// * [earliestBenefitCleaningDate] 
/// * [benefitStatus] 
/// * [promoCode] 
/// * [cleaningFlowAvailable] 
@BuiltValue()
abstract class RentalCleaningContext implements Built<RentalCleaningContext, RentalCleaningContextBuilder> {
  @BuiltValueField(wireName: r'rentalBookingId')
  int get rentalBookingId;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'checkOutDate')
  Date get checkOutDate;

  @BuiltValueField(wireName: r'earliestBenefitCleaningDate')
  Date get earliestBenefitCleaningDate;

  @BuiltValueField(wireName: r'benefitStatus')
  RentalCleaningContextBenefitStatusEnum? get benefitStatus;
  // enum benefitStatusEnum {  AVAILABLE,  RESERVED,  REDEEMED,  REVOKED,  UNKNOWN,  ,  };

  @BuiltValueField(wireName: r'promoCode')
  String? get promoCode;

  @BuiltValueField(wireName: r'cleaningFlowAvailable')
  bool get cleaningFlowAvailable;

  RentalCleaningContext._();

  factory RentalCleaningContext([void updates(RentalCleaningContextBuilder b)]) = _$RentalCleaningContext;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalCleaningContextBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalCleaningContext> get serializer => _$RentalCleaningContextSerializer();
}

class _$RentalCleaningContextSerializer implements PrimitiveSerializer<RentalCleaningContext> {
  @override
  final Iterable<Type> types = const [RentalCleaningContext, _$RentalCleaningContext];

  @override
  final String wireName = r'RentalCleaningContext';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalCleaningContext object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'rentalBookingId';
    yield serializers.serialize(
      object.rentalBookingId,
      specifiedType: const FullType(int),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'phone';
    yield serializers.serialize(
      object.phone,
      specifiedType: const FullType(String),
    );
    yield r'checkOutDate';
    yield serializers.serialize(
      object.checkOutDate,
      specifiedType: const FullType(Date),
    );
    yield r'earliestBenefitCleaningDate';
    yield serializers.serialize(
      object.earliestBenefitCleaningDate,
      specifiedType: const FullType(Date),
    );
    yield r'benefitStatus';
    yield object.benefitStatus == null ? null : serializers.serialize(
      object.benefitStatus,
      specifiedType: const FullType.nullable(RentalCleaningContextBenefitStatusEnum),
    );
    yield r'promoCode';
    yield object.promoCode == null ? null : serializers.serialize(
      object.promoCode,
      specifiedType: const FullType.nullable(String),
    );
    yield r'cleaningFlowAvailable';
    yield serializers.serialize(
      object.cleaningFlowAvailable,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalCleaningContext object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalCleaningContextBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'rentalBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rentalBookingId = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'phone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phone = valueDes;
          break;
        case r'checkOutDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.checkOutDate = valueDes;
          break;
        case r'earliestBenefitCleaningDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.earliestBenefitCleaningDate = valueDes;
          break;
        case r'benefitStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalCleaningContextBenefitStatusEnum),
          ) as RentalCleaningContextBenefitStatusEnum?;
          if (valueDes == null) continue;
          result.benefitStatus = valueDes;
          break;
        case r'promoCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.promoCode = valueDes;
          break;
        case r'cleaningFlowAvailable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.cleaningFlowAvailable = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalCleaningContext deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalCleaningContextBuilder();
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


class RentalCleaningContextBenefitStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'AVAILABLE')
  static const RentalCleaningContextBenefitStatusEnum AVAILABLE = _$rentalCleaningContextBenefitStatusEnum_AVAILABLE;
  @BuiltValueEnumConst(wireName: r'RESERVED')
  static const RentalCleaningContextBenefitStatusEnum RESERVED = _$rentalCleaningContextBenefitStatusEnum_RESERVED;
  @BuiltValueEnumConst(wireName: r'REDEEMED')
  static const RentalCleaningContextBenefitStatusEnum REDEEMED = _$rentalCleaningContextBenefitStatusEnum_REDEEMED;
  @BuiltValueEnumConst(wireName: r'REVOKED')
  static const RentalCleaningContextBenefitStatusEnum REVOKED = _$rentalCleaningContextBenefitStatusEnum_REVOKED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalCleaningContextBenefitStatusEnum UNKNOWN = _$rentalCleaningContextBenefitStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalCleaningContextBenefitStatusEnum unknownDefaultOpenApi = _$rentalCleaningContextBenefitStatusEnum_unknownDefaultOpenApi;

  static Serializer<RentalCleaningContextBenefitStatusEnum> get serializer => _$rentalCleaningContextBenefitStatusEnumSerializer;

  const RentalCleaningContextBenefitStatusEnum._(String name): super(name);

  static BuiltSet<RentalCleaningContextBenefitStatusEnum> get values => _$rentalCleaningContextBenefitStatusEnumValues;
  static RentalCleaningContextBenefitStatusEnum valueOf(String name) => _$rentalCleaningContextBenefitStatusEnumValueOf(name);
}

