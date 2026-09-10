//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_repeat_prefill.g.dart';

/// CleaningRepeatPrefill
///
/// Properties:
/// * [sourceOrderId] 
/// * [area] 
/// * [address] 
/// * [apartmentType] 
/// * [duplex] 
/// * [cleaningType] 
@BuiltValue()
abstract class CleaningRepeatPrefill implements Built<CleaningRepeatPrefill, CleaningRepeatPrefillBuilder> {
  @BuiltValueField(wireName: r'sourceOrderId')
  int get sourceOrderId;

  @BuiltValueField(wireName: r'area')
  CleaningRepeatPrefillAreaEnum get area;
  // enum areaEnum {  MAHMUTLAR,  KARGICAK,  KESTEL,  UNKNOWN,  };

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'apartmentType')
  CleaningRepeatPrefillApartmentTypeEnum get apartmentType;
  // enum apartmentTypeEnum {  STUDIO,  ONE_PLUS_ONE,  TWO_PLUS_ONE,  THREE_PLUS_ONE,  FOUR_PLUS_ONE,  UNKNOWN,  };

  @BuiltValueField(wireName: r'duplex')
  bool get duplex;

  @BuiltValueField(wireName: r'cleaningType')
  CleaningRepeatPrefillCleaningTypeEnum get cleaningType;
  // enum cleaningTypeEnum {  REGULAR,  DEEP,  UNKNOWN,  };

  CleaningRepeatPrefill._();

  factory CleaningRepeatPrefill([void updates(CleaningRepeatPrefillBuilder b)]) = _$CleaningRepeatPrefill;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningRepeatPrefillBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningRepeatPrefill> get serializer => _$CleaningRepeatPrefillSerializer();
}

class _$CleaningRepeatPrefillSerializer implements PrimitiveSerializer<CleaningRepeatPrefill> {
  @override
  final Iterable<Type> types = const [CleaningRepeatPrefill, _$CleaningRepeatPrefill];

  @override
  final String wireName = r'CleaningRepeatPrefill';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningRepeatPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sourceOrderId';
    yield serializers.serialize(
      object.sourceOrderId,
      specifiedType: const FullType(int),
    );
    yield r'area';
    yield serializers.serialize(
      object.area,
      specifiedType: const FullType(CleaningRepeatPrefillAreaEnum),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'apartmentType';
    yield serializers.serialize(
      object.apartmentType,
      specifiedType: const FullType(CleaningRepeatPrefillApartmentTypeEnum),
    );
    yield r'duplex';
    yield serializers.serialize(
      object.duplex,
      specifiedType: const FullType(bool),
    );
    yield r'cleaningType';
    yield serializers.serialize(
      object.cleaningType,
      specifiedType: const FullType(CleaningRepeatPrefillCleaningTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningRepeatPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningRepeatPrefillBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sourceOrderId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceOrderId = valueDes;
          break;
        case r'area':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningRepeatPrefillAreaEnum),
          ) as CleaningRepeatPrefillAreaEnum;
          result.area = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'apartmentType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningRepeatPrefillApartmentTypeEnum),
          ) as CleaningRepeatPrefillApartmentTypeEnum;
          result.apartmentType = valueDes;
          break;
        case r'duplex':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.duplex = valueDes;
          break;
        case r'cleaningType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningRepeatPrefillCleaningTypeEnum),
          ) as CleaningRepeatPrefillCleaningTypeEnum;
          result.cleaningType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningRepeatPrefill deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningRepeatPrefillBuilder();
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


class CleaningRepeatPrefillAreaEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAHMUTLAR')
  static const CleaningRepeatPrefillAreaEnum MAHMUTLAR = _$cleaningRepeatPrefillAreaEnum_MAHMUTLAR;
  @BuiltValueEnumConst(wireName: r'KARGICAK')
  static const CleaningRepeatPrefillAreaEnum KARGICAK = _$cleaningRepeatPrefillAreaEnum_KARGICAK;
  @BuiltValueEnumConst(wireName: r'KESTEL')
  static const CleaningRepeatPrefillAreaEnum KESTEL = _$cleaningRepeatPrefillAreaEnum_KESTEL;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningRepeatPrefillAreaEnum UNKNOWN = _$cleaningRepeatPrefillAreaEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatPrefillAreaEnum unknownDefaultOpenApi = _$cleaningRepeatPrefillAreaEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatPrefillAreaEnum> get serializer => _$cleaningRepeatPrefillAreaEnumSerializer;

  const CleaningRepeatPrefillAreaEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatPrefillAreaEnum> get values => _$cleaningRepeatPrefillAreaEnumValues;
  static CleaningRepeatPrefillAreaEnum valueOf(String name) => _$cleaningRepeatPrefillAreaEnumValueOf(name);
}

class CleaningRepeatPrefillApartmentTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'STUDIO')
  static const CleaningRepeatPrefillApartmentTypeEnum STUDIO = _$cleaningRepeatPrefillApartmentTypeEnum_STUDIO;
  @BuiltValueEnumConst(wireName: r'ONE_PLUS_ONE')
  static const CleaningRepeatPrefillApartmentTypeEnum ONE_PLUS_ONE = _$cleaningRepeatPrefillApartmentTypeEnum_ONE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'TWO_PLUS_ONE')
  static const CleaningRepeatPrefillApartmentTypeEnum TWO_PLUS_ONE = _$cleaningRepeatPrefillApartmentTypeEnum_TWO_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'THREE_PLUS_ONE')
  static const CleaningRepeatPrefillApartmentTypeEnum THREE_PLUS_ONE = _$cleaningRepeatPrefillApartmentTypeEnum_THREE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'FOUR_PLUS_ONE')
  static const CleaningRepeatPrefillApartmentTypeEnum FOUR_PLUS_ONE = _$cleaningRepeatPrefillApartmentTypeEnum_FOUR_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningRepeatPrefillApartmentTypeEnum UNKNOWN = _$cleaningRepeatPrefillApartmentTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatPrefillApartmentTypeEnum unknownDefaultOpenApi = _$cleaningRepeatPrefillApartmentTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatPrefillApartmentTypeEnum> get serializer => _$cleaningRepeatPrefillApartmentTypeEnumSerializer;

  const CleaningRepeatPrefillApartmentTypeEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatPrefillApartmentTypeEnum> get values => _$cleaningRepeatPrefillApartmentTypeEnumValues;
  static CleaningRepeatPrefillApartmentTypeEnum valueOf(String name) => _$cleaningRepeatPrefillApartmentTypeEnumValueOf(name);
}

class CleaningRepeatPrefillCleaningTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REGULAR')
  static const CleaningRepeatPrefillCleaningTypeEnum REGULAR = _$cleaningRepeatPrefillCleaningTypeEnum_REGULAR;
  @BuiltValueEnumConst(wireName: r'DEEP')
  static const CleaningRepeatPrefillCleaningTypeEnum DEEP = _$cleaningRepeatPrefillCleaningTypeEnum_DEEP;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningRepeatPrefillCleaningTypeEnum UNKNOWN = _$cleaningRepeatPrefillCleaningTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatPrefillCleaningTypeEnum unknownDefaultOpenApi = _$cleaningRepeatPrefillCleaningTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatPrefillCleaningTypeEnum> get serializer => _$cleaningRepeatPrefillCleaningTypeEnumSerializer;

  const CleaningRepeatPrefillCleaningTypeEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatPrefillCleaningTypeEnum> get values => _$cleaningRepeatPrefillCleaningTypeEnumValues;
  static CleaningRepeatPrefillCleaningTypeEnum valueOf(String name) => _$cleaningRepeatPrefillCleaningTypeEnumValueOf(name);
}

