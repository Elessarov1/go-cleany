//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_transfer_benefit.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_transfer_context_option.g.dart';

/// RentalTransferContextOption
///
/// Properties:
/// * [context] 
/// * [availability] 
/// * [direction] 
/// * [suggestedDate] 
/// * [address] 
/// * [availableFromDate] 
/// * [benefit] 
@BuiltValue()
abstract class RentalTransferContextOption implements Built<RentalTransferContextOption, RentalTransferContextOptionBuilder> {
  @BuiltValueField(wireName: r'context')
  RentalTransferContextOptionContextEnum get context;
  // enum contextEnum {  ARRIVAL,  CHECKOUT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'availability')
  RentalTransferContextOptionAvailabilityEnum get availability;
  // enum availabilityEnum {  BOOKABLE,  AVAILABLE_LATER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'direction')
  RentalTransferContextOptionDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'suggestedDate')
  Date get suggestedDate;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'availableFromDate')
  Date? get availableFromDate;

  @BuiltValueField(wireName: r'benefit')
  RentalTransferBenefit? get benefit;

  RentalTransferContextOption._();

  factory RentalTransferContextOption([void updates(RentalTransferContextOptionBuilder b)]) = _$RentalTransferContextOption;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalTransferContextOptionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalTransferContextOption> get serializer => _$RentalTransferContextOptionSerializer();
}

class _$RentalTransferContextOptionSerializer implements PrimitiveSerializer<RentalTransferContextOption> {
  @override
  final Iterable<Type> types = const [RentalTransferContextOption, _$RentalTransferContextOption];

  @override
  final String wireName = r'RentalTransferContextOption';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalTransferContextOption object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'context';
    yield serializers.serialize(
      object.context,
      specifiedType: const FullType(RentalTransferContextOptionContextEnum),
    );
    yield r'availability';
    yield serializers.serialize(
      object.availability,
      specifiedType: const FullType(RentalTransferContextOptionAvailabilityEnum),
    );
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(RentalTransferContextOptionDirectionEnum),
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
    yield r'availableFromDate';
    yield object.availableFromDate == null ? null : serializers.serialize(
      object.availableFromDate,
      specifiedType: const FullType.nullable(Date),
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
    RentalTransferContextOption object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalTransferContextOptionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'context':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferContextOptionContextEnum),
          ) as RentalTransferContextOptionContextEnum;
          result.context = valueDes;
          break;
        case r'availability':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferContextOptionAvailabilityEnum),
          ) as RentalTransferContextOptionAvailabilityEnum;
          result.availability = valueDes;
          break;
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferContextOptionDirectionEnum),
          ) as RentalTransferContextOptionDirectionEnum;
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
        case r'availableFromDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.availableFromDate = valueDes;
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
  RentalTransferContextOption deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalTransferContextOptionBuilder();
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


class RentalTransferContextOptionContextEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ARRIVAL')
  static const RentalTransferContextOptionContextEnum ARRIVAL = _$rentalTransferContextOptionContextEnum_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'CHECKOUT')
  static const RentalTransferContextOptionContextEnum CHECKOUT = _$rentalTransferContextOptionContextEnum_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferContextOptionContextEnum UNKNOWN = _$rentalTransferContextOptionContextEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferContextOptionContextEnum unknownDefaultOpenApi = _$rentalTransferContextOptionContextEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferContextOptionContextEnum> get serializer => _$rentalTransferContextOptionContextEnumSerializer;

  const RentalTransferContextOptionContextEnum._(String name): super(name);

  static BuiltSet<RentalTransferContextOptionContextEnum> get values => _$rentalTransferContextOptionContextEnumValues;
  static RentalTransferContextOptionContextEnum valueOf(String name) => _$rentalTransferContextOptionContextEnumValueOf(name);
}

class RentalTransferContextOptionAvailabilityEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BOOKABLE')
  static const RentalTransferContextOptionAvailabilityEnum BOOKABLE = _$rentalTransferContextOptionAvailabilityEnum_BOOKABLE;
  @BuiltValueEnumConst(wireName: r'AVAILABLE_LATER')
  static const RentalTransferContextOptionAvailabilityEnum AVAILABLE_LATER = _$rentalTransferContextOptionAvailabilityEnum_AVAILABLE_LATER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferContextOptionAvailabilityEnum UNKNOWN = _$rentalTransferContextOptionAvailabilityEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferContextOptionAvailabilityEnum unknownDefaultOpenApi = _$rentalTransferContextOptionAvailabilityEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferContextOptionAvailabilityEnum> get serializer => _$rentalTransferContextOptionAvailabilityEnumSerializer;

  const RentalTransferContextOptionAvailabilityEnum._(String name): super(name);

  static BuiltSet<RentalTransferContextOptionAvailabilityEnum> get values => _$rentalTransferContextOptionAvailabilityEnumValues;
  static RentalTransferContextOptionAvailabilityEnum valueOf(String name) => _$rentalTransferContextOptionAvailabilityEnumValueOf(name);
}

class RentalTransferContextOptionDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const RentalTransferContextOptionDirectionEnum TO_AIRPORT = _$rentalTransferContextOptionDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const RentalTransferContextOptionDirectionEnum FROM_AIRPORT = _$rentalTransferContextOptionDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferContextOptionDirectionEnum UNKNOWN = _$rentalTransferContextOptionDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferContextOptionDirectionEnum unknownDefaultOpenApi = _$rentalTransferContextOptionDirectionEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferContextOptionDirectionEnum> get serializer => _$rentalTransferContextOptionDirectionEnumSerializer;

  const RentalTransferContextOptionDirectionEnum._(String name): super(name);

  static BuiltSet<RentalTransferContextOptionDirectionEnum> get values => _$rentalTransferContextOptionDirectionEnumValues;
  static RentalTransferContextOptionDirectionEnum valueOf(String name) => _$rentalTransferContextOptionDirectionEnumValueOf(name);
}

