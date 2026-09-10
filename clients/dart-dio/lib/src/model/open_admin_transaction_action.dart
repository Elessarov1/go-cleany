//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'open_admin_transaction_action.g.dart';

/// OpenAdminTransactionAction
///
/// Properties:
/// * [type] 
/// * [service] 
/// * [entityId] 
@BuiltValue()
abstract class OpenAdminTransactionAction implements Built<OpenAdminTransactionAction, OpenAdminTransactionActionBuilder> {
  @BuiltValueField(wireName: r'type')
  OpenAdminTransactionActionTypeEnum get type;
  // enum typeEnum {  OPEN_ADMIN_TRANSACTION,  };

  @BuiltValueField(wireName: r'service')
  OpenAdminTransactionActionServiceEnum get service;
  // enum serviceEnum {  CLEANING,  RENTAL,  TRANSFER,  UNKNOWN,  };

  @BuiltValueField(wireName: r'entityId')
  int get entityId;

  OpenAdminTransactionAction._();

  factory OpenAdminTransactionAction([void updates(OpenAdminTransactionActionBuilder b)]) = _$OpenAdminTransactionAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OpenAdminTransactionActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OpenAdminTransactionAction> get serializer => _$OpenAdminTransactionActionSerializer();
}

class _$OpenAdminTransactionActionSerializer implements PrimitiveSerializer<OpenAdminTransactionAction> {
  @override
  final Iterable<Type> types = const [OpenAdminTransactionAction, _$OpenAdminTransactionAction];

  @override
  final String wireName = r'OpenAdminTransactionAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OpenAdminTransactionAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OpenAdminTransactionActionTypeEnum),
    );
    yield r'service';
    yield serializers.serialize(
      object.service,
      specifiedType: const FullType(OpenAdminTransactionActionServiceEnum),
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
    OpenAdminTransactionAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OpenAdminTransactionActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenAdminTransactionActionTypeEnum),
          ) as OpenAdminTransactionActionTypeEnum;
          result.type = valueDes;
          break;
        case r'service':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OpenAdminTransactionActionServiceEnum),
          ) as OpenAdminTransactionActionServiceEnum;
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
  OpenAdminTransactionAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OpenAdminTransactionActionBuilder();
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


class OpenAdminTransactionActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN_ADMIN_TRANSACTION')
  static const OpenAdminTransactionActionTypeEnum OPEN_ADMIN_TRANSACTION = _$openAdminTransactionActionTypeEnum_OPEN_ADMIN_TRANSACTION;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenAdminTransactionActionTypeEnum unknownDefaultOpenApi = _$openAdminTransactionActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<OpenAdminTransactionActionTypeEnum> get serializer => _$openAdminTransactionActionTypeEnumSerializer;

  const OpenAdminTransactionActionTypeEnum._(String name): super(name);

  static BuiltSet<OpenAdminTransactionActionTypeEnum> get values => _$openAdminTransactionActionTypeEnumValues;
  static OpenAdminTransactionActionTypeEnum valueOf(String name) => _$openAdminTransactionActionTypeEnumValueOf(name);
}

class OpenAdminTransactionActionServiceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLEANING')
  static const OpenAdminTransactionActionServiceEnum CLEANING = _$openAdminTransactionActionServiceEnum_CLEANING;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const OpenAdminTransactionActionServiceEnum RENTAL = _$openAdminTransactionActionServiceEnum_RENTAL;
  @BuiltValueEnumConst(wireName: r'TRANSFER')
  static const OpenAdminTransactionActionServiceEnum TRANSFER = _$openAdminTransactionActionServiceEnum_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const OpenAdminTransactionActionServiceEnum UNKNOWN = _$openAdminTransactionActionServiceEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const OpenAdminTransactionActionServiceEnum unknownDefaultOpenApi = _$openAdminTransactionActionServiceEnum_unknownDefaultOpenApi;

  static Serializer<OpenAdminTransactionActionServiceEnum> get serializer => _$openAdminTransactionActionServiceEnumSerializer;

  const OpenAdminTransactionActionServiceEnum._(String name): super(name);

  static BuiltSet<OpenAdminTransactionActionServiceEnum> get values => _$openAdminTransactionActionServiceEnumValues;
  static OpenAdminTransactionActionServiceEnum valueOf(String name) => _$openAdminTransactionActionServiceEnumValueOf(name);
}

