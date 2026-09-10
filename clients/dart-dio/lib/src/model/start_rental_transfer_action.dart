//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'start_rental_transfer_action.g.dart';

/// StartRentalTransferAction
///
/// Properties:
/// * [type] 
/// * [rentalBookingId] 
/// * [context] 
@BuiltValue()
abstract class StartRentalTransferAction implements Built<StartRentalTransferAction, StartRentalTransferActionBuilder> {
  @BuiltValueField(wireName: r'type')
  StartRentalTransferActionTypeEnum get type;
  // enum typeEnum {  START_RENTAL_TRANSFER,  };

  @BuiltValueField(wireName: r'rentalBookingId')
  int get rentalBookingId;

  @BuiltValueField(wireName: r'context')
  StartRentalTransferActionContextEnum get context;
  // enum contextEnum {  ARRIVAL,  CHECKOUT,  UNKNOWN,  };

  StartRentalTransferAction._();

  factory StartRentalTransferAction([void updates(StartRentalTransferActionBuilder b)]) = _$StartRentalTransferAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(StartRentalTransferActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<StartRentalTransferAction> get serializer => _$StartRentalTransferActionSerializer();
}

class _$StartRentalTransferActionSerializer implements PrimitiveSerializer<StartRentalTransferAction> {
  @override
  final Iterable<Type> types = const [StartRentalTransferAction, _$StartRentalTransferAction];

  @override
  final String wireName = r'StartRentalTransferAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    StartRentalTransferAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(StartRentalTransferActionTypeEnum),
    );
    yield r'rentalBookingId';
    yield serializers.serialize(
      object.rentalBookingId,
      specifiedType: const FullType(int),
    );
    yield r'context';
    yield serializers.serialize(
      object.context,
      specifiedType: const FullType(StartRentalTransferActionContextEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    StartRentalTransferAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required StartRentalTransferActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StartRentalTransferActionTypeEnum),
          ) as StartRentalTransferActionTypeEnum;
          result.type = valueDes;
          break;
        case r'rentalBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rentalBookingId = valueDes;
          break;
        case r'context':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StartRentalTransferActionContextEnum),
          ) as StartRentalTransferActionContextEnum;
          result.context = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  StartRentalTransferAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = StartRentalTransferActionBuilder();
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


class StartRentalTransferActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'START_RENTAL_TRANSFER')
  static const StartRentalTransferActionTypeEnum START_RENTAL_TRANSFER = _$startRentalTransferActionTypeEnum_START_RENTAL_TRANSFER;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const StartRentalTransferActionTypeEnum unknownDefaultOpenApi = _$startRentalTransferActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<StartRentalTransferActionTypeEnum> get serializer => _$startRentalTransferActionTypeEnumSerializer;

  const StartRentalTransferActionTypeEnum._(String name): super(name);

  static BuiltSet<StartRentalTransferActionTypeEnum> get values => _$startRentalTransferActionTypeEnumValues;
  static StartRentalTransferActionTypeEnum valueOf(String name) => _$startRentalTransferActionTypeEnumValueOf(name);
}

class StartRentalTransferActionContextEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ARRIVAL')
  static const StartRentalTransferActionContextEnum ARRIVAL = _$startRentalTransferActionContextEnum_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'CHECKOUT')
  static const StartRentalTransferActionContextEnum CHECKOUT = _$startRentalTransferActionContextEnum_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const StartRentalTransferActionContextEnum UNKNOWN = _$startRentalTransferActionContextEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const StartRentalTransferActionContextEnum unknownDefaultOpenApi = _$startRentalTransferActionContextEnum_unknownDefaultOpenApi;

  static Serializer<StartRentalTransferActionContextEnum> get serializer => _$startRentalTransferActionContextEnumSerializer;

  const StartRentalTransferActionContextEnum._(String name): super(name);

  static BuiltSet<StartRentalTransferActionContextEnum> get values => _$startRentalTransferActionContextEnumValues;
  static StartRentalTransferActionContextEnum valueOf(String name) => _$startRentalTransferActionContextEnumValueOf(name);
}

