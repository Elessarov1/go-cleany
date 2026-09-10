//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'support_case.g.dart';

/// SupportCase
///
/// Properties:
/// * [id] 
/// * [service] 
/// * [sourceEntityId] 
/// * [category] 
/// * [status] 
/// * [description] 
/// * [createdAt] 
/// * [resolvedAt] 
/// * [resolutionComment] 
@BuiltValue()
abstract class SupportCase implements Built<SupportCase, SupportCaseBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'service')
  SupportCaseServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'category')
  SupportCaseCategoryEnum get category;
  // enum categoryEnum {  PROVIDER_LATE,  PROVIDER_NO_SHOW,  QUALITY_PROBLEM,  BOOKING_PROBLEM,  OTHER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'status')
  SupportCaseStatusEnum get status;
  // enum statusEnum {  OPEN,  RESOLVED,  UNKNOWN,  };

  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'resolvedAt')
  DateTime? get resolvedAt;

  @BuiltValueField(wireName: r'resolutionComment')
  String? get resolutionComment;

  SupportCase._();

  factory SupportCase([void updates(SupportCaseBuilder b)]) = _$SupportCase;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SupportCaseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SupportCase> get serializer => _$SupportCaseSerializer();
}

class _$SupportCaseSerializer implements PrimitiveSerializer<SupportCase> {
  @override
  final Iterable<Type> types = const [SupportCase, _$SupportCase];

  @override
  final String wireName = r'SupportCase';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SupportCase object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(SupportCaseServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(SupportCaseCategoryEnum),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(SupportCaseStatusEnum),
    );
    yield r'description';
    yield object.description == null ? null : serializers.serialize(
      object.description,
      specifiedType: const FullType.nullable(String),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'resolvedAt';
    yield object.resolvedAt == null ? null : serializers.serialize(
      object.resolvedAt,
      specifiedType: const FullType.nullable(DateTime),
    );
    yield r'resolutionComment';
    yield object.resolutionComment == null ? null : serializers.serialize(
      object.resolutionComment,
      specifiedType: const FullType.nullable(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SupportCase object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SupportCaseBuilder result,
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
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SupportCaseServiceEnum),
          ) as SupportCaseServiceEnum;
          result.service = valueDes;
          break;
        case r'sourceEntityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceEntityId = valueDes;
          break;
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SupportCaseCategoryEnum),
          ) as SupportCaseCategoryEnum;
          result.category = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SupportCaseStatusEnum),
          ) as SupportCaseStatusEnum;
          result.status = valueDes;
          break;
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.description = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'resolvedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.resolvedAt = valueDes;
          break;
        case r'resolutionComment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.resolutionComment = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SupportCase deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SupportCaseBuilder();
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


class SupportCaseServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const SupportCaseServiceEnum CLEANING = _$supportCaseServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const SupportCaseServiceEnum RENTAL = _$supportCaseServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const SupportCaseServiceEnum TRANSFER = _$supportCaseServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const SupportCaseServiceEnum UNKNOWN = _$supportCaseServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const SupportCaseServiceEnum unknownDefaultOpenApi = _$supportCaseServiceEnum_unknownDefaultOpenApi;

  static Serializer<SupportCaseServiceEnum> get serializer => _$supportCaseServiceEnumSerializer;

  const SupportCaseServiceEnum._(String name): super(name);

  static BuiltSet<SupportCaseServiceEnum> get values => _$supportCaseServiceEnumValues;
  static SupportCaseServiceEnum valueOf(String name) => _$supportCaseServiceEnumValueOf(name);
}

class SupportCaseCategoryEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROVIDER_LATE')
  static const SupportCaseCategoryEnum PROVIDER_LATE = _$supportCaseCategoryEnum_PROVIDER_LATE;
  @BuiltValueEnumConst(wireName: r'PROVIDER_NO_SHOW')
  static const SupportCaseCategoryEnum PROVIDER_NO_SHOW = _$supportCaseCategoryEnum_PROVIDER_NO_SHOW;
  @BuiltValueEnumConst(wireName: r'QUALITY_PROBLEM')
  static const SupportCaseCategoryEnum QUALITY_PROBLEM = _$supportCaseCategoryEnum_QUALITY_PROBLEM;
  @BuiltValueEnumConst(wireName: r'BOOKING_PROBLEM')
  static const SupportCaseCategoryEnum BOOKING_PROBLEM = _$supportCaseCategoryEnum_BOOKING_PROBLEM;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const SupportCaseCategoryEnum OTHER = _$supportCaseCategoryEnum_OTHER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const SupportCaseCategoryEnum UNKNOWN = _$supportCaseCategoryEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const SupportCaseCategoryEnum unknownDefaultOpenApi = _$supportCaseCategoryEnum_unknownDefaultOpenApi;

  static Serializer<SupportCaseCategoryEnum> get serializer => _$supportCaseCategoryEnumSerializer;

  const SupportCaseCategoryEnum._(String name): super(name);

  static BuiltSet<SupportCaseCategoryEnum> get values => _$supportCaseCategoryEnumValues;
  static SupportCaseCategoryEnum valueOf(String name) => _$supportCaseCategoryEnumValueOf(name);
}

class SupportCaseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN')
  static const SupportCaseStatusEnum OPEN = _$supportCaseStatusEnum_OPEN;
  @BuiltValueEnumConst(wireName: r'RESOLVED')
  static const SupportCaseStatusEnum RESOLVED = _$supportCaseStatusEnum_RESOLVED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const SupportCaseStatusEnum UNKNOWN = _$supportCaseStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const SupportCaseStatusEnum unknownDefaultOpenApi = _$supportCaseStatusEnum_unknownDefaultOpenApi;

  static Serializer<SupportCaseStatusEnum> get serializer => _$supportCaseStatusEnumSerializer;

  const SupportCaseStatusEnum._(String name): super(name);

  static BuiltSet<SupportCaseStatusEnum> get values => _$supportCaseStatusEnumValues;
  static SupportCaseStatusEnum valueOf(String name) => _$supportCaseStatusEnumValueOf(name);
}

