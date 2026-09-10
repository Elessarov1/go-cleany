//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_order_quote.g.dart';

/// CleaningOrderQuote
///
/// Properties:
/// * [basePrice] 
/// * [customerDiscount] 
/// * [finalCustomerPrice] 
/// * [customerDiscountType] 
/// * [currency] 
@BuiltValue()
abstract class CleaningOrderQuote implements Built<CleaningOrderQuote, CleaningOrderQuoteBuilder> {
  @BuiltValueField(wireName: r'basePrice')
  num get basePrice;

  @BuiltValueField(wireName: r'customerDiscount')
  num get customerDiscount;

  @BuiltValueField(wireName: r'finalCustomerPrice')
  num get finalCustomerPrice;

  @BuiltValueField(wireName: r'customerDiscountType')
  String get customerDiscountType;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  CleaningOrderQuote._();

  factory CleaningOrderQuote([void updates(CleaningOrderQuoteBuilder b)]) = _$CleaningOrderQuote;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningOrderQuoteBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningOrderQuote> get serializer => _$CleaningOrderQuoteSerializer();
}

class _$CleaningOrderQuoteSerializer implements PrimitiveSerializer<CleaningOrderQuote> {
  @override
  final Iterable<Type> types = const [CleaningOrderQuote, _$CleaningOrderQuote];

  @override
  final String wireName = r'CleaningOrderQuote';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningOrderQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'basePrice';
    yield serializers.serialize(
      object.basePrice,
      specifiedType: const FullType(num),
    );
    yield r'customerDiscount';
    yield serializers.serialize(
      object.customerDiscount,
      specifiedType: const FullType(num),
    );
    yield r'finalCustomerPrice';
    yield serializers.serialize(
      object.finalCustomerPrice,
      specifiedType: const FullType(num),
    );
    yield r'customerDiscountType';
    yield serializers.serialize(
      object.customerDiscountType,
      specifiedType: const FullType(String),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningOrderQuote object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningOrderQuoteBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'basePrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.basePrice = valueDes;
          break;
        case r'customerDiscount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.customerDiscount = valueDes;
          break;
        case r'finalCustomerPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.finalCustomerPrice = valueDes;
          break;
        case r'customerDiscountType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerDiscountType = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningOrderQuote deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningOrderQuoteBuilder();
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


