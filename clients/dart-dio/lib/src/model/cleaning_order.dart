//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/cleaning_report.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_order.g.dart';

/// CleaningOrder
///
/// Properties:
/// * [id] 
/// * [communicationIdentityId] 
/// * [customerName] 
/// * [phone] 
/// * [area] 
/// * [address] 
/// * [apartmentType] 
/// * [duplex] 
/// * [cleaningType] 
/// * [price] 
/// * [basePrice] 
/// * [customerDiscount] 
/// * [finalCustomerPrice] 
/// * [customerDiscountType] 
/// * [currency] 
/// * [requestedDate] 
/// * [customerComment] 
/// * [cleanerComment] 
/// * [cleanerTelegramUserId] 
/// * [status] 
/// * [createdAt] 
/// * [acceptedAt] 
/// * [completedAt] 
/// * [report] 
/// * [money] 
@BuiltValue()
abstract class CleaningOrder implements Built<CleaningOrder, CleaningOrderBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'communicationIdentityId')
  int get communicationIdentityId;

  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'area')
  CleaningOrderAreaEnum get area;
  // enum areaEnum {  MAHMUTLAR,  KARGICAK,  KESTEL,  UNKNOWN,  };

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'apartmentType')
  CleaningOrderApartmentTypeEnum get apartmentType;
  // enum apartmentTypeEnum {  STUDIO,  ONE_PLUS_ONE,  TWO_PLUS_ONE,  THREE_PLUS_ONE,  FOUR_PLUS_ONE,  UNKNOWN,  };

  @BuiltValueField(wireName: r'duplex')
  bool get duplex;

  @BuiltValueField(wireName: r'cleaningType')
  CleaningOrderCleaningTypeEnum get cleaningType;
  // enum cleaningTypeEnum {  REGULAR,  DEEP,  UNKNOWN,  };

  @BuiltValueField(wireName: r'price')
  num get price;

  @BuiltValueField(wireName: r'basePrice')
  num get basePrice;

  @BuiltValueField(wireName: r'customerDiscount')
  num get customerDiscount;

  @BuiltValueField(wireName: r'finalCustomerPrice')
  num get finalCustomerPrice;

  @BuiltValueField(wireName: r'customerDiscountType')
  String get customerDiscountType;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  @BuiltValueField(wireName: r'requestedDate')
  Date get requestedDate;

  @BuiltValueField(wireName: r'customerComment')
  String? get customerComment;

  @BuiltValueField(wireName: r'cleanerComment')
  String? get cleanerComment;

  @BuiltValueField(wireName: r'cleanerTelegramUserId')
  int? get cleanerTelegramUserId;

  @BuiltValueField(wireName: r'status')
  CleaningOrderStatusEnum get status;
  // enum statusEnum {  NEW,  ACCEPTED,  AWAITING_REPORT,  ONSITE_ISSUE_REPORTED,  COMPLETED,  REJECTED,  CANCELLED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'acceptedAt')
  DateTime? get acceptedAt;

  @BuiltValueField(wireName: r'completedAt')
  DateTime? get completedAt;

  @BuiltValueField(wireName: r'report')
  CleaningReport? get report;

  @BuiltValueField(wireName: r'money')
  Money get money;

  CleaningOrder._();

  factory CleaningOrder([void updates(CleaningOrderBuilder b)]) = _$CleaningOrder;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningOrderBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningOrder> get serializer => _$CleaningOrderSerializer();
}

class _$CleaningOrderSerializer implements PrimitiveSerializer<CleaningOrder> {
  @override
  final Iterable<Type> types = const [CleaningOrder, _$CleaningOrder];

  @override
  final String wireName = r'CleaningOrder';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningOrder object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'communicationIdentityId';
    yield serializers.serialize(
      object.communicationIdentityId,
      specifiedType: const FullType(int),
    );
    yield r'customerName';
    yield serializers.serialize(
      object.customerName,
      specifiedType: const FullType(String),
    );
    yield r'phone';
    yield serializers.serialize(
      object.phone,
      specifiedType: const FullType(String),
    );
    yield r'area';
    yield serializers.serialize(
      object.area,
      specifiedType: const FullType(CleaningOrderAreaEnum),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'apartmentType';
    yield serializers.serialize(
      object.apartmentType,
      specifiedType: const FullType(CleaningOrderApartmentTypeEnum),
    );
    yield r'duplex';
    yield serializers.serialize(
      object.duplex,
      specifiedType: const FullType(bool),
    );
    yield r'cleaningType';
    yield serializers.serialize(
      object.cleaningType,
      specifiedType: const FullType(CleaningOrderCleaningTypeEnum),
    );
    yield r'price';
    yield serializers.serialize(
      object.price,
      specifiedType: const FullType(num),
    );
    yield r'basePrice';
    yield serializers.serialize(
      object.basePrice,
      specifiedType: const FullType(num),
    );
    yield r'customerDiscount';
    yield serializers.serialize(
      object.customerDiscount,
      specifiedType: const FullType(num),
    );
    yield r'finalCustomerPrice';
    yield serializers.serialize(
      object.finalCustomerPrice,
      specifiedType: const FullType(num),
    );
    yield r'customerDiscountType';
    yield serializers.serialize(
      object.customerDiscountType,
      specifiedType: const FullType(String),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
    yield r'requestedDate';
    yield serializers.serialize(
      object.requestedDate,
      specifiedType: const FullType(Date),
    );
    if (object.customerComment != null) {
      yield r'customerComment';
      yield serializers.serialize(
        object.customerComment,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.cleanerComment != null) {
      yield r'cleanerComment';
      yield serializers.serialize(
        object.cleanerComment,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.cleanerTelegramUserId != null) {
      yield r'cleanerTelegramUserId';
      yield serializers.serialize(
        object.cleanerTelegramUserId,
        specifiedType: const FullType.nullable(int),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(CleaningOrderStatusEnum),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.acceptedAt != null) {
      yield r'acceptedAt';
      yield serializers.serialize(
        object.acceptedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.completedAt != null) {
      yield r'completedAt';
      yield serializers.serialize(
        object.completedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.report != null) {
      yield r'report';
      yield serializers.serialize(
        object.report,
        specifiedType: const FullType.nullable(CleaningReport),
      );
    }
    yield r'money';
    yield serializers.serialize(
      object.money,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningOrder object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningOrderBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.id = valueDes;
          break;
        case r'communicationIdentityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.communicationIdentityId = valueDes;
          break;
        case r'customerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerName = valueDes;
          break;
        case r'phone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phone = valueDes;
          break;
        case r'area':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningOrderAreaEnum),
          ) as CleaningOrderAreaEnum;
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
            specifiedType: const FullType(CleaningOrderApartmentTypeEnum),
          ) as CleaningOrderApartmentTypeEnum;
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
            specifiedType: const FullType(CleaningOrderCleaningTypeEnum),
          ) as CleaningOrderCleaningTypeEnum;
          result.cleaningType = valueDes;
          break;
        case r'price':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.price = valueDes;
          break;
        case r'basePrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.basePrice = valueDes;
          break;
        case r'customerDiscount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.customerDiscount = valueDes;
          break;
        case r'finalCustomerPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.finalCustomerPrice = valueDes;
          break;
        case r'customerDiscountType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerDiscountType = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        case r'requestedDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.requestedDate = valueDes;
          break;
        case r'customerComment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.customerComment = valueDes;
          break;
        case r'cleanerComment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.cleanerComment = valueDes;
          break;
        case r'cleanerTelegramUserId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.cleanerTelegramUserId = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningOrderStatusEnum),
          ) as CleaningOrderStatusEnum;
          result.status = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'acceptedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.acceptedAt = valueDes;
          break;
        case r'completedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.completedAt = valueDes;
          break;
        case r'report':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CleaningReport),
          ) as CleaningReport?;
          if (valueDes == null) continue;
          result.report.replace(valueDes);
          break;
        case r'money':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.money.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningOrder deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningOrderBuilder();
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


class CleaningOrderAreaEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAHMUTLAR')
  static const CleaningOrderAreaEnum MAHMUTLAR = _$cleaningOrderAreaEnum_MAHMUTLAR;
  @BuiltValueEnumConst(wireName: r'KARGICAK')
  static const CleaningOrderAreaEnum KARGICAK = _$cleaningOrderAreaEnum_KARGICAK;
  @BuiltValueEnumConst(wireName: r'KESTEL')
  static const CleaningOrderAreaEnum KESTEL = _$cleaningOrderAreaEnum_KESTEL;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderAreaEnum UNKNOWN = _$cleaningOrderAreaEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderAreaEnum unknownDefaultOpenApi = _$cleaningOrderAreaEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderAreaEnum> get serializer => _$cleaningOrderAreaEnumSerializer;

  const CleaningOrderAreaEnum._(String name): super(name);

  static BuiltSet<CleaningOrderAreaEnum> get values => _$cleaningOrderAreaEnumValues;
  static CleaningOrderAreaEnum valueOf(String name) => _$cleaningOrderAreaEnumValueOf(name);
}

class CleaningOrderApartmentTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'STUDIO')
  static const CleaningOrderApartmentTypeEnum STUDIO = _$cleaningOrderApartmentTypeEnum_STUDIO;
  @BuiltValueEnumConst(wireName: r'ONE_PLUS_ONE')
  static const CleaningOrderApartmentTypeEnum ONE_PLUS_ONE = _$cleaningOrderApartmentTypeEnum_ONE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'TWO_PLUS_ONE')
  static const CleaningOrderApartmentTypeEnum TWO_PLUS_ONE = _$cleaningOrderApartmentTypeEnum_TWO_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'THREE_PLUS_ONE')
  static const CleaningOrderApartmentTypeEnum THREE_PLUS_ONE = _$cleaningOrderApartmentTypeEnum_THREE_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'FOUR_PLUS_ONE')
  static const CleaningOrderApartmentTypeEnum FOUR_PLUS_ONE = _$cleaningOrderApartmentTypeEnum_FOUR_PLUS_ONE;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderApartmentTypeEnum UNKNOWN = _$cleaningOrderApartmentTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderApartmentTypeEnum unknownDefaultOpenApi = _$cleaningOrderApartmentTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderApartmentTypeEnum> get serializer => _$cleaningOrderApartmentTypeEnumSerializer;

  const CleaningOrderApartmentTypeEnum._(String name): super(name);

  static BuiltSet<CleaningOrderApartmentTypeEnum> get values => _$cleaningOrderApartmentTypeEnumValues;
  static CleaningOrderApartmentTypeEnum valueOf(String name) => _$cleaningOrderApartmentTypeEnumValueOf(name);
}

class CleaningOrderCleaningTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REGULAR')
  static const CleaningOrderCleaningTypeEnum REGULAR = _$cleaningOrderCleaningTypeEnum_REGULAR;
  @BuiltValueEnumConst(wireName: r'DEEP')
  static const CleaningOrderCleaningTypeEnum DEEP = _$cleaningOrderCleaningTypeEnum_DEEP;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderCleaningTypeEnum UNKNOWN = _$cleaningOrderCleaningTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderCleaningTypeEnum unknownDefaultOpenApi = _$cleaningOrderCleaningTypeEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderCleaningTypeEnum> get serializer => _$cleaningOrderCleaningTypeEnumSerializer;

  const CleaningOrderCleaningTypeEnum._(String name): super(name);

  static BuiltSet<CleaningOrderCleaningTypeEnum> get values => _$cleaningOrderCleaningTypeEnumValues;
  static CleaningOrderCleaningTypeEnum valueOf(String name) => _$cleaningOrderCleaningTypeEnumValueOf(name);
}

class CleaningOrderStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NEW')
  static const CleaningOrderStatusEnum NEW = _$cleaningOrderStatusEnum_NEW;
  @BuiltValueEnumConst(wireName: r'ACCEPTED')
  static const CleaningOrderStatusEnum ACCEPTED = _$cleaningOrderStatusEnum_ACCEPTED;
  @BuiltValueEnumConst(wireName: r'AWAITING_REPORT')
  static const CleaningOrderStatusEnum AWAITING_REPORT = _$cleaningOrderStatusEnum_AWAITING_REPORT;
  @BuiltValueEnumConst(wireName: r'ONSITE_ISSUE_REPORTED')
  static const CleaningOrderStatusEnum ONSITE_ISSUE_REPORTED = _$cleaningOrderStatusEnum_ONSITE_ISSUE_REPORTED;
  @BuiltValueEnumConst(wireName: r'COMPLETED')
  static const CleaningOrderStatusEnum COMPLETED = _$cleaningOrderStatusEnum_COMPLETED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const CleaningOrderStatusEnum REJECTED = _$cleaningOrderStatusEnum_REJECTED;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const CleaningOrderStatusEnum CANCELLED = _$cleaningOrderStatusEnum_CANCELLED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningOrderStatusEnum UNKNOWN = _$cleaningOrderStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningOrderStatusEnum unknownDefaultOpenApi = _$cleaningOrderStatusEnum_unknownDefaultOpenApi;

  static Serializer<CleaningOrderStatusEnum> get serializer => _$cleaningOrderStatusEnumSerializer;

  const CleaningOrderStatusEnum._(String name): super(name);

  static BuiltSet<CleaningOrderStatusEnum> get values => _$cleaningOrderStatusEnumValues;
  static CleaningOrderStatusEnum valueOf(String name) => _$cleaningOrderStatusEnumValueOf(name);
}

