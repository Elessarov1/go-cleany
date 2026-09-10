//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_airport.g.dart';

/// TransferAirport
///
/// Properties:
/// * [id] 
/// * [code] 
/// * [nameRu] 
/// * [nameEn] 
@BuiltValue()
abstract class TransferAirport implements Built<TransferAirport, TransferAirportBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'nameRu')
  String get nameRu;

  @BuiltValueField(wireName: r'nameEn')
  String get nameEn;

  TransferAirport._();

  factory TransferAirport([void updates(TransferAirportBuilder b)]) = _$TransferAirport;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferAirportBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferAirport> get serializer => _$TransferAirportSerializer();
}

class _$TransferAirportSerializer implements PrimitiveSerializer<TransferAirport> {
  @override
  final Iterable<Type> types = const [TransferAirport, _$TransferAirport];

  @override
  final String wireName = r'TransferAirport';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferAirport object, {
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
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferAirport object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferAirportBuilder result,
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferAirport deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferAirportBuilder();
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


