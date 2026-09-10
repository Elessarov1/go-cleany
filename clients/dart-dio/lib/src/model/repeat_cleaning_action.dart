//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'repeat_cleaning_action.g.dart';

/// RepeatCleaningAction
///
/// Properties:
/// * [type] 
/// * [sourceOrderId] 
@BuiltValue()
abstract class RepeatCleaningAction implements Built<RepeatCleaningAction, RepeatCleaningActionBuilder> {
  @BuiltValueField(wireName: r'type')
  RepeatCleaningActionTypeEnum get type;
  // enum typeEnum {  REPEAT_CLEANING,  };

  @BuiltValueField(wireName: r'sourceOrderId')
  int get sourceOrderId;

  RepeatCleaningAction._();

  factory RepeatCleaningAction([void updates(RepeatCleaningActionBuilder b)]) = _$RepeatCleaningAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RepeatCleaningActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RepeatCleaningAction> get serializer => _$RepeatCleaningActionSerializer();
}

class _$RepeatCleaningActionSerializer implements PrimitiveSerializer<RepeatCleaningAction> {
  @override
  final Iterable<Type> types = const [RepeatCleaningAction, _$RepeatCleaningAction];

  @override
  final String wireName = r'RepeatCleaningAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RepeatCleaningAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(RepeatCleaningActionTypeEnum),
    );
    yield r'sourceOrderId';
    yield serializers.serialize(
      object.sourceOrderId,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RepeatCleaningAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RepeatCleaningActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RepeatCleaningActionTypeEnum),
          ) as RepeatCleaningActionTypeEnum;
          result.type = valueDes;
          break;
        case r'sourceOrderId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sourceOrderId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RepeatCleaningAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RepeatCleaningActionBuilder();
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


class RepeatCleaningActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'REPEAT_CLEANING')
  static const RepeatCleaningActionTypeEnum REPEAT_CLEANING = _$repeatCleaningActionTypeEnum_REPEAT_CLEANING;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RepeatCleaningActionTypeEnum unknownDefaultOpenApi = _$repeatCleaningActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<RepeatCleaningActionTypeEnum> get serializer => _$repeatCleaningActionTypeEnumSerializer;

  const RepeatCleaningActionTypeEnum._(String name): super(name);

  static BuiltSet<RepeatCleaningActionTypeEnum> get values => _$repeatCleaningActionTypeEnumValues;
  static RepeatCleaningActionTypeEnum valueOf(String name) => _$repeatCleaningActionTypeEnumValueOf(name);
}

