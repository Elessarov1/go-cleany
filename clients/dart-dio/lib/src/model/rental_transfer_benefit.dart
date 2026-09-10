//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_transfer_benefit.g.dart';

/// RentalTransferBenefit
///
/// Properties:
/// * [type] 
/// * [discountRate] 
@BuiltValue()
abstract class RentalTransferBenefit implements Built<RentalTransferBenefit, RentalTransferBenefitBuilder> {
  @BuiltValueField(wireName: r'type')
  RentalTransferBenefitTypeEnum get type;
  // enum typeEnum {  RENTAL_FIRST_TRANSFER,  };

  @BuiltValueField(wireName: r'discountRate')
  num get discountRate;

  RentalTransferBenefit._();

  factory RentalTransferBenefit([void updates(RentalTransferBenefitBuilder b)]) = _$RentalTransferBenefit;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalTransferBenefitBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalTransferBenefit> get serializer => _$RentalTransferBenefitSerializer();
}

class _$RentalTransferBenefitSerializer implements PrimitiveSerializer<RentalTransferBenefit> {
  @override
  final Iterable<Type> types = const [RentalTransferBenefit, _$RentalTransferBenefit];

  @override
  final String wireName = r'RentalTransferBenefit';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalTransferBenefit object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(RentalTransferBenefitTypeEnum),
    );
    yield r'discountRate';
    yield serializers.serialize(
      object.discountRate,
      specifiedType: const FullType(num),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalTransferBenefit object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalTransferBenefitBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferBenefitTypeEnum),
          ) as RentalTransferBenefitTypeEnum;
          result.type = valueDes;
          break;
        case r'discountRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountRate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalTransferBenefit deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalTransferBenefitBuilder();
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


class RentalTransferBenefitTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_FIRST_TRANSFER')
  static const RentalTransferBenefitTypeEnum RENTAL_FIRST_TRANSFER = _$rentalTransferBenefitTypeEnum_RENTAL_FIRST_TRANSFER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferBenefitTypeEnum unknownDefaultOpenApi = _$rentalTransferBenefitTypeEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferBenefitTypeEnum> get serializer => _$rentalTransferBenefitTypeEnumSerializer;

  const RentalTransferBenefitTypeEnum._(String name): super(name);

  static BuiltSet<RentalTransferBenefitTypeEnum> get values => _$rentalTransferBenefitTypeEnumValues;
  static RentalTransferBenefitTypeEnum valueOf(String name) => _$rentalTransferBenefitTypeEnumValueOf(name);
}

