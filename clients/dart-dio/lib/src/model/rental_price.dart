//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_price.g.dart';

/// RentalPrice
///
/// Properties:
/// * [baseDailyPrice] 
/// * [baseMonthlyPrice] 
/// * [monthlyPrice] 
/// * [baseAmount] 
/// * [discountRate] 
/// * [discountAmount] 
/// * [longTermDiscountApplied] 
/// * [totalPrice] 
/// * [currency] 
/// * [rentalMonths] 
/// * [durationDays] 
@BuiltValue()
abstract class RentalPrice implements Built<RentalPrice, RentalPriceBuilder> {
  @BuiltValueField(wireName: r'baseDailyPrice')
  num get baseDailyPrice;

  @BuiltValueField(wireName: r'baseMonthlyPrice')
  num? get baseMonthlyPrice;

  @BuiltValueField(wireName: r'monthlyPrice')
  num? get monthlyPrice;

  @BuiltValueField(wireName: r'baseAmount')
  num get baseAmount;

  @BuiltValueField(wireName: r'discountRate')
  num get discountRate;

  @BuiltValueField(wireName: r'discountAmount')
  num get discountAmount;

  @BuiltValueField(wireName: r'longTermDiscountApplied')
  bool get longTermDiscountApplied;

  @BuiltValueField(wireName: r'totalPrice')
  num get totalPrice;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  @BuiltValueField(wireName: r'rentalMonths')
  int? get rentalMonths;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  RentalPrice._();

  factory RentalPrice([void updates(RentalPriceBuilder b)]) = _$RentalPrice;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalPriceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalPrice> get serializer => _$RentalPriceSerializer();
}

class _$RentalPriceSerializer implements PrimitiveSerializer<RentalPrice> {
  @override
  final Iterable<Type> types = const [RentalPrice, _$RentalPrice];

  @override
  final String wireName = r'RentalPrice';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'baseDailyPrice';
    yield serializers.serialize(
      object.baseDailyPrice,
      specifiedType: const FullType(num),
    );
    yield r'baseMonthlyPrice';
    yield object.baseMonthlyPrice == null ? null : serializers.serialize(
      object.baseMonthlyPrice,
      specifiedType: const FullType.nullable(num),
    );
    yield r'monthlyPrice';
    yield object.monthlyPrice == null ? null : serializers.serialize(
      object.monthlyPrice,
      specifiedType: const FullType.nullable(num),
    );
    yield r'baseAmount';
    yield serializers.serialize(
      object.baseAmount,
      specifiedType: const FullType(num),
    );
    yield r'discountRate';
    yield serializers.serialize(
      object.discountRate,
      specifiedType: const FullType(num),
    );
    yield r'discountAmount';
    yield serializers.serialize(
      object.discountAmount,
      specifiedType: const FullType(num),
    );
    yield r'longTermDiscountApplied';
    yield serializers.serialize(
      object.longTermDiscountApplied,
      specifiedType: const FullType(bool),
    );
    yield r'totalPrice';
    yield serializers.serialize(
      object.totalPrice,
      specifiedType: const FullType(num),
    );
    yield r'currency';
    yield serializers.serialize(
      object.currency,
      specifiedType: const FullType(String),
    );
    yield r'rentalMonths';
    yield object.rentalMonths == null ? null : serializers.serialize(
      object.rentalMonths,
      specifiedType: const FullType.nullable(int),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalPriceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'baseDailyPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseDailyPrice = valueDes;
          break;
        case r'baseMonthlyPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.baseMonthlyPrice = valueDes;
          break;
        case r'monthlyPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(num),
          ) as num?;
          if (valueDes == null) continue;
          result.monthlyPrice = valueDes;
          break;
        case r'baseAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseAmount = valueDes;
          break;
        case r'discountRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountRate = valueDes;
          break;
        case r'discountAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.discountAmount = valueDes;
          break;
        case r'longTermDiscountApplied':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.longTermDiscountApplied = valueDes;
          break;
        case r'totalPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.totalPrice = valueDes;
          break;
        case r'currency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.currency = valueDes;
          break;
        case r'rentalMonths':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.rentalMonths = valueDes;
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalPrice deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalPriceBuilder();
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


