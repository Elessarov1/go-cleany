//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_transfer_benefit.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_transfer_prefill.g.dart';

/// RentalTransferPrefill
///
/// Properties:
/// * [rentalBookingId] 
/// * [context] 
/// * [direction] 
/// * [suggestedDate] 
/// * [address] 
/// * [benefit] 
@BuiltValue()
abstract class RentalTransferPrefill implements Built<RentalTransferPrefill, RentalTransferPrefillBuilder> {
  @BuiltValueField(wireName: r'rentalBookingId')
  int get rentalBookingId;

  @BuiltValueField(wireName: r'context')
  RentalTransferPrefillContextEnum get context;
  // enum contextEnum {  ARRIVAL,  CHECKOUT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'direction')
  RentalTransferPrefillDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'suggestedDate')
  Date get suggestedDate;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'benefit')
  RentalTransferBenefit? get benefit;

  RentalTransferPrefill._();

  factory RentalTransferPrefill([void updates(RentalTransferPrefillBuilder b)]) = _$RentalTransferPrefill;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalTransferPrefillBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalTransferPrefill> get serializer => _$RentalTransferPrefillSerializer();
}

class _$RentalTransferPrefillSerializer implements PrimitiveSerializer<RentalTransferPrefill> {
  @override
  final Iterable<Type> types = const [RentalTransferPrefill, _$RentalTransferPrefill];

  @override
  final String wireName = r'RentalTransferPrefill';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalTransferPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'rentalBookingId';
    yield serializers.serialize(
      object.rentalBookingId,
      specifiedType: const FullType(int),
    );
    yield r'context';
    yield serializers.serialize(
      object.context,
      specifiedType: const FullType(RentalTransferPrefillContextEnum),
    );
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(RentalTransferPrefillDirectionEnum),
    );
    yield r'suggestedDate';
    yield serializers.serialize(
      object.suggestedDate,
      specifiedType: const FullType(Date),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'benefit';
    yield object.benefit == null ? null : serializers.serialize(
      object.benefit,
      specifiedType: const FullType.nullable(RentalTransferBenefit),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalTransferPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalTransferPrefillBuilder result,
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
        case r'context':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferPrefillContextEnum),
          ) as RentalTransferPrefillContextEnum;
          result.context = valueDes;
          break;
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferPrefillDirectionEnum),
          ) as RentalTransferPrefillDirectionEnum;
          result.direction = valueDes;
          break;
        case r'suggestedDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.suggestedDate = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'benefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalTransferBenefit),
          ) as RentalTransferBenefit?;
          if (valueDes == null) continue;
          result.benefit.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalTransferPrefill deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalTransferPrefillBuilder();
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


class RentalTransferPrefillContextEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ARRIVAL')
  static const RentalTransferPrefillContextEnum ARRIVAL = _$rentalTransferPrefillContextEnum_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'CHECKOUT')
  static const RentalTransferPrefillContextEnum CHECKOUT = _$rentalTransferPrefillContextEnum_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferPrefillContextEnum UNKNOWN = _$rentalTransferPrefillContextEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferPrefillContextEnum unknownDefaultOpenApi = _$rentalTransferPrefillContextEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferPrefillContextEnum> get serializer => _$rentalTransferPrefillContextEnumSerializer;

  const RentalTransferPrefillContextEnum._(String name): super(name);

  static BuiltSet<RentalTransferPrefillContextEnum> get values => _$rentalTransferPrefillContextEnumValues;
  static RentalTransferPrefillContextEnum valueOf(String name) => _$rentalTransferPrefillContextEnumValueOf(name);
}

class RentalTransferPrefillDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const RentalTransferPrefillDirectionEnum TO_AIRPORT = _$rentalTransferPrefillDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const RentalTransferPrefillDirectionEnum FROM_AIRPORT = _$rentalTransferPrefillDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferPrefillDirectionEnum UNKNOWN = _$rentalTransferPrefillDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferPrefillDirectionEnum unknownDefaultOpenApi = _$rentalTransferPrefillDirectionEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferPrefillDirectionEnum> get serializer => _$rentalTransferPrefillDirectionEnumSerializer;

  const RentalTransferPrefillDirectionEnum._(String name): super(name);

  static BuiltSet<RentalTransferPrefillDirectionEnum> get values => _$rentalTransferPrefillDirectionEnumValues;
  static RentalTransferPrefillDirectionEnum valueOf(String name) => _$rentalTransferPrefillDirectionEnumValueOf(name);
}

