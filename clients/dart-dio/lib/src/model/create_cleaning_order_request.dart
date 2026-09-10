//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_cleaning_order_request.g.dart';

/// CreateCleaningOrderRequest
///
/// Properties:
/// * [area] 
/// * [address] 
/// * [apartmentType] 
/// * [duplex] 
/// * [cleaningType] 
/// * [requestedDate] 
/// * [phone] 
/// * [comment] 
/// * [referralCode] 
/// * [rentalCleaningPromoCode] 
/// * [repeatFromOrderId] 
@BuiltValue()
abstract class CreateCleaningOrderRequest implements Built<CreateCleaningOrderRequest, CreateCleaningOrderRequestBuilder> {
  @BuiltValueField(wireName: r'area')
  CreateCleaningOrderRequestAreaEnum get area;
  // enum areaEnum {  MAHMUTLAR,  KARGICAK,  KESTEL,  UNKNOWN,  };

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'apartmentType')
  CreateCleaningOrderRequestApartmentTypeEnum get apartmentType;
  // enum apartmentTypeEnum {  STUDIO,  ONE_PLUS_ONE,  TWO_PLUS_ONE,  THREE_PLUS_ONE,  FOUR_PLUS_ONE,  UNKNOWN,  };

  @BuiltValueField(wireName: r'duplex')
  bool get duplex;

  @BuiltValueField(wireName: r'cleaningType')
  CreateCleaningOrderRequestCleaningTypeEnum get cleaningType;
  // enum cleaningTypeEnum {  REGULAR,  DEEP,  UNKNOWN,  };

  @BuiltValueField(wireName: r'requestedDate')
  Date get requestedDate;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  @BuiltValueField(wireName: r'referralCode')
  String? get referralCode;

  @BuiltValueField(wireName: r'rentalCleaningPromoCode')
  String? get rentalCleaningPromoCode;

  @BuiltValueField(wireName: r'repeatFromOrderId')
  int? get repeatFromOrderId;

  CreateCleaningOrderRequest._();

  factory CreateCleaningOrderRequest([void updates(CreateCleaningOrderRequestBuilder b)]) = _$CreateCleaningOrderRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateCleaningOrderRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateCleaningOrderRequest> get serializer => _$CreateCleaningOrderRequestSerializer();
}

class _$CreateCleaningOrderRequestSerializer implements PrimitiveSerializer<CreateCleaningOrderRequest> {
  @override
  final Iterable<Type> types = const [CreateCleaningOrderRequest, _$CreateCleaningOrderRequest];

  @override
  final String wireName = r'CreateCleaningOrderRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateCleaningOrderRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'area';
    yield serializers.serialize(
      object.area,
      specifiedType: const FullType(CreateCleaningOrderRequestAreaEnum),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'apartmentType';
    yield serializers.serialize(
      object.apartmentType,
      specifiedType: const FullType(CreateCleaningOrderRequestApartmentTypeEnum),
    );
    yield r'duplex';
    yield serializers.serialize(
      object.duplex,
      specifiedType: const FullType(bool),
    );
    yield r'cleaningType';
    yield serializers.serialize(
      object.cleaningType,
      specifiedType: const FullType(CreateCleaningOrderRequestCleaningTypeEnum),
    );
    yield r'requestedDate';
    yield serializers.serialize(
      object.requestedDate,
      specifiedType: const FullType(Date),
    );
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
    if (object.referralCode != null) {
      yield r'referralCode';
      yield serializers.serialize(
        object.referralCode,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.rentalCleaningPromoCode != null) {
      yield r'rentalCleaningPromoCode';
      yield serializers.serialize(
        object.rentalCleaningPromoCode,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.repeatFromOrderId != null) {
      yield r'repeatFromOrderId';
      yield serializers.serialize(
        object.repeatFromOrderId,
        specifiedType: const FullType.nullable(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateCleaningOrderRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateCleaningOrderRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'area':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateCleaningOrderRequestAreaEnum),
          ) as CreateCleaningOrderRequestAreaEnum;
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
            specifiedType: const FullType(CreateCleaningOrderRequestApartmentTypeEnum),
          ) as CreateCleaningOrderRequestApartmentTypeEnum;
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
            specifiedType: const FullType(CreateCleaningOrderRequestCleaningTypeEnum),
          ) as CreateCleaningOrderRequestCleaningTypeEnum;
          result.cleaningType = valueDes;
          break;
        case r'requestedDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.requestedDate = valueDes;
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
        case r'referralCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.referralCode = valueDes;
          break;
        case r'rentalCleaningPromoCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.rentalCleaningPromoCode = valueDes;
          break;
        case r'repeatFromOrderId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.repeatFromOrderId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateCleaningOrderRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateCleaningOrderRequestBuilder();
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


class CreateCleaningOrderRequestAreaEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAHMUTLAR')
  static const CreateCleaningOrderRequestAreaEnum MAHMUTLAR = _$createCleaningOrderRequestAreaEnum_MAHMUTLAR;
  @BuiltValueEnumConst(wireName: r'KARGICAK')
  static const CreateCleaningOrderRequestAreaEnum KARGICAK = _$createCleaningOrderRequestAreaEnum_KARGICAK;
  @BuiltValueEnumConst(wireName: r'KESTEL')
  static const CreateCleaningOrderRequestAreaEnum KESTEL = _$createCleaningOrderRequestAreaEnum_KESTEL;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateCleaningOrderRequestAreaEnum UNKNOWN = _$createCleaningOrderRequestAreaEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateCleaningOrderRequestAreaEnum unknownDefaultOpenApi = _$createCleaningOrderRequestAreaEnum_unknownDefaultOpenApi;

  static Serializer<CreateCleaningOrderRequestAreaEnum> get serializer => _$createCleaningOrderRequestAreaEnumSerializer;

  const CreateCleaningOrderRequestAreaEnum._(String name): super(name);

  static BuiltSet<CreateCleaningOrderRequestAreaEnum> get values => _$createCleaningOrderRequestAreaEnumValues;
  static CreateCleaningOrderRequestAreaEnum valueOf(String name) => _$createCleaningOrderRequestAreaEnumValueOf(name);
}

class CreateCleaningOrderRequestApartmentTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'STUDIO')
  static const CreateCleaningOrderRequestApartmentTypeEnum STUDIO = _$createCleaningOrderRequestApartmentTypeEnum_STUDIO;
  @BuiltValueEnumConst(wireName: r'ONE_PLUS_ONE')
  static const CreateCleaningOrderRequestApartmentTypeEnum ONE_PLUS_ONE = _$createCleaningOrderRequestApartmentTypeEnum_ONE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'TWO_PLUS_ONE')
  static const CreateCleaningOrderRequestApartmentTypeEnum TWO_PLUS_ONE = _$createCleaningOrderRequestApartmentTypeEnum_TWO_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'THREE_PLUS_ONE')
  static const CreateCleaningOrderRequestApartmentTypeEnum THREE_PLUS_ONE = _$createCleaningOrderRequestApartmentTypeEnum_THREE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'FOUR_PLUS_ONE')
  static const CreateCleaningOrderRequestApartmentTypeEnum FOUR_PLUS_ONE = _$createCleaningOrderRequestApartmentTypeEnum_FOUR_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateCleaningOrderRequestApartmentTypeEnum UNKNOWN = _$createCleaningOrderRequestApartmentTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateCleaningOrderRequestApartmentTypeEnum unknownDefaultOpenApi = _$createCleaningOrderRequestApartmentTypeEnum_unknownDefaultOpenApi;

  static Serializer<CreateCleaningOrderRequestApartmentTypeEnum> get serializer => _$createCleaningOrderRequestApartmentTypeEnumSerializer;

  const CreateCleaningOrderRequestApartmentTypeEnum._(String name): super(name);

  static BuiltSet<CreateCleaningOrderRequestApartmentTypeEnum> get values => _$createCleaningOrderRequestApartmentTypeEnumValues;
  static CreateCleaningOrderRequestApartmentTypeEnum valueOf(String name) => _$createCleaningOrderRequestApartmentTypeEnumValueOf(name);
}

class CreateCleaningOrderRequestCleaningTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REGULAR')
  static const CreateCleaningOrderRequestCleaningTypeEnum REGULAR = _$createCleaningOrderRequestCleaningTypeEnum_REGULAR;
  @BuiltValueEnumConst(wireName: r'DEEP')
  static const CreateCleaningOrderRequestCleaningTypeEnum DEEP = _$createCleaningOrderRequestCleaningTypeEnum_DEEP;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CreateCleaningOrderRequestCleaningTypeEnum UNKNOWN = _$createCleaningOrderRequestCleaningTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateCleaningOrderRequestCleaningTypeEnum unknownDefaultOpenApi = _$createCleaningOrderRequestCleaningTypeEnum_unknownDefaultOpenApi;

  static Serializer<CreateCleaningOrderRequestCleaningTypeEnum> get serializer => _$createCleaningOrderRequestCleaningTypeEnumSerializer;

  const CreateCleaningOrderRequestCleaningTypeEnum._(String name): super(name);

  static BuiltSet<CreateCleaningOrderRequestCleaningTypeEnum> get values => _$createCleaningOrderRequestCleaningTypeEnumValues;
  static CreateCleaningOrderRequestCleaningTypeEnum valueOf(String name) => _$createCleaningOrderRequestCleaningTypeEnumValueOf(name);
}

