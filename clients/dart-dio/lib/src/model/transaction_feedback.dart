//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transaction_feedback.g.dart';

/// TransactionFeedback
///
/// Properties:
/// * [id] 
/// * [outcome] 
/// * [category] 
/// * [comment] 
/// * [supportCaseId] 
/// * [createdAt] 
@BuiltValue()
abstract class TransactionFeedback implements Built<TransactionFeedback, TransactionFeedbackBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'outcome')
  TransactionFeedbackOutcomeEnum get outcome;
  // enum outcomeEnum {  GOOD,  PROBLEM,  UNKNOWN,  };

  @BuiltValueField(wireName: r'category')
  TransactionFeedbackCategoryEnum? get category;
  // enum categoryEnum {  PROVIDER_LATE,  PROVIDER_NO_SHOW,  QUALITY_PROBLEM,  BOOKING_PROBLEM,  OTHER,  UNKNOWN,  ,  };

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  @BuiltValueField(wireName: r'supportCaseId')
  int? get supportCaseId;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  TransactionFeedback._();

  factory TransactionFeedback([void updates(TransactionFeedbackBuilder b)]) = _$TransactionFeedback;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransactionFeedbackBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransactionFeedback> get serializer => _$TransactionFeedbackSerializer();
}

class _$TransactionFeedbackSerializer implements PrimitiveSerializer<TransactionFeedback> {
  @override
  final Iterable<Type> types = const [TransactionFeedback, _$TransactionFeedback];

  @override
  final String wireName = r'TransactionFeedback';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransactionFeedback object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'outcome';
    yield serializers.serialize(
      object.outcome,
      specifiedType: const FullType(TransactionFeedbackOutcomeEnum),
    );
    yield r'category';
    yield object.category == null ? null : serializers.serialize(
      object.category,
      specifiedType: const FullType.nullable(TransactionFeedbackCategoryEnum),
    );
    yield r'comment';
    yield object.comment == null ? null : serializers.serialize(
      object.comment,
      specifiedType: const FullType.nullable(String),
    );
    yield r'supportCaseId';
    yield object.supportCaseId == null ? null : serializers.serialize(
      object.supportCaseId,
      specifiedType: const FullType.nullable(int),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransactionFeedback object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransactionFeedbackBuilder result,
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
        case r'outcome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransactionFeedbackOutcomeEnum),
          ) as TransactionFeedbackOutcomeEnum;
          result.outcome = valueDes;
          break;
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(TransactionFeedbackCategoryEnum),
          ) as TransactionFeedbackCategoryEnum?;
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
        case r'supportCaseId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.supportCaseId = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransactionFeedback deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransactionFeedbackBuilder();
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


class TransactionFeedbackOutcomeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'GOOD')
  static const TransactionFeedbackOutcomeEnum GOOD = _$transactionFeedbackOutcomeEnum_GOOD;
  @BuiltValueEnumConst(wireName: r'PROBLEM')
  static const TransactionFeedbackOutcomeEnum PROBLEM = _$transactionFeedbackOutcomeEnum_PROBLEM;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransactionFeedbackOutcomeEnum UNKNOWN = _$transactionFeedbackOutcomeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransactionFeedbackOutcomeEnum unknownDefaultOpenApi = _$transactionFeedbackOutcomeEnum_unknownDefaultOpenApi;

  static Serializer<TransactionFeedbackOutcomeEnum> get serializer => _$transactionFeedbackOutcomeEnumSerializer;

  const TransactionFeedbackOutcomeEnum._(String name): super(name);

  static BuiltSet<TransactionFeedbackOutcomeEnum> get values => _$transactionFeedbackOutcomeEnumValues;
  static TransactionFeedbackOutcomeEnum valueOf(String name) => _$transactionFeedbackOutcomeEnumValueOf(name);
}

class TransactionFeedbackCategoryEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROVIDER_LATE')
  static const TransactionFeedbackCategoryEnum PROVIDER_LATE = _$transactionFeedbackCategoryEnum_PROVIDER_LATE;
  @BuiltValueEnumConst(wireName: r'PROVIDER_NO_SHOW')
  static const TransactionFeedbackCategoryEnum PROVIDER_NO_SHOW = _$transactionFeedbackCategoryEnum_PROVIDER_NO_SHOW;
  @BuiltValueEnumConst(wireName: r'QUALITY_PROBLEM')
  static const TransactionFeedbackCategoryEnum QUALITY_PROBLEM = _$transactionFeedbackCategoryEnum_QUALITY_PROBLEM;
  @BuiltValueEnumConst(wireName: r'BOOKING_PROBLEM')
  static const TransactionFeedbackCategoryEnum BOOKING_PROBLEM = _$transactionFeedbackCategoryEnum_BOOKING_PROBLEM;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const TransactionFeedbackCategoryEnum OTHER = _$transactionFeedbackCategoryEnum_OTHER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransactionFeedbackCategoryEnum UNKNOWN = _$transactionFeedbackCategoryEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransactionFeedbackCategoryEnum unknownDefaultOpenApi = _$transactionFeedbackCategoryEnum_unknownDefaultOpenApi;

  static Serializer<TransactionFeedbackCategoryEnum> get serializer => _$transactionFeedbackCategoryEnumSerializer;

  const TransactionFeedbackCategoryEnum._(String name): super(name);

  static BuiltSet<TransactionFeedbackCategoryEnum> get values => _$transactionFeedbackCategoryEnumValues;
  static TransactionFeedbackCategoryEnum valueOf(String name) => _$transactionFeedbackCategoryEnumValueOf(name);
}

