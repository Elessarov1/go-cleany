//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_term_type.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_search_criteria.g.dart';

/// RentalSearchCriteria
///
/// Properties:
/// * [mode] 
/// * [termType] 
/// * [checkInDate] 
/// * [checkOutDate] 
/// * [rentalMonths] 
/// * [durationDays] 
/// * [guests] 
@BuiltValue()
abstract class RentalSearchCriteria implements Built<RentalSearchCriteria, RentalSearchCriteriaBuilder> {
  @BuiltValueField(wireName: r'mode')
  RentalSearchCriteriaModeEnum get mode;
  // enum modeEnum {  DATE_RANGE,  MONTHLY,  BROWSE_ALL,  UNKNOWN,  };

  @BuiltValueField(wireName: r'termType')
  RentalTermType? get termType;
  // enum termTypeEnum {  DATE_RANGE,  MONTHLY,  UNKNOWN,  };

  @BuiltValueField(wireName: r'checkInDate')
  Date? get checkInDate;

  @BuiltValueField(wireName: r'checkOutDate')
  Date? get checkOutDate;

  @BuiltValueField(wireName: r'rentalMonths')
  int? get rentalMonths;

  @BuiltValueField(wireName: r'durationDays')
  int? get durationDays;

  @BuiltValueField(wireName: r'guests')
  int? get guests;

  RentalSearchCriteria._();

  factory RentalSearchCriteria([void updates(RentalSearchCriteriaBuilder b)]) = _$RentalSearchCriteria;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalSearchCriteriaBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalSearchCriteria> get serializer => _$RentalSearchCriteriaSerializer();
}

class _$RentalSearchCriteriaSerializer implements PrimitiveSerializer<RentalSearchCriteria> {
  @override
  final Iterable<Type> types = const [RentalSearchCriteria, _$RentalSearchCriteria];

  @override
  final String wireName = r'RentalSearchCriteria';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalSearchCriteria object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'mode';
    yield serializers.serialize(
      object.mode,
      specifiedType: const FullType(RentalSearchCriteriaModeEnum),
    );
    yield r'termType';
    yield object.termType == null ? null : serializers.serialize(
      object.termType,
      specifiedType: const FullType.nullable(RentalTermType),
    );
    yield r'checkInDate';
    yield object.checkInDate == null ? null : serializers.serialize(
      object.checkInDate,
      specifiedType: const FullType.nullable(Date),
    );
    yield r'checkOutDate';
    yield object.checkOutDate == null ? null : serializers.serialize(
      object.checkOutDate,
      specifiedType: const FullType.nullable(Date),
    );
    yield r'rentalMonths';
    yield object.rentalMonths == null ? null : serializers.serialize(
      object.rentalMonths,
      specifiedType: const FullType.nullable(int),
    );
    yield r'durationDays';
    yield object.durationDays == null ? null : serializers.serialize(
      object.durationDays,
      specifiedType: const FullType.nullable(int),
    );
    yield r'guests';
    yield object.guests == null ? null : serializers.serialize(
      object.guests,
      specifiedType: const FullType.nullable(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalSearchCriteria object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalSearchCriteriaBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'mode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalSearchCriteriaModeEnum),
          ) as RentalSearchCriteriaModeEnum;
          result.mode = valueDes;
          break;
        case r'termType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalTermType),
          ) as RentalTermType?;
          if (valueDes == null) continue;
          result.termType = valueDes;
          break;
        case r'checkInDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.checkInDate = valueDes;
          break;
        case r'checkOutDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
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
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.durationDays = valueDes;
          break;
        case r'guests':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.guests = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalSearchCriteria deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalSearchCriteriaBuilder();
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


class RentalSearchCriteriaModeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DATE_RANGE')
  static const RentalSearchCriteriaModeEnum DATE_RANGE = _$rentalSearchCriteriaModeEnum_DATE_RANGE;
  @BuiltValueEnumConst(wireName: r'MONTHLY')
  static const RentalSearchCriteriaModeEnum MONTHLY = _$rentalSearchCriteriaModeEnum_MONTHLY;
  @BuiltValueEnumConst(wireName: r'BROWSE_ALL')
  static const RentalSearchCriteriaModeEnum BROWSE_ALL = _$rentalSearchCriteriaModeEnum_BROWSE_ALL;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalSearchCriteriaModeEnum UNKNOWN = _$rentalSearchCriteriaModeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalSearchCriteriaModeEnum unknownDefaultOpenApi = _$rentalSearchCriteriaModeEnum_unknownDefaultOpenApi;

  static Serializer<RentalSearchCriteriaModeEnum> get serializer => _$rentalSearchCriteriaModeEnumSerializer;

  const RentalSearchCriteriaModeEnum._(String name): super(name);

  static BuiltSet<RentalSearchCriteriaModeEnum> get values => _$rentalSearchCriteriaModeEnumValues;
  static RentalSearchCriteriaModeEnum valueOf(String name) => _$rentalSearchCriteriaModeEnumValueOf(name);
}

