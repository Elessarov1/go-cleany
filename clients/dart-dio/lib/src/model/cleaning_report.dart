//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/cleaning_report_photo.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_report.g.dart';

/// CleaningReport
///
/// Properties:
/// * [status] 
/// * [expiresAt] 
/// * [retentionDays] 
/// * [cleanerComment] 
/// * [photos] 
@BuiltValue()
abstract class CleaningReport implements Built<CleaningReport, CleaningReportBuilder> {
  @BuiltValueField(wireName: r'status')
  CleaningReportStatusEnum get status;
  // enum statusEnum {  NOT_READY,  AVAILABLE,  EXPIRED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'expiresAt')
  DateTime? get expiresAt;

  @BuiltValueField(wireName: r'retentionDays')
  int get retentionDays;

  @BuiltValueField(wireName: r'cleanerComment')
  String? get cleanerComment;

  @BuiltValueField(wireName: r'photos')
  BuiltList<CleaningReportPhoto> get photos;

  CleaningReport._();

  factory CleaningReport([void updates(CleaningReportBuilder b)]) = _$CleaningReport;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningReportBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningReport> get serializer => _$CleaningReportSerializer();
}

class _$CleaningReportSerializer implements PrimitiveSerializer<CleaningReport> {
  @override
  final Iterable<Type> types = const [CleaningReport, _$CleaningReport];

  @override
  final String wireName = r'CleaningReport';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningReport object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(CleaningReportStatusEnum),
    );
    if (object.expiresAt != null) {
      yield r'expiresAt';
      yield serializers.serialize(
        object.expiresAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'retentionDays';
    yield serializers.serialize(
      object.retentionDays,
      specifiedType: const FullType(int),
    );
    if (object.cleanerComment != null) {
      yield r'cleanerComment';
      yield serializers.serialize(
        object.cleanerComment,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'photos';
    yield serializers.serialize(
      object.photos,
      specifiedType: const FullType(BuiltList, [FullType(CleaningReportPhoto)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningReport object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningReportBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningReportStatusEnum),
          ) as CleaningReportStatusEnum;
          result.status = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.expiresAt = valueDes;
          break;
        case r'retentionDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.retentionDays = valueDes;
          break;
        case r'cleanerComment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.cleanerComment = valueDes;
          break;
        case r'photos':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CleaningReportPhoto)]),
          ) as BuiltList<CleaningReportPhoto>;
          result.photos.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningReport deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningReportBuilder();
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


class CleaningReportStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NOT_READY')
  static const CleaningReportStatusEnum NOT_READY = _$cleaningReportStatusEnum_NOT_READY;
  @BuiltValueEnumConst(wireName: r'AVAILABLE')
  static const CleaningReportStatusEnum AVAILABLE = _$cleaningReportStatusEnum_AVAILABLE;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const CleaningReportStatusEnum EXPIRED = _$cleaningReportStatusEnum_EXPIRED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningReportStatusEnum UNKNOWN = _$cleaningReportStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningReportStatusEnum unknownDefaultOpenApi = _$cleaningReportStatusEnum_unknownDefaultOpenApi;

  static Serializer<CleaningReportStatusEnum> get serializer => _$cleaningReportStatusEnumSerializer;

  const CleaningReportStatusEnum._(String name): super(name);

  static BuiltSet<CleaningReportStatusEnum> get values => _$cleaningReportStatusEnumValues;
  static CleaningReportStatusEnum valueOf(String name) => _$cleaningReportStatusEnumValueOf(name);
}

