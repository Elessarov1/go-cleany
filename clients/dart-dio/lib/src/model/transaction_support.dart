//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/transaction_feedback.dart';
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/support_case.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transaction_support.g.dart';

/// TransactionSupport
///
/// Properties:
/// * [service] 
/// * [sourceEntityId] 
/// * [feedbackEligible] 
/// * [feedback] 
/// * [latestCase] 
@BuiltValue()
abstract class TransactionSupport implements Built<TransactionSupport, TransactionSupportBuilder> {
  @BuiltValueField(wireName: r'service')
  TransactionSupportServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'sourceEntityId')
  int get sourceEntityId;

  @BuiltValueField(wireName: r'feedbackEligible')
  bool get feedbackEligible;

  @BuiltValueField(wireName: r'feedback')
  TransactionFeedback? get feedback;

  @BuiltValueField(wireName: r'latestCase')
  SupportCase? get latestCase;

  TransactionSupport._();

  factory TransactionSupport([void updates(TransactionSupportBuilder b)]) = _$TransactionSupport;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransactionSupportBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransactionSupport> get serializer => _$TransactionSupportSerializer();
}

class _$TransactionSupportSerializer implements PrimitiveSerializer<TransactionSupport> {
  @override
  final Iterable<Type> types = const [TransactionSupport, _$TransactionSupport];

  @override
  final String wireName = r'TransactionSupport';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransactionSupport object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(TransactionSupportServiceEnum),
    );
    yield r'sourceEntityId';
    yield serializers.serialize(
      object.sourceEntityId,
      specifiedType: const FullType(int),
    );
    yield r'feedbackEligible';
    yield serializers.serialize(
      object.feedbackEligible,
      specifiedType: const FullType(bool),
    );
    yield r'feedback';
    yield object.feedback == null ? null : serializers.serialize(
      object.feedback,
      specifiedType: const FullType.nullable(TransactionFeedback),
    );
    yield r'latestCase';
    yield object.latestCase == null ? null : serializers.serialize(
      object.latestCase,
      specifiedType: const FullType.nullable(SupportCase),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransactionSupport object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransactionSupportBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransactionSupportServiceEnum),
          ) as TransactionSupportServiceEnum;
          result.service = valueDes;
          break;
        case r'sourceEntityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceEntityId = valueDes;
          break;
        case r'feedbackEligible':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.feedbackEligible = valueDes;
          break;
        case r'feedback':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(TransactionFeedback),
          ) as TransactionFeedback?;
          if (valueDes == null) continue;
          result.feedback.replace(valueDes);
          break;
        case r'latestCase':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(SupportCase),
          ) as SupportCase?;
          if (valueDes == null) continue;
          result.latestCase.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransactionSupport deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransactionSupportBuilder();
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


class TransactionSupportServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const TransactionSupportServiceEnum CLEANING = _$transactionSupportServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const TransactionSupportServiceEnum RENTAL = _$transactionSupportServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const TransactionSupportServiceEnum TRANSFER = _$transactionSupportServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransactionSupportServiceEnum UNKNOWN = _$transactionSupportServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransactionSupportServiceEnum unknownDefaultOpenApi = _$transactionSupportServiceEnum_unknownDefaultOpenApi;

  static Serializer<TransactionSupportServiceEnum> get serializer => _$transactionSupportServiceEnumSerializer;

  const TransactionSupportServiceEnum._(String name): super(name);

  static BuiltSet<TransactionSupportServiceEnum> get values => _$transactionSupportServiceEnumValues;
  static TransactionSupportServiceEnum valueOf(String name) => _$transactionSupportServiceEnumValueOf(name);
}

