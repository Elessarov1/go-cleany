//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'open_transaction_action.g.dart';

/// OpenTransactionAction
///
/// Properties:
/// * [type] 
/// * [service] 
/// * [entityId] 
@BuiltValue()
abstract class OpenTransactionAction implements Built<OpenTransactionAction, OpenTransactionActionBuilder> {
  @BuiltValueField(wireName: r'type')
  OpenTransactionActionTypeEnum get type;
  // enum typeEnum {  OPEN_TRANSACTION,  };

  @BuiltValueField(wireName: r'service')
  OpenTransactionActionServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'entityId')
  int get entityId;

  OpenTransactionAction._();

  factory OpenTransactionAction([void updates(OpenTransactionActionBuilder b)]) = _$OpenTransactionAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OpenTransactionActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OpenTransactionAction> get serializer => _$OpenTransactionActionSerializer();
}

class _$OpenTransactionActionSerializer implements PrimitiveSerializer<OpenTransactionAction> {
  @override
  final Iterable<Type> types = const [OpenTransactionAction, _$OpenTransactionAction];

  @override
  final String wireName = r'OpenTransactionAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OpenTransactionAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OpenTransactionActionTypeEnum),
    );
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(OpenTransactionActionServiceEnum),
    );
    yield r'entityId';
    yield serializers.serialize(
      object.entityId,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OpenTransactionAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OpenTransactionActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenTransactionActionTypeEnum),
          ) as OpenTransactionActionTypeEnum;
          result.type = valueDes;
          break;
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenTransactionActionServiceEnum),
          ) as OpenTransactionActionServiceEnum;
          result.service = valueDes;
          break;
        case r'entityId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.entityId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OpenTransactionAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OpenTransactionActionBuilder();
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


class OpenTransactionActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN_TRANSACTION')
  static const OpenTransactionActionTypeEnum OPEN_TRANSACTION = _$openTransactionActionTypeEnum_OPEN_TRANSACTION;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenTransactionActionTypeEnum unknownDefaultOpenApi = _$openTransactionActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<OpenTransactionActionTypeEnum> get serializer => _$openTransactionActionTypeEnumSerializer;

  const OpenTransactionActionTypeEnum._(String name): super(name);

  static BuiltSet<OpenTransactionActionTypeEnum> get values => _$openTransactionActionTypeEnumValues;
  static OpenTransactionActionTypeEnum valueOf(String name) => _$openTransactionActionTypeEnumValueOf(name);
}

class OpenTransactionActionServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const OpenTransactionActionServiceEnum CLEANING = _$openTransactionActionServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const OpenTransactionActionServiceEnum RENTAL = _$openTransactionActionServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const OpenTransactionActionServiceEnum TRANSFER = _$openTransactionActionServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const OpenTransactionActionServiceEnum UNKNOWN = _$openTransactionActionServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenTransactionActionServiceEnum unknownDefaultOpenApi = _$openTransactionActionServiceEnum_unknownDefaultOpenApi;

  static Serializer<OpenTransactionActionServiceEnum> get serializer => _$openTransactionActionServiceEnumSerializer;

  const OpenTransactionActionServiceEnum._(String name): super(name);

  static BuiltSet<OpenTransactionActionServiceEnum> get values => _$openTransactionActionServiceEnumValues;
  static OpenTransactionActionServiceEnum valueOf(String name) => _$openTransactionActionServiceEnumValueOf(name);
}

