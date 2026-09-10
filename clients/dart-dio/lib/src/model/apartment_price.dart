//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'apartment_price.g.dart';

/// ApartmentPrice
///
/// Properties:
/// * [type] 
/// * [regularPrice] 
/// * [deepPrice] 
@BuiltValue()
abstract class ApartmentPrice implements Built<ApartmentPrice, ApartmentPriceBuilder> {
  @BuiltValueField(wireName: r'type')
  ApartmentPriceTypeEnum get type;
  // enum typeEnum {  STUDIO,  ONE_PLUS_ONE,  TWO_PLUS_ONE,  THREE_PLUS_ONE,  FOUR_PLUS_ONE,  UNKNOWN,  };

  @BuiltValueField(wireName: r'regularPrice')
  num get regularPrice;

  @BuiltValueField(wireName: r'deepPrice')
  num get deepPrice;

  ApartmentPrice._();

  factory ApartmentPrice([void updates(ApartmentPriceBuilder b)]) = _$ApartmentPrice;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ApartmentPriceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ApartmentPrice> get serializer => _$ApartmentPriceSerializer();
}

class _$ApartmentPriceSerializer implements PrimitiveSerializer<ApartmentPrice> {
  @override
  final Iterable<Type> types = const [ApartmentPrice, _$ApartmentPrice];

  @override
  final String wireName = r'ApartmentPrice';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ApartmentPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ApartmentPriceTypeEnum),
    );
    yield r'regularPrice';
    yield serializers.serialize(
      object.regularPrice,
      specifiedType: const FullType(num),
    );
    yield r'deepPrice';
    yield serializers.serialize(
      object.deepPrice,
      specifiedType: const FullType(num),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ApartmentPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ApartmentPriceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ApartmentPriceTypeEnum),
          ) as ApartmentPriceTypeEnum;
          result.type = valueDes;
          break;
        case r'regularPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.regularPrice = valueDes;
          break;
        case r'deepPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.deepPrice = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ApartmentPrice deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ApartmentPriceBuilder();
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


class ApartmentPriceTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'STUDIO')
  static const ApartmentPriceTypeEnum STUDIO = _$apartmentPriceTypeEnum_STUDIO;
  @BuiltValueEnumConst(wireName: r'ONE_PLUS_ONE')
  static const ApartmentPriceTypeEnum ONE_PLUS_ONE = _$apartmentPriceTypeEnum_ONE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'TWO_PLUS_ONE')
  static const ApartmentPriceTypeEnum TWO_PLUS_ONE = _$apartmentPriceTypeEnum_TWO_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'THREE_PLUS_ONE')
  static const ApartmentPriceTypeEnum THREE_PLUS_ONE = _$apartmentPriceTypeEnum_THREE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'FOUR_PLUS_ONE')
  static const ApartmentPriceTypeEnum FOUR_PLUS_ONE = _$apartmentPriceTypeEnum_FOUR_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const ApartmentPriceTypeEnum UNKNOWN = _$apartmentPriceTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const ApartmentPriceTypeEnum unknownDefaultOpenApi = _$apartmentPriceTypeEnum_unknownDefaultOpenApi;

  static Serializer<ApartmentPriceTypeEnum> get serializer => _$apartmentPriceTypeEnumSerializer;

  const ApartmentPriceTypeEnum._(String name): super(name);

  static BuiltSet<ApartmentPriceTypeEnum> get values => _$apartmentPriceTypeEnumValues;
  static ApartmentPriceTypeEnum valueOf(String name) => _$apartmentPriceTypeEnumValueOf(name);
}

