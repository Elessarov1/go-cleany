//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_transaction_feedback_request.g.dart';

/// CreateTransactionFeedbackRequest
///
/// Properties:
/// * [service] 
/// * [sourceEntityId] 
/// * [outcome] 
/// * [category] 
/// * [comment] 
@BuiltValue()
abstract class CreateTransactionFeedbackRequest implements Built<CreateTransactionFeedbackRequest, CreateTransactionFeedbackRequestBuilder> {
  @BuiltValueField(wireName: r'service')
  CreateTransactionFeedbackRequestServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'outcome')
  CreateTransactionFeedbackRequestOutcomeEnum get outcome;
  // enum outcomeEnum {  GOOD,  PROBLEM,  };

  @BuiltValueField(wireName: r'category')
  CreateTransactionFeedbackRequestCategoryEnum? get category;
  // enum categoryEnum {  PROVIDER_LATE,  PROVIDER_NO_SHOW,  QUALITY_PROBLEM,  BOOKING_PROBLEM,  OTHER,  ,  };

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  CreateTransactionFeedbackRequest._();

  factory CreateTransactionFeedbackRequest([void updates(CreateTransactionFeedbackRequestBuilder b)]) = _$CreateTransactionFeedbackRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateTransactionFeedbackRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateTransactionFeedbackRequest> get serializer => _$CreateTransactionFeedbackRequestSerializer();
}

class _$CreateTransactionFeedbackRequestSerializer implements PrimitiveSerializer<CreateTransactionFeedbackRequest> {
  @override
  final Iterable<Type> types = const [CreateTransactionFeedbackRequest, _$CreateTransactionFeedbackRequest];

  @override
  final String wireName = r'CreateTransactionFeedbackRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateTransactionFeedbackRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(CreateTransactionFeedbackRequestServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'outcome';
    yield serializers.serialize(
      object.outcome,
      specifiedType: const FullType(CreateTransactionFeedbackRequestOutcomeEnum),
    );
    if (object.category != null) {
      yield r'category';
      yield serializers.serialize(
        object.category,
        specifiedType: const FullType.nullable(CreateTransactionFeedbackRequestCategoryEnum),
      );
    }
    if (object.comment != null) {
      yield r'comment';
      yield serializers.serialize(
        object.comment,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateTransactionFeedbackRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateTransactionFeedbackRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateTransactionFeedbackRequestServiceEnum),
          ) as CreateTransactionFeedbackRequestServiceEnum;
          result.service = valueDes;
          break;
        case r'sourceEntityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceEntityId = valueDes;
          break;
        case r'outcome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateTransactionFeedbackRequestOutcomeEnum),
          ) as CreateTransactionFeedbackRequestOutcomeEnum;
          result.outcome = valueDes;
          break;
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CreateTransactionFeedbackRequestCategoryEnum),
          ) as CreateTransactionFeedbackRequestCategoryEnum?;
          if (valueDes == null) continue;
          result.category = valueDes;
          break;
        case r'comment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.comment = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateTransactionFeedbackRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateTransactionFeedbackRequestBuilder();
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


class CreateTransactionFeedbackRequestServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const CreateTransactionFeedbackRequestServiceEnum CLEANING = _$createTransactionFeedbackRequestServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const CreateTransactionFeedbackRequestServiceEnum RENTAL = _$createTransactionFeedbackRequestServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const CreateTransactionFeedbackRequestServiceEnum TRANSFER = _$createTransactionFeedbackRequestServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateTransactionFeedbackRequestServiceEnum unknownDefaultOpenApi = _$createTransactionFeedbackRequestServiceEnum_unknownDefaultOpenApi;

  static Serializer<CreateTransactionFeedbackRequestServiceEnum> get serializer => _$createTransactionFeedbackRequestServiceEnumSerializer;

  const CreateTransactionFeedbackRequestServiceEnum._(String name): super(name);

  static BuiltSet<CreateTransactionFeedbackRequestServiceEnum> get values => _$createTransactionFeedbackRequestServiceEnumValues;
  static CreateTransactionFeedbackRequestServiceEnum valueOf(String name) => _$createTransactionFeedbackRequestServiceEnumValueOf(name);
}

class CreateTransactionFeedbackRequestOutcomeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'GOOD')
  static const CreateTransactionFeedbackRequestOutcomeEnum GOOD = _$createTransactionFeedbackRequestOutcomeEnum_GOOD;
  @BuiltValueEnumConst(wireName: r'PROBLEM')
  static const CreateTransactionFeedbackRequestOutcomeEnum PROBLEM = _$createTransactionFeedbackRequestOutcomeEnum_PROBLEM;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateTransactionFeedbackRequestOutcomeEnum unknownDefaultOpenApi = _$createTransactionFeedbackRequestOutcomeEnum_unknownDefaultOpenApi;

  static Serializer<CreateTransactionFeedbackRequestOutcomeEnum> get serializer => _$createTransactionFeedbackRequestOutcomeEnumSerializer;

  const CreateTransactionFeedbackRequestOutcomeEnum._(String name): super(name);

  static BuiltSet<CreateTransactionFeedbackRequestOutcomeEnum> get values => _$createTransactionFeedbackRequestOutcomeEnumValues;
  static CreateTransactionFeedbackRequestOutcomeEnum valueOf(String name) => _$createTransactionFeedbackRequestOutcomeEnumValueOf(name);
}

class CreateTransactionFeedbackRequestCategoryEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROVIDER_LATE')
  static const CreateTransactionFeedbackRequestCategoryEnum PROVIDER_LATE = _$createTransactionFeedbackRequestCategoryEnum_PROVIDER_LATE;
  @BuiltValueEnumConst(wireName: r'PROVIDER_NO_SHOW')
  static const CreateTransactionFeedbackRequestCategoryEnum PROVIDER_NO_SHOW = _$createTransactionFeedbackRequestCategoryEnum_PROVIDER_NO_SHOW;
  @BuiltValueEnumConst(wireName: r'QUALITY_PROBLEM')
  static const CreateTransactionFeedbackRequestCategoryEnum QUALITY_PROBLEM = _$createTransactionFeedbackRequestCategoryEnum_QUALITY_PROBLEM;
  @BuiltValueEnumConst(wireName: r'BOOKING_PROBLEM')
  static const CreateTransactionFeedbackRequestCategoryEnum BOOKING_PROBLEM = _$createTransactionFeedbackRequestCategoryEnum_BOOKING_PROBLEM;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const CreateTransactionFeedbackRequestCategoryEnum OTHER = _$createTransactionFeedbackRequestCategoryEnum_OTHER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CreateTransactionFeedbackRequestCategoryEnum unknownDefaultOpenApi = _$createTransactionFeedbackRequestCategoryEnum_unknownDefaultOpenApi;

  static Serializer<CreateTransactionFeedbackRequestCategoryEnum> get serializer => _$createTransactionFeedbackRequestCategoryEnumSerializer;

  const CreateTransactionFeedbackRequestCategoryEnum._(String name): super(name);

  static BuiltSet<CreateTransactionFeedbackRequestCategoryEnum> get values => _$createTransactionFeedbackRequestCategoryEnumValues;
  static CreateTransactionFeedbackRequestCategoryEnum valueOf(String name) => _$createTransactionFeedbackRequestCategoryEnumValueOf(name);
}

