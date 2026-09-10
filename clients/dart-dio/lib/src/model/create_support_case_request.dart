//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_support_case_request.g.dart';

/// CreateSupportCaseRequest
///
/// Properties:
/// * [service] 
/// * [sourceEntityId] 
/// * [category] 
/// * [description] 
@BuiltValue()
abstract class CreateSupportCaseRequest implements Built<CreateSupportCaseRequest, CreateSupportCaseRequestBuilder> {
  @BuiltValueField(wireName: r'service')
  CreateSupportCaseRequestServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'category')
  CreateSupportCaseRequestCategoryEnum get category;
  // enum categoryEnum {  PROVIDER_LATE,  PROVIDER_NO_SHOW,  QUALITY_PROBLEM,  BOOKING_PROBLEM,  OTHER,  };

  @BuiltValueField(wireName: r'description')
  String? get description;

  CreateSupportCaseRequest._();

  factory CreateSupportCaseRequest([void updates(CreateSupportCaseRequestBuilder b)]) = _$CreateSupportCaseRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateSupportCaseRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateSupportCaseRequest> get serializer => _$CreateSupportCaseRequestSerializer();
}

class _$CreateSupportCaseRequestSerializer implements PrimitiveSerializer<CreateSupportCaseRequest> {
  @override
  final Iterable<Type> types = const [CreateSupportCaseRequest, _$CreateSupportCaseRequest];

  @override
  final String wireName = r'CreateSupportCaseRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateSupportCaseRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(CreateSupportCaseRequestServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(CreateSupportCaseRequestCategoryEnum),
    );
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateSupportCaseRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateSupportCaseRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateSupportCaseRequestServiceEnum),
          ) as CreateSupportCaseRequestServiceEnum;
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
            specifiedType: const FullType(CreateSupportCaseRequestCategoryEnum),
          ) as CreateSupportCaseRequestCategoryEnum;
          result.category = valueDes;
          break;
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.description = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateSupportCaseRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateSupportCaseRequestBuilder();
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


class CreateSupportCaseRequestServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CreateSupportCaseRequestServiceEnum CLEANING = _$createSupportCaseRequestServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CreateSupportCaseRequestServiceEnum RENTAL = _$createSupportCaseRequestServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CreateSupportCaseRequestServiceEnum TRANSFER = _$createSupportCaseRequestServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateSupportCaseRequestServiceEnum unknownDefaultOpenApi = _$createSupportCaseRequestServiceEnum_unknownDefaultOpenApi;

  static Serializer<CreateSupportCaseRequestServiceEnum> get serializer => _$createSupportCaseRequestServiceEnumSerializer;

  const CreateSupportCaseRequestServiceEnum._(String name): super(name);

  static BuiltSet<CreateSupportCaseRequestServiceEnum> get values => _$createSupportCaseRequestServiceEnumValues;
  static CreateSupportCaseRequestServiceEnum valueOf(String name) => _$createSupportCaseRequestServiceEnumValueOf(name);
}

class CreateSupportCaseRequestCategoryEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROVIDER_LATE')
  static const CreateSupportCaseRequestCategoryEnum PROVIDER_LATE = _$createSupportCaseRequestCategoryEnum_PROVIDER_LATE;
  @BuiltValueEnumConst(wireName: r'PROVIDER_NO_SHOW')
  static const CreateSupportCaseRequestCategoryEnum PROVIDER_NO_SHOW = _$createSupportCaseRequestCategoryEnum_PROVIDER_NO_SHOW;
  @BuiltValueEnumConst(wireName: r'QUALITY_PROBLEM')
  static const CreateSupportCaseRequestCategoryEnum QUALITY_PROBLEM = _$createSupportCaseRequestCategoryEnum_QUALITY_PROBLEM;
  @BuiltValueEnumConst(wireName: r'BOOKING_PROBLEM')
  static const CreateSupportCaseRequestCategoryEnum BOOKING_PROBLEM = _$createSupportCaseRequestCategoryEnum_BOOKING_PROBLEM;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const CreateSupportCaseRequestCategoryEnum OTHER = _$createSupportCaseRequestCategoryEnum_OTHER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateSupportCaseRequestCategoryEnum unknownDefaultOpenApi = _$createSupportCaseRequestCategoryEnum_unknownDefaultOpenApi;

  static Serializer<CreateSupportCaseRequestCategoryEnum> get serializer => _$createSupportCaseRequestCategoryEnumSerializer;

  const CreateSupportCaseRequestCategoryEnum._(String name): super(name);

  static BuiltSet<CreateSupportCaseRequestCategoryEnum> get values => _$createSupportCaseRequestCategoryEnumValues;
  static CreateSupportCaseRequestCategoryEnum valueOf(String name) => _$createSupportCaseRequestCategoryEnumValueOf(name);
}

