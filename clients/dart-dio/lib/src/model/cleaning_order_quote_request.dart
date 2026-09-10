//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_order_quote_request.g.dart';

/// CleaningOrderQuoteRequest
///
/// Properties:
/// * [apartmentType] 
/// * [duplex] 
/// * [cleaningType] 
/// * [referralCode] 
/// * [requestedDate] 
/// * [rentalCleaningPromoCode] 
@BuiltValue()
abstract class CleaningOrderQuoteRequest implements Built<CleaningOrderQuoteRequest, CleaningOrderQuoteRequestBuilder> {
  @BuiltValueField(wireName: r'apartmentType')
  CleaningOrderQuoteRequestApartmentTypeEnum get apartmentType;
  // enum apartmentTypeEnum {  STUDIO,  ONE_PLUS_ONE,  TWO_PLUS_ONE,  THREE_PLUS_ONE,  FOUR_PLUS_ONE,  UNKNOWN,  };

  @BuiltValueField(wireName: r'duplex')
  bool get duplex;

  @BuiltValueField(wireName: r'cleaningType')
  CleaningOrderQuoteRequestCleaningTypeEnum get cleaningType;
  // enum cleaningTypeEnum {  REGULAR,  DEEP,  UNKNOWN,  };

  @BuiltValueField(wireName: r'referralCode')
  String? get referralCode;

  @BuiltValueField(wireName: r'requestedDate')
  Date? get requestedDate;

  @BuiltValueField(wireName: r'rentalCleaningPromoCode')
  String? get rentalCleaningPromoCode;

  CleaningOrderQuoteRequest._();

  factory CleaningOrderQuoteRequest([void updates(CleaningOrderQuoteRequestBuilder b)]) = _$CleaningOrderQuoteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningOrderQuoteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningOrderQuoteRequest> get serializer => _$CleaningOrderQuoteRequestSerializer();
}

class _$CleaningOrderQuoteRequestSerializer implements PrimitiveSerializer<CleaningOrderQuoteRequest> {
  @override
  final Iterable<Type> types = const [CleaningOrderQuoteRequest, _$CleaningOrderQuoteRequest];

  @override
  final String wireName = r'CleaningOrderQuoteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningOrderQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'apartmentType';
    yield serializers.serialize(
      object.apartmentType,
      specifiedType: const FullType(CleaningOrderQuoteRequestApartmentTypeEnum),
    );
    yield r'duplex';
    yield serializers.serialize(
      object.duplex,
      specifiedType: const FullType(bool),
    );
    yield r'cleaningType';
    yield serializers.serialize(
      object.cleaningType,
      specifiedType: const FullType(CleaningOrderQuoteRequestCleaningTypeEnum),
    );
    if (object.referralCode != null) {
      yield r'referralCode';
      yield serializers.serialize(
        object.referralCode,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.requestedDate != null) {
      yield r'requestedDate';
      yield serializers.serialize(
        object.requestedDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.rentalCleaningPromoCode != null) {
      yield r'rentalCleaningPromoCode';
      yield serializers.serialize(
        object.rentalCleaningPromoCode,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningOrderQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningOrderQuoteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'apartmentType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningOrderQuoteRequestApartmentTypeEnum),
          ) as CleaningOrderQuoteRequestApartmentTypeEnum;
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
            specifiedType: const FullType(CleaningOrderQuoteRequestCleaningTypeEnum),
          ) as CleaningOrderQuoteRequestCleaningTypeEnum;
          result.cleaningType = valueDes;
          break;
        case r'referralCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.referralCode = valueDes;
          break;
        case r'requestedDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.requestedDate = valueDes;
          break;
        case r'rentalCleaningPromoCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.rentalCleaningPromoCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningOrderQuoteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningOrderQuoteRequestBuilder();
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


class CleaningOrderQuoteRequestApartmentTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'STUDIO')
  static const CleaningOrderQuoteRequestApartmentTypeEnum STUDIO = _$cleaningOrderQuoteRequestApartmentTypeEnum_STUDIO;
  @BuiltValueEnumConst(wireName: r'ONE_PLUS_ONE')
  static const CleaningOrderQuoteRequestApartmentTypeEnum ONE_PLUS_ONE = _$cleaningOrderQuoteRequestApartmentTypeEnum_ONE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'TWO_PLUS_ONE')
  static const CleaningOrderQuoteRequestApartmentTypeEnum TWO_PLUS_ONE = _$cleaningOrderQuoteRequestApartmentTypeEnum_TWO_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'THREE_PLUS_ONE')
  static const CleaningOrderQuoteRequestApartmentTypeEnum THREE_PLUS_ONE = _$cleaningOrderQuoteRequestApartmentTypeEnum_THREE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'FOUR_PLUS_ONE')
  static const CleaningOrderQuoteRequestApartmentTypeEnum FOUR_PLUS_ONE = _$cleaningOrderQuoteRequestApartmentTypeEnum_FOUR_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderQuoteRequestApartmentTypeEnum UNKNOWN = _$cleaningOrderQuoteRequestApartmentTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderQuoteRequestApartmentTypeEnum unknownDefaultOpenApi = _$cleaningOrderQuoteRequestApartmentTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderQuoteRequestApartmentTypeEnum> get serializer => _$cleaningOrderQuoteRequestApartmentTypeEnumSerializer;

  const CleaningOrderQuoteRequestApartmentTypeEnum._(String name): super(name);

  static BuiltSet<CleaningOrderQuoteRequestApartmentTypeEnum> get values => _$cleaningOrderQuoteRequestApartmentTypeEnumValues;
  static CleaningOrderQuoteRequestApartmentTypeEnum valueOf(String name) => _$cleaningOrderQuoteRequestApartmentTypeEnumValueOf(name);
}

class CleaningOrderQuoteRequestCleaningTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REGULAR')
  static const CleaningOrderQuoteRequestCleaningTypeEnum REGULAR = _$cleaningOrderQuoteRequestCleaningTypeEnum_REGULAR;
  @BuiltValueEnumConst(wireName: r'DEEP')
  static const CleaningOrderQuoteRequestCleaningTypeEnum DEEP = _$cleaningOrderQuoteRequestCleaningTypeEnum_DEEP;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderQuoteRequestCleaningTypeEnum UNKNOWN = _$cleaningOrderQuoteRequestCleaningTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderQuoteRequestCleaningTypeEnum unknownDefaultOpenApi = _$cleaningOrderQuoteRequestCleaningTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderQuoteRequestCleaningTypeEnum> get serializer => _$cleaningOrderQuoteRequestCleaningTypeEnumSerializer;

  const CleaningOrderQuoteRequestCleaningTypeEnum._(String name): super(name);

  static BuiltSet<CleaningOrderQuoteRequestCleaningTypeEnum> get values => _$cleaningOrderQuoteRequestCleaningTypeEnumValues;
  static CleaningOrderQuoteRequestCleaningTypeEnum valueOf(String name) => _$cleaningOrderQuoteRequestCleaningTypeEnumValueOf(name);
}

