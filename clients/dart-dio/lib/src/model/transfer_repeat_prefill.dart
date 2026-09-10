//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_repeat_prefill.g.dart';

/// TransferRepeatPrefill
///
/// Properties:
/// * [sourceBookingId] 
/// * [direction] 
/// * [airportId] 
/// * [vehicleTypeId] 
/// * [address] 
/// * [passengerCount] 
/// * [luggageCount] 
@BuiltValue()
abstract class TransferRepeatPrefill implements Built<TransferRepeatPrefill, TransferRepeatPrefillBuilder> {
  @BuiltValueField(wireName: r'sourceBookingId')
  int get sourceBookingId;

  @BuiltValueField(wireName: r'direction')
  TransferRepeatPrefillDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'airportId')
  int? get airportId;

  @BuiltValueField(wireName: r'vehicleTypeId')
  int? get vehicleTypeId;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'passengerCount')
  int get passengerCount;

  @BuiltValueField(wireName: r'luggageCount')
  int get luggageCount;

  TransferRepeatPrefill._();

  factory TransferRepeatPrefill([void updates(TransferRepeatPrefillBuilder b)]) = _$TransferRepeatPrefill;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferRepeatPrefillBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferRepeatPrefill> get serializer => _$TransferRepeatPrefillSerializer();
}

class _$TransferRepeatPrefillSerializer implements PrimitiveSerializer<TransferRepeatPrefill> {
  @override
  final Iterable<Type> types = const [TransferRepeatPrefill, _$TransferRepeatPrefill];

  @override
  final String wireName = r'TransferRepeatPrefill';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferRepeatPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sourceBookingId';
    yield serializers.serialize(
      object.sourceBookingId,
      specifiedType: const FullType(int),
    );
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(TransferRepeatPrefillDirectionEnum),
    );
    yield r'airportId';
    yield object.airportId == null ? null : serializers.serialize(
      object.airportId,
      specifiedType: const FullType.nullable(int),
    );
    yield r'vehicleTypeId';
    yield object.vehicleTypeId == null ? null : serializers.serialize(
      object.vehicleTypeId,
      specifiedType: const FullType.nullable(int),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
    yield r'passengerCount';
    yield serializers.serialize(
      object.passengerCount,
      specifiedType: const FullType(int),
    );
    yield r'luggageCount';
    yield serializers.serialize(
      object.luggageCount,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferRepeatPrefill object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferRepeatPrefillBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sourceBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceBookingId = valueDes;
          break;
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransferRepeatPrefillDirectionEnum),
          ) as TransferRepeatPrefillDirectionEnum;
          result.direction = valueDes;
          break;
        case r'airportId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.airportId = valueDes;
          break;
        case r'vehicleTypeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.vehicleTypeId = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'passengerCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.passengerCount = valueDes;
          break;
        case r'luggageCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.luggageCount = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferRepeatPrefill deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferRepeatPrefillBuilder();
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


class TransferRepeatPrefillDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const TransferRepeatPrefillDirectionEnum TO_AIRPORT = _$transferRepeatPrefillDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const TransferRepeatPrefillDirectionEnum FROM_AIRPORT = _$transferRepeatPrefillDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferRepeatPrefillDirectionEnum UNKNOWN = _$transferRepeatPrefillDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferRepeatPrefillDirectionEnum unknownDefaultOpenApi = _$transferRepeatPrefillDirectionEnum_unknownDefaultOpenApi;

  static Serializer<TransferRepeatPrefillDirectionEnum> get serializer => _$transferRepeatPrefillDirectionEnumSerializer;

  const TransferRepeatPrefillDirectionEnum._(String name): super(name);

  static BuiltSet<TransferRepeatPrefillDirectionEnum> get values => _$transferRepeatPrefillDirectionEnumValues;
  static TransferRepeatPrefillDirectionEnum valueOf(String name) => _$transferRepeatPrefillDirectionEnumValueOf(name);
}

