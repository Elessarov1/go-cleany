//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_property_media.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_property.g.dart';

/// RentalProperty
///
/// Properties:
/// * [id] 
/// * [slug] 
/// * [titleRu] 
/// * [titleEn] 
/// * [descriptionEn] 
/// * [area] 
/// * [address] 
/// * [apartmentNumber] 
/// * [bedrooms] 
/// * [beds] 
/// * [bathrooms] 
/// * [maxGuests] 
/// * [areaSqm] 
/// * [floor] 
/// * [baseDailyPrice] 
/// * [currency] 
/// * [displayOrder] 
/// * [status] 
/// * [amenities] 
/// * [media] 
/// * [createdAt] 
/// * [updatedAt] 
@BuiltValue()
abstract class RentalProperty implements Built<RentalProperty, RentalPropertyBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'slug')
  String? get slug;

  @BuiltValueField(wireName: r'titleRu')
  String? get titleRu;

  @BuiltValueField(wireName: r'titleEn')
  String? get titleEn;

  @BuiltValueField(wireName: r'descriptionEn')
  String? get descriptionEn;

  @BuiltValueField(wireName: r'area')
  String? get area;

  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'apartmentNumber')
  String? get apartmentNumber;

  @BuiltValueField(wireName: r'bedrooms')
  int? get bedrooms;

  @BuiltValueField(wireName: r'beds')
  int? get beds;

  @BuiltValueField(wireName: r'bathrooms')
  int? get bathrooms;

  @BuiltValueField(wireName: r'maxGuests')
  int? get maxGuests;

  @BuiltValueField(wireName: r'areaSqm')
  num? get areaSqm;

  @BuiltValueField(wireName: r'floor')
  int? get floor;

  @BuiltValueField(wireName: r'baseDailyPrice')
  num? get baseDailyPrice;

  @BuiltValueField(wireName: r'currency')
  String? get currency;

  @BuiltValueField(wireName: r'displayOrder')
  int get displayOrder;

  @BuiltValueField(wireName: r'status')
  RentalPropertyStatusEnum get status;
  // enum statusEnum {  DRAFT,  PUBLISHED,  ARCHIVED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'amenities')
  BuiltList<RentalPropertyAmenitiesEnum> get amenities;
  // enum amenitiesEnum {  WIFI,  AIR_CONDITIONING,  WASHING_MACHINE,  DISHWASHER,  BALCONY,  SEA_VIEW,  POOL,  PARKING,  ELEVATOR,  WORKSPACE,  TV,  KITCHEN,  UNKNOWN,  };

  @BuiltValueField(wireName: r'media')
  BuiltList<RentalPropertyMedia> get media;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  RentalProperty._();

  factory RentalProperty([void updates(RentalPropertyBuilder b)]) = _$RentalProperty;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalPropertyBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalProperty> get serializer => _$RentalPropertySerializer();
}

class _$RentalPropertySerializer implements PrimitiveSerializer<RentalProperty> {
  @override
  final Iterable<Type> types = const [RentalProperty, _$RentalProperty];

  @override
  final String wireName = r'RentalProperty';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'slug';
    yield object.slug == null ? null : serializers.serialize(
      object.slug,
      specifiedType: const FullType.nullable(String),
    );
    yield r'titleRu';
    yield object.titleRu == null ? null : serializers.serialize(
      object.titleRu,
      specifiedType: const FullType.nullable(String),
    );
    yield r'titleEn';
    yield object.titleEn == null ? null : serializers.serialize(
      object.titleEn,
      specifiedType: const FullType.nullable(String),
    );
    yield r'descriptionEn';
    yield object.descriptionEn == null ? null : serializers.serialize(
      object.descriptionEn,
      specifiedType: const FullType.nullable(String),
    );
    yield r'area';
    yield object.area == null ? null : serializers.serialize(
      object.area,
      specifiedType: const FullType.nullable(String),
    );
    yield r'address';
    yield object.address == null ? null : serializers.serialize(
      object.address,
      specifiedType: const FullType.nullable(String),
    );
    yield r'apartmentNumber';
    yield object.apartmentNumber == null ? null : serializers.serialize(
      object.apartmentNumber,
      specifiedType: const FullType.nullable(String),
    );
    yield r'bedrooms';
    yield object.bedrooms == null ? null : serializers.serialize(
      object.bedrooms,
      specifiedType: const FullType.nullable(int),
    );
    yield r'beds';
    yield object.beds == null ? null : serializers.serialize(
      object.beds,
      specifiedType: const FullType.nullable(int),
    );
    yield r'bathrooms';
    yield object.bathrooms == null ? null : serializers.serialize(
      object.bathrooms,
      specifiedType: const FullType.nullable(int),
    );
    yield r'maxGuests';
    yield object.maxGuests == null ? null : serializers.serialize(
      object.maxGuests,
      specifiedType: const FullType.nullable(int),
    );
    yield r'areaSqm';
    yield object.areaSqm == null ? null : serializers.serialize(
      object.areaSqm,
      specifiedType: const FullType.nullable(num),
    );
    yield r'floor';
    yield object.floor == null ? null : serializers.serialize(
      object.floor,
      specifiedType: const FullType.nullable(int),
    );
    yield r'baseDailyPrice';
    yield object.baseDailyPrice == null ? null : serializers.serialize(
      object.baseDailyPrice,
      specifiedType: const FullType.nullable(num),
    );
    yield r'currency';
    yield object.currency == null ? null : serializers.serialize(
      object.currency,
      specifiedType: const FullType.nullable(String),
    );
    yield r'displayOrder';
    yield serializers.serialize(
      object.displayOrder,
      specifiedType: const FullType(int),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(RentalPropertyStatusEnum),
    );
    yield r'amenities';
    yield serializers.serialize(
      object.amenities,
      specifiedType: const FullType(BuiltList, [FullType(RentalPropertyAmenitiesEnum)]),
    );
    yield r'media';
    yield serializers.serialize(
      object.media,
      specifiedType: const FullType(BuiltList, [FullType(RentalPropertyMedia)]),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalPropertyBuilder result,
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
        case r'slug':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.slug = valueDes;
          break;
        case r'titleRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.titleRu = valueDes;
          break;
        case r'titleEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.titleEn = valueDes;
          break;
        case r'descriptionEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.descriptionEn = valueDes;
          break;
        case r'area':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.area = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.address = valueDes;
          break;
        case r'apartmentNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.apartmentNumber = valueDes;
          break;
        case r'bedrooms':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.bedrooms = valueDes;
          break;
        case r'beds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.beds = valueDes;
          break;
        case r'bathrooms':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.bathrooms = valueDes;
          break;
        case r'maxGuests':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.maxGuests = valueDes;
          break;
        case r'areaSqm':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.areaSqm = valueDes;
          break;
        case r'floor':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.floor = valueDes;
          break;
        case r'baseDailyPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.baseDailyPrice = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.currency = valueDes;
          break;
        case r'displayOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.displayOrder = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalPropertyStatusEnum),
          ) as RentalPropertyStatusEnum;
          result.status = valueDes;
          break;
        case r'amenities':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalPropertyAmenitiesEnum)]),
          ) as BuiltList<RentalPropertyAmenitiesEnum>;
          result.amenities.replace(valueDes);
          break;
        case r'media':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalPropertyMedia)]),
          ) as BuiltList<RentalPropertyMedia>;
          result.media.replace(valueDes);
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalProperty deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalPropertyBuilder();
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


class RentalPropertyStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const RentalPropertyStatusEnum DRAFT = _$rentalPropertyStatusEnum_DRAFT;
  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const RentalPropertyStatusEnum PUBLISHED = _$rentalPropertyStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'ARCHIVED')
  static const RentalPropertyStatusEnum ARCHIVED = _$rentalPropertyStatusEnum_ARCHIVED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalPropertyStatusEnum UNKNOWN = _$rentalPropertyStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalPropertyStatusEnum unknownDefaultOpenApi = _$rentalPropertyStatusEnum_unknownDefaultOpenApi;

  static Serializer<RentalPropertyStatusEnum> get serializer => _$rentalPropertyStatusEnumSerializer;

  const RentalPropertyStatusEnum._(String name): super(name);

  static BuiltSet<RentalPropertyStatusEnum> get values => _$rentalPropertyStatusEnumValues;
  static RentalPropertyStatusEnum valueOf(String name) => _$rentalPropertyStatusEnumValueOf(name);
}

class RentalPropertyAmenitiesEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'WIFI')
  static const RentalPropertyAmenitiesEnum WIFI = _$rentalPropertyAmenitiesEnum_WIFI;
  @BuiltValueEnumConst(wireName: r'AIR_CONDITIONING')
  static const RentalPropertyAmenitiesEnum AIR_CONDITIONING = _$rentalPropertyAmenitiesEnum_AIR_CONDITIONING;
  @BuiltValueEnumConst(wireName: r'WASHING_MACHINE')
  static const RentalPropertyAmenitiesEnum WASHING_MACHINE = _$rentalPropertyAmenitiesEnum_WASHING_MACHINE;
  @BuiltValueEnumConst(wireName: r'DISHWASHER')
  static const RentalPropertyAmenitiesEnum DISHWASHER = _$rentalPropertyAmenitiesEnum_DISHWASHER;
  @BuiltValueEnumConst(wireName: r'BALCONY')
  static const RentalPropertyAmenitiesEnum BALCONY = _$rentalPropertyAmenitiesEnum_BALCONY;
  @BuiltValueEnumConst(wireName: r'SEA_VIEW')
  static const RentalPropertyAmenitiesEnum SEA_VIEW = _$rentalPropertyAmenitiesEnum_SEA_VIEW;
  @BuiltValueEnumConst(wireName: r'POOL')
  static const RentalPropertyAmenitiesEnum POOL = _$rentalPropertyAmenitiesEnum_POOL;
  @BuiltValueEnumConst(wireName: r'PARKING')
  static const RentalPropertyAmenitiesEnum PARKING = _$rentalPropertyAmenitiesEnum_PARKING;
  @BuiltValueEnumConst(wireName: r'ELEVATOR')
  static const RentalPropertyAmenitiesEnum ELEVATOR = _$rentalPropertyAmenitiesEnum_ELEVATOR;
  @BuiltValueEnumConst(wireName: r'WORKSPACE')
  static const RentalPropertyAmenitiesEnum WORKSPACE = _$rentalPropertyAmenitiesEnum_WORKSPACE;
  @BuiltValueEnumConst(wireName: r'TV')
  static const RentalPropertyAmenitiesEnum TV = _$rentalPropertyAmenitiesEnum_TV;
  @BuiltValueEnumConst(wireName: r'KITCHEN')
  static const RentalPropertyAmenitiesEnum KITCHEN = _$rentalPropertyAmenitiesEnum_KITCHEN;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalPropertyAmenitiesEnum UNKNOWN = _$rentalPropertyAmenitiesEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalPropertyAmenitiesEnum unknownDefaultOpenApi = _$rentalPropertyAmenitiesEnum_unknownDefaultOpenApi;

  static Serializer<RentalPropertyAmenitiesEnum> get serializer => _$rentalPropertyAmenitiesEnumSerializer;

  const RentalPropertyAmenitiesEnum._(String name): super(name);

  static BuiltSet<RentalPropertyAmenitiesEnum> get values => _$rentalPropertyAmenitiesEnumValues;
  static RentalPropertyAmenitiesEnum valueOf(String name) => _$rentalPropertyAmenitiesEnumValueOf(name);
}

