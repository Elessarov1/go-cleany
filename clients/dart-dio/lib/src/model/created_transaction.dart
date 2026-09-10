//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/money.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'created_transaction.g.dart';

/// CreatedTransaction
///
/// Properties:
/// * [id] 
/// * [money] 
@BuiltValue()
abstract class CreatedTransaction implements Built<CreatedTransaction, CreatedTransactionBuilder> {
  @BuiltValueField(wireName: r'id')
  int get id;

  @BuiltValueField(wireName: r'money')
  Money get money;

  CreatedTransaction._();

  factory CreatedTransaction([void updates(CreatedTransactionBuilder b)]) = _$CreatedTransaction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreatedTransactionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreatedTransaction> get serializer => _$CreatedTransactionSerializer();
}

class _$CreatedTransactionSerializer implements PrimitiveSerializer<CreatedTransaction> {
  @override
  final Iterable<Type> types = const [CreatedTransaction, _$CreatedTransaction];

  @override
  final String wireName = r'CreatedTransaction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreatedTransaction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(int),
    );
    yield r'money';
    yield serializers.serialize(
      object.money,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreatedTransaction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreatedTransactionBuilder result,
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
        case r'money':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.money.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreatedTransaction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreatedTransactionBuilder();
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


