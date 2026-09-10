//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_vehicle_type.g.dart';

/// TransferVehicleType
///
/// Properties:
/// * [id] 
/// * [code] 
/// * [nameRu] 
/// * [nameEn] 
/// * [maxPassengers] 
/// * [maxLuggage] 
@BuiltValue()
abstract class TransferVehicleType implements Built<TransferVehicleType, TransferVehicleTypeBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'nameRu')
  String get nameRu;

  @BuiltValueField(wireName: r'nameEn')
  String get nameEn;

  @BuiltValueField(wireName: r'maxPassengers')
  int get maxPassengers;

  @BuiltValueField(wireName: r'maxLuggage')
  int get maxLuggage;

  TransferVehicleType._();

  factory TransferVehicleType([void updates(TransferVehicleTypeBuilder b)]) = _$TransferVehicleType;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferVehicleTypeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferVehicleType> get serializer => _$TransferVehicleTypeSerializer();
}

class _$TransferVehicleTypeSerializer implements PrimitiveSerializer<TransferVehicleType> {
  @override
  final Iterable<Type> types = const [TransferVehicleType, _$TransferVehicleType];

  @override
  final String wireName = r'TransferVehicleType';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferVehicleType object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'nameRu';
    yield serializers.serialize(
      object.nameRu,
      specifiedType: const FullType(String),
    );
    yield r'nameEn';
    yield serializers.serialize(
      object.nameEn,
      specifiedType: const FullType(String),
    );
    yield r'maxPassengers';
    yield serializers.serialize(
      object.maxPassengers,
      specifiedType: const FullType(int),
    );
    yield r'maxLuggage';
    yield serializers.serialize(
      object.maxLuggage,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferVehicleType object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferVehicleTypeBuilder result,
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
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'nameRu':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nameRu = valueDes;
          break;
        case r'nameEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nameEn = valueDes;
          break;
        case r'maxPassengers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxPassengers = valueDes;
          break;
        case r'maxLuggage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxLuggage = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferVehicleType deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferVehicleTypeBuilder();
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


