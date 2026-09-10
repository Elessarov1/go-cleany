//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_quote.g.dart';

/// TransferQuote
///
/// Properties:
/// * [baseAmount] 
/// * [discountAmount] 
/// * [payableAmount] 
/// * [currency] 
/// * [appliedBenefit] 
@BuiltValue()
abstract class TransferQuote implements Built<TransferQuote, TransferQuoteBuilder> {
  @BuiltValueField(wireName: r'baseAmount')
  num get baseAmount;

  @BuiltValueField(wireName: r'discountAmount')
  num get discountAmount;

  @BuiltValueField(wireName: r'payableAmount')
  num get payableAmount;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  @BuiltValueField(wireName: r'appliedBenefit')
  TransferQuoteAppliedBenefitEnum? get appliedBenefit;
  // enum appliedBenefitEnum {  RENTAL_FIRST_TRANSFER,  UNKNOWN,  ,  };

  TransferQuote._();

  factory TransferQuote([void updates(TransferQuoteBuilder b)]) = _$TransferQuote;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferQuoteBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferQuote> get serializer => _$TransferQuoteSerializer();
}

class _$TransferQuoteSerializer implements PrimitiveSerializer<TransferQuote> {
  @override
  final Iterable<Type> types = const [TransferQuote, _$TransferQuote];

  @override
  final String wireName = r'TransferQuote';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'baseAmount';
    yield serializers.serialize(
      object.baseAmount,
      specifiedType: const FullType(num),
    );
    yield r'discountAmount';
    yield serializers.serialize(
      object.discountAmount,
      specifiedType: const FullType(num),
    );
    yield r'payableAmount';
    yield serializers.serialize(
      object.payableAmount,
      specifiedType: const FullType(num),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
    yield r'appliedBenefit';
    yield object.appliedBenefit == null ? null : serializers.serialize(
      object.appliedBenefit,
      specifiedType: const FullType.nullable(TransferQuoteAppliedBenefitEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferQuoteBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'baseAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseAmount = valueDes;
          break;
        case r'discountAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountAmount = valueDes;
          break;
        case r'payableAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.payableAmount = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        case r'appliedBenefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(TransferQuoteAppliedBenefitEnum),
          ) as TransferQuoteAppliedBenefitEnum?;
          if (valueDes == null) continue;
          result.appliedBenefit = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferQuote deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferQuoteBuilder();
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


class TransferQuoteAppliedBenefitEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_FIRST_TRANSFER')
  static const TransferQuoteAppliedBenefitEnum RENTAL_FIRST_TRANSFER = _$transferQuoteAppliedBenefitEnum_RENTAL_FIRST_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferQuoteAppliedBenefitEnum UNKNOWN = _$transferQuoteAppliedBenefitEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferQuoteAppliedBenefitEnum unknownDefaultOpenApi = _$transferQuoteAppliedBenefitEnum_unknownDefaultOpenApi;

  static Serializer<TransferQuoteAppliedBenefitEnum> get serializer => _$transferQuoteAppliedBenefitEnumSerializer;

  const TransferQuoteAppliedBenefitEnum._(String name): super(name);

  static BuiltSet<TransferQuoteAppliedBenefitEnum> get values => _$transferQuoteAppliedBenefitEnumValues;
  static TransferQuoteAppliedBenefitEnum valueOf(String name) => _$transferQuoteAppliedBenefitEnumValueOf(name);
}

