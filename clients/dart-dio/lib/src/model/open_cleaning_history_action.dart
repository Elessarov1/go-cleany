//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'open_cleaning_history_action.g.dart';

/// OpenCleaningHistoryAction
///
/// Properties:
/// * [type] 
@BuiltValue()
abstract class OpenCleaningHistoryAction implements Built<OpenCleaningHistoryAction, OpenCleaningHistoryActionBuilder> {
  @BuiltValueField(wireName: r'type')
  OpenCleaningHistoryActionTypeEnum get type;
  // enum typeEnum {  OPEN_CLEANING_HISTORY,  };

  OpenCleaningHistoryAction._();

  factory OpenCleaningHistoryAction([void updates(OpenCleaningHistoryActionBuilder b)]) = _$OpenCleaningHistoryAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OpenCleaningHistoryActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OpenCleaningHistoryAction> get serializer => _$OpenCleaningHistoryActionSerializer();
}

class _$OpenCleaningHistoryActionSerializer implements PrimitiveSerializer<OpenCleaningHistoryAction> {
  @override
  final Iterable<Type> types = const [OpenCleaningHistoryAction, _$OpenCleaningHistoryAction];

  @override
  final String wireName = r'OpenCleaningHistoryAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OpenCleaningHistoryAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OpenCleaningHistoryActionTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OpenCleaningHistoryAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OpenCleaningHistoryActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenCleaningHistoryActionTypeEnum),
          ) as OpenCleaningHistoryActionTypeEnum;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OpenCleaningHistoryAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OpenCleaningHistoryActionBuilder();
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


class OpenCleaningHistoryActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN_CLEANING_HISTORY')
  static const OpenCleaningHistoryActionTypeEnum OPEN_CLEANING_HISTORY = _$openCleaningHistoryActionTypeEnum_OPEN_CLEANING_HISTORY;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenCleaningHistoryActionTypeEnum unknownDefaultOpenApi = _$openCleaningHistoryActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<OpenCleaningHistoryActionTypeEnum> get serializer => _$openCleaningHistoryActionTypeEnumSerializer;

  const OpenCleaningHistoryActionTypeEnum._(String name): super(name);

  static BuiltSet<OpenCleaningHistoryActionTypeEnum> get values => _$openCleaningHistoryActionTypeEnumValues;
  static OpenCleaningHistoryActionTypeEnum valueOf(String name) => _$openCleaningHistoryActionTypeEnumValueOf(name);
}

