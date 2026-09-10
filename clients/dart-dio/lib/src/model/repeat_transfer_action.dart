//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'repeat_transfer_action.g.dart';

/// RepeatTransferAction
///
/// Properties:
/// * [type] 
/// * [sourceBookingId] 
@BuiltValue()
abstract class RepeatTransferAction implements Built<RepeatTransferAction, RepeatTransferActionBuilder> {
  @BuiltValueField(wireName: r'type')
  RepeatTransferActionTypeEnum get type;
  // enum typeEnum {  REPEAT_TRANSFER,  };

  @BuiltValueField(wireName: r'sourceBookingId')
  int get sourceBookingId;

  RepeatTransferAction._();

  factory RepeatTransferAction([void updates(RepeatTransferActionBuilder b)]) = _$RepeatTransferAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RepeatTransferActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RepeatTransferAction> get serializer => _$RepeatTransferActionSerializer();
}

class _$RepeatTransferActionSerializer implements PrimitiveSerializer<RepeatTransferAction> {
  @override
  final Iterable<Type> types = const [RepeatTransferAction, _$RepeatTransferAction];

  @override
  final String wireName = r'RepeatTransferAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RepeatTransferAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(RepeatTransferActionTypeEnum),
    );
    yield r'sourceBookingId';
    yield serializers.serialize(
      object.sourceBookingId,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RepeatTransferAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RepeatTransferActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RepeatTransferActionTypeEnum),
          ) as RepeatTransferActionTypeEnum;
          result.type = valueDes;
          break;
        case r'sourceBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceBookingId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RepeatTransferAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RepeatTransferActionBuilder();
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


class RepeatTransferActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REPEAT_TRANSFER')
  static const RepeatTransferActionTypeEnum REPEAT_TRANSFER = _$repeatTransferActionTypeEnum_REPEAT_TRANSFER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RepeatTransferActionTypeEnum unknownDefaultOpenApi = _$repeatTransferActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<RepeatTransferActionTypeEnum> get serializer => _$repeatTransferActionTypeEnumSerializer;

  const RepeatTransferActionTypeEnum._(String name): super(name);

  static BuiltSet<RepeatTransferActionTypeEnum> get values => _$repeatTransferActionTypeEnumValues;
  static RepeatTransferActionTypeEnum valueOf(String name) => _$repeatTransferActionTypeEnumValueOf(name);
}

