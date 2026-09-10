//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_property_media.g.dart';

/// RentalPropertyMedia
///
/// Properties:
/// * [id] 
/// * [mediaAssetId] 
/// * [sortOrder] 
/// * [cover] 
/// * [url] 
/// * [cardUrl] 
/// * [thumbnailUrl] 
@BuiltValue()
abstract class RentalPropertyMedia implements Built<RentalPropertyMedia, RentalPropertyMediaBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'mediaAssetId')
  int get mediaAssetId;

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  @BuiltValueField(wireName: r'cover')
  bool get cover;

  @BuiltValueField(wireName: r'url')
  String get url;

  @BuiltValueField(wireName: r'cardUrl')
  String get cardUrl;

  @BuiltValueField(wireName: r'thumbnailUrl')
  String get thumbnailUrl;

  RentalPropertyMedia._();

  factory RentalPropertyMedia([void updates(RentalPropertyMediaBuilder b)]) = _$RentalPropertyMedia;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalPropertyMediaBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalPropertyMedia> get serializer => _$RentalPropertyMediaSerializer();
}

class _$RentalPropertyMediaSerializer implements PrimitiveSerializer<RentalPropertyMedia> {
  @override
  final Iterable<Type> types = const [RentalPropertyMedia, _$RentalPropertyMedia];

  @override
  final String wireName = r'RentalPropertyMedia';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalPropertyMedia object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'mediaAssetId';
    yield serializers.serialize(
      object.mediaAssetId,
      specifiedType: const FullType(int),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
    yield r'cover';
    yield serializers.serialize(
      object.cover,
      specifiedType: const FullType(bool),
    );
    yield r'url';
    yield serializers.serialize(
      object.url,
      specifiedType: const FullType(String),
    );
    yield r'cardUrl';
    yield serializers.serialize(
      object.cardUrl,
      specifiedType: const FullType(String),
    );
    yield r'thumbnailUrl';
    yield serializers.serialize(
      object.thumbnailUrl,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalPropertyMedia object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalPropertyMediaBuilder result,
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
        case r'mediaAssetId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.mediaAssetId = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        case r'cover':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.cover = valueDes;
          break;
        case r'url':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.url = valueDes;
          break;
        case r'cardUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.cardUrl = valueDes;
          break;
        case r'thumbnailUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.thumbnailUrl = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalPropertyMedia deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalPropertyMediaBuilder();
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


