//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_price.g.dart';

/// TransferPrice
///
/// Properties:
/// * [airportId] 
/// * [vehicleTypeId] 
/// * [direction] 
/// * [amount] 
/// * [currency] 
@BuiltValue()
abstract class TransferPrice implements Built<TransferPrice, TransferPriceBuilder> {
  @BuiltValueField(wireName: r'airportId')
  int get airportId;

  @BuiltValueField(wireName: r'vehicleTypeId')
  int get vehicleTypeId;

  @BuiltValueField(wireName: r'direction')
  TransferPriceDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'amount')
  num get amount;

  @BuiltValueField(wireName: r'currency')
  String get currency;

  TransferPrice._();

  factory TransferPrice([void updates(TransferPriceBuilder b)]) = _$TransferPrice;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferPriceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferPrice> get serializer => _$TransferPriceSerializer();
}

class _$TransferPriceSerializer implements PrimitiveSerializer<TransferPrice> {
  @override
  final Iterable<Type> types = const [TransferPrice, _$TransferPrice];

  @override
  final String wireName = r'TransferPrice';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'airportId';
    yield serializers.serialize(
      object.airportId,
      specifiedType: const FullType(int),
    );
    yield r'vehicleTypeId';
    yield serializers.serialize(
      object.vehicleTypeId,
      specifiedType: const FullType(int),
    );
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(TransferPriceDirectionEnum),
    );
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(num),
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
    TransferPrice object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferPriceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'airportId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.airportId = valueDes;
          break;
        case r'vehicleTypeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.vehicleTypeId = valueDes;
          break;
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransferPriceDirectionEnum),
          ) as TransferPriceDirectionEnum;
          result.direction = valueDes;
          break;
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.amount = valueDes;
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
  TransferPrice deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferPriceBuilder();
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


class TransferPriceDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const TransferPriceDirectionEnum TO_AIRPORT = _$transferPriceDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const TransferPriceDirectionEnum FROM_AIRPORT = _$transferPriceDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferPriceDirectionEnum UNKNOWN = _$transferPriceDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferPriceDirectionEnum unknownDefaultOpenApi = _$transferPriceDirectionEnum_unknownDefaultOpenApi;

  static Serializer<TransferPriceDirectionEnum> get serializer => _$transferPriceDirectionEnumSerializer;

  const TransferPriceDirectionEnum._(String name): super(name);

  static BuiltSet<TransferPriceDirectionEnum> get values => _$transferPriceDirectionEnumValues;
  static TransferPriceDirectionEnum valueOf(String name) => _$transferPriceDirectionEnumValueOf(name);
}

