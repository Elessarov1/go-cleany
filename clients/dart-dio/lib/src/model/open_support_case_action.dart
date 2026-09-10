//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'open_support_case_action.g.dart';

/// OpenSupportCaseAction
///
/// Properties:
/// * [type] 
/// * [caseId] 
@BuiltValue()
abstract class OpenSupportCaseAction implements Built<OpenSupportCaseAction, OpenSupportCaseActionBuilder> {
  @BuiltValueField(wireName: r'type')
  OpenSupportCaseActionTypeEnum get type;
  // enum typeEnum {  OPEN_SUPPORT_CASE,  };

  @BuiltValueField(wireName: r'caseId')
  int get caseId;

  OpenSupportCaseAction._();

  factory OpenSupportCaseAction([void updates(OpenSupportCaseActionBuilder b)]) = _$OpenSupportCaseAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OpenSupportCaseActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OpenSupportCaseAction> get serializer => _$OpenSupportCaseActionSerializer();
}

class _$OpenSupportCaseActionSerializer implements PrimitiveSerializer<OpenSupportCaseAction> {
  @override
  final Iterable<Type> types = const [OpenSupportCaseAction, _$OpenSupportCaseAction];

  @override
  final String wireName = r'OpenSupportCaseAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OpenSupportCaseAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OpenSupportCaseActionTypeEnum),
    );
    yield r'caseId';
    yield serializers.serialize(
      object.caseId,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OpenSupportCaseAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OpenSupportCaseActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenSupportCaseActionTypeEnum),
          ) as OpenSupportCaseActionTypeEnum;
          result.type = valueDes;
          break;
        case r'caseId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.caseId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OpenSupportCaseAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OpenSupportCaseActionBuilder();
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


class OpenSupportCaseActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN_SUPPORT_CASE')
  static const OpenSupportCaseActionTypeEnum OPEN_SUPPORT_CASE = _$openSupportCaseActionTypeEnum_OPEN_SUPPORT_CASE;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenSupportCaseActionTypeEnum unknownDefaultOpenApi = _$openSupportCaseActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<OpenSupportCaseActionTypeEnum> get serializer => _$openSupportCaseActionTypeEnumSerializer;

  const OpenSupportCaseActionTypeEnum._(String name): super(name);

  static BuiltSet<OpenSupportCaseActionTypeEnum> get values => _$openSupportCaseActionTypeEnumValues;
  static OpenSupportCaseActionTypeEnum valueOf(String name) => _$openSupportCaseActionTypeEnumValueOf(name);
}

