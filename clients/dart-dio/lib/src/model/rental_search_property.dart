//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/rental_price.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_search_property.g.dart';

/// RentalSearchProperty
///
/// Properties:
/// * [id] 
/// * [slug] 
/// * [titleRu] 
/// * [titleEn] 
/// * [descriptionEn] 
/// * [area] 
/// * [bedrooms] 
/// * [maxGuests] 
/// * [areaSqm] 
/// * [baseDailyPrice] 
/// * [currency] 
/// * [coverUrl] 
/// * [price] 
@BuiltValue()
abstract class RentalSearchProperty implements Built<RentalSearchProperty, RentalSearchPropertyBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'slug')
  String get slug;

  @BuiltValueField(wireName: r'titleRu')
  String? get titleRu;

  @BuiltValueField(wireName: r'titleEn')
  String get titleEn;

  @BuiltValueField(wireName: r'descriptionEn')
  String? get descriptionEn;

  @BuiltValueField(wireName: r'area')
  String get area;

  @BuiltValueField(wireName: r'bedrooms')
  int get bedrooms;

  @BuiltValueField(wireName: r'maxGuests')
  int get maxGuests;

  @BuiltValueField(wireName: r'areaSqm')
  num get areaSqm;

  @BuiltValueField(wireName: r'baseDailyPrice')
  num get baseDailyPrice;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  @BuiltValueField(wireName: r'coverUrl')
  String? get coverUrl;

  @BuiltValueField(wireName: r'price')
  RentalPrice? get price;

  RentalSearchProperty._();

  factory RentalSearchProperty([void updates(RentalSearchPropertyBuilder b)]) = _$RentalSearchProperty;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalSearchPropertyBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalSearchProperty> get serializer => _$RentalSearchPropertySerializer();
}

class _$RentalSearchPropertySerializer implements PrimitiveSerializer<RentalSearchProperty> {
  @override
  final Iterable<Type> types = const [RentalSearchProperty, _$RentalSearchProperty];

  @override
  final String wireName = r'RentalSearchProperty';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalSearchProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'slug';
    yield serializers.serialize(
      object.slug,
      specifiedType: const FullType(String),
    );
    yield r'titleRu';
    yield object.titleRu == null ? null : serializers.serialize(
      object.titleRu,
      specifiedType: const FullType.nullable(String),
    );
    yield r'titleEn';
    yield serializers.serialize(
      object.titleEn,
      specifiedType: const FullType(String),
    );
    yield r'descriptionEn';
    yield object.descriptionEn == null ? null : serializers.serialize(
      object.descriptionEn,
      specifiedType: const FullType.nullable(String),
    );
    yield r'area';
    yield serializers.serialize(
      object.area,
      specifiedType: const FullType(String),
    );
    yield r'bedrooms';
    yield serializers.serialize(
      object.bedrooms,
      specifiedType: const FullType(int),
    );
    yield r'maxGuests';
    yield serializers.serialize(
      object.maxGuests,
      specifiedType: const FullType(int),
    );
    yield r'areaSqm';
    yield serializers.serialize(
      object.areaSqm,
      specifiedType: const FullType(num),
    );
    yield r'baseDailyPrice';
    yield serializers.serialize(
      object.baseDailyPrice,
      specifiedType: const FullType(num),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
    yield r'coverUrl';
    yield object.coverUrl == null ? null : serializers.serialize(
      object.coverUrl,
      specifiedType: const FullType.nullable(String),
    );
    yield r'price';
    yield object.price == null ? null : serializers.serialize(
      object.price,
      specifiedType: const FullType.nullable(RentalPrice),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalSearchProperty object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalSearchPropertyBuilder result,
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
            specifiedType: const FullType(String),
          ) as String;
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
            specifiedType: const FullType(String),
          ) as String;
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
            specifiedType: const FullType(String),
          ) as String;
          result.area = valueDes;
          break;
        case r'bedrooms':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.bedrooms = valueDes;
          break;
        case r'maxGuests':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxGuests = valueDes;
          break;
        case r'areaSqm':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.areaSqm = valueDes;
          break;
        case r'baseDailyPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseDailyPrice = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        case r'coverUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.coverUrl = valueDes;
          break;
        case r'price':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalPrice),
          ) as RentalPrice?;
          if (valueDes == null) continue;
          result.price.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalSearchProperty deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalSearchPropertyBuilder();
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


