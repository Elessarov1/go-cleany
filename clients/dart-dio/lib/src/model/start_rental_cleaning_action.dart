//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'start_rental_cleaning_action.g.dart';

/// StartRentalCleaningAction
///
/// Properties:
/// * [type] 
/// * [rentalBookingId] 
@BuiltValue()
abstract class StartRentalCleaningAction implements Built<StartRentalCleaningAction, StartRentalCleaningActionBuilder> {
  @BuiltValueField(wireName: r'type')
  StartRentalCleaningActionTypeEnum get type;
  // enum typeEnum {  START_RENTAL_CLEANING,  };

  @BuiltValueField(wireName: r'rentalBookingId')
  int get rentalBookingId;

  StartRentalCleaningAction._();

  factory StartRentalCleaningAction([void updates(StartRentalCleaningActionBuilder b)]) = _$StartRentalCleaningAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(StartRentalCleaningActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<StartRentalCleaningAction> get serializer => _$StartRentalCleaningActionSerializer();
}

class _$StartRentalCleaningActionSerializer implements PrimitiveSerializer<StartRentalCleaningAction> {
  @override
  final Iterable<Type> types = const [StartRentalCleaningAction, _$StartRentalCleaningAction];

  @override
  final String wireName = r'StartRentalCleaningAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    StartRentalCleaningAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(StartRentalCleaningActionTypeEnum),
    );
    yield r'rentalBookingId';
    yield serializers.serialize(
      object.rentalBookingId,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    StartRentalCleaningAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required StartRentalCleaningActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StartRentalCleaningActionTypeEnum),
          ) as StartRentalCleaningActionTypeEnum;
          result.type = valueDes;
          break;
        case r'rentalBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rentalBookingId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  StartRentalCleaningAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = StartRentalCleaningActionBuilder();
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


class StartRentalCleaningActionTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'START_RENTAL_CLEANING')
  static const StartRentalCleaningActionTypeEnum START_RENTAL_CLEANING = _$startRentalCleaningActionTypeEnum_START_RENTAL_CLEANING;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const StartRentalCleaningActionTypeEnum unknownDefaultOpenApi = _$startRentalCleaningActionTypeEnum_unknownDefaultOpenApi;

  static Serializer<StartRentalCleaningActionTypeEnum> get serializer => _$startRentalCleaningActionTypeEnumSerializer;

  const StartRentalCleaningActionTypeEnum._(String name): super(name);

  static BuiltSet<StartRentalCleaningActionTypeEnum> get values => _$startRentalCleaningActionTypeEnumValues;
  static StartRentalCleaningActionTypeEnum valueOf(String name) => _$startRentalCleaningActionTypeEnumValueOf(name);
}

