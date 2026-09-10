//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/apartment_price.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_configuration.g.dart';

/// CleaningConfiguration
///
/// Properties:
/// * [areas] 
/// * [apartmentTypes] 
/// * [duplexSurcharges] 
/// * [bookingDaysAhead] 
/// * [currency] 
@BuiltValue()
abstract class CleaningConfiguration implements Built<CleaningConfiguration, CleaningConfigurationBuilder> {
  @BuiltValueField(wireName: r'areas')
  BuiltList<CleaningConfigurationAreasEnum> get areas;
  // enum areasEnum {  MAHMUTLAR,  KARGICAK,  KESTEL,  UNKNOWN,  };

  @BuiltValueField(wireName: r'apartmentTypes')
  BuiltList<ApartmentPrice> get apartmentTypes;

  @BuiltValueField(wireName: r'duplexSurcharges')
  BuiltMap<String, num> get duplexSurcharges;

  @BuiltValueField(wireName: r'bookingDaysAhead')
  int get bookingDaysAhead;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  CleaningConfiguration._();

  factory CleaningConfiguration([void updates(CleaningConfigurationBuilder b)]) = _$CleaningConfiguration;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningConfigurationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningConfiguration> get serializer => _$CleaningConfigurationSerializer();
}

class _$CleaningConfigurationSerializer implements PrimitiveSerializer<CleaningConfiguration> {
  @override
  final Iterable<Type> types = const [CleaningConfiguration, _$CleaningConfiguration];

  @override
  final String wireName = r'CleaningConfiguration';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'areas';
    yield serializers.serialize(
      object.areas,
      specifiedType: const FullType(BuiltList, [FullType(CleaningConfigurationAreasEnum)]),
    );
    yield r'apartmentTypes';
    yield serializers.serialize(
      object.apartmentTypes,
      specifiedType: const FullType(BuiltList, [FullType(ApartmentPrice)]),
    );
    yield r'duplexSurcharges';
    yield serializers.serialize(
      object.duplexSurcharges,
      specifiedType: const FullType(BuiltMap, [FullType(String), FullType(num)]),
    );
    yield r'bookingDaysAhead';
    yield serializers.serialize(
      object.bookingDaysAhead,
      specifiedType: const FullType(int),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningConfigurationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'areas':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CleaningConfigurationAreasEnum)]),
          ) as BuiltList<CleaningConfigurationAreasEnum>;
          result.areas.replace(valueDes);
          break;
        case r'apartmentTypes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ApartmentPrice)]),
          ) as BuiltList<ApartmentPrice>;
          result.apartmentTypes.replace(valueDes);
          break;
        case r'duplexSurcharges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(num)]),
          ) as BuiltMap<String, num>;
          result.duplexSurcharges.replace(valueDes);
          break;
        case r'bookingDaysAhead':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.bookingDaysAhead = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningConfiguration deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningConfigurationBuilder();
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


class CleaningConfigurationAreasEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAHMUTLAR')
  static const CleaningConfigurationAreasEnum MAHMUTLAR = _$cleaningConfigurationAreasEnum_MAHMUTLAR;
  @BuiltValueEnumConst(wireName: r'KARGICAK')
  static const CleaningConfigurationAreasEnum KARGICAK = _$cleaningConfigurationAreasEnum_KARGICAK;
  @BuiltValueEnumConst(wireName: r'KESTEL')
  static const CleaningConfigurationAreasEnum KESTEL = _$cleaningConfigurationAreasEnum_KESTEL;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningConfigurationAreasEnum UNKNOWN = _$cleaningConfigurationAreasEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningConfigurationAreasEnum unknownDefaultOpenApi = _$cleaningConfigurationAreasEnum_unknownDefaultOpenApi;

  static Serializer<CleaningConfigurationAreasEnum> get serializer => _$cleaningConfigurationAreasEnumSerializer;

  const CleaningConfigurationAreasEnum._(String name): super(name);

  static BuiltSet<CleaningConfigurationAreasEnum> get values => _$cleaningConfigurationAreasEnumValues;
  static CleaningConfigurationAreasEnum valueOf(String name) => _$cleaningConfigurationAreasEnumValueOf(name);
}

