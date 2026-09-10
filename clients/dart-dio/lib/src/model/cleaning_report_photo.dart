//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_report_photo.g.dart';

/// CleaningReportPhoto
///
/// Properties:
/// * [id] 
/// * [url] 
@BuiltValue()
abstract class CleaningReportPhoto implements Built<CleaningReportPhoto, CleaningReportPhotoBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'url')
  String get url;

  CleaningReportPhoto._();

  factory CleaningReportPhoto([void updates(CleaningReportPhotoBuilder b)]) = _$CleaningReportPhoto;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningReportPhotoBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningReportPhoto> get serializer => _$CleaningReportPhotoSerializer();
}

class _$CleaningReportPhotoSerializer implements PrimitiveSerializer<CleaningReportPhoto> {
  @override
  final Iterable<Type> types = const [CleaningReportPhoto, _$CleaningReportPhoto];

  @override
  final String wireName = r'CleaningReportPhoto';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningReportPhoto object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'url';
    yield serializers.serialize(
      object.url,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningReportPhoto object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningReportPhotoBuilder result,
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
        case r'url':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.url = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningReportPhoto deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningReportPhotoBuilder();
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


