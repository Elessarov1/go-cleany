//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_repeat_reminder_request.g.dart';

/// CleaningRepeatReminderRequest
///
/// Properties:
/// * [selection] 
@BuiltValue()
abstract class CleaningRepeatReminderRequest implements Built<CleaningRepeatReminderRequest, CleaningRepeatReminderRequestBuilder> {
  @BuiltValueField(wireName: r'selection')
  CleaningRepeatReminderRequestSelectionEnum get selection;
  // enum selectionEnum {  IN_14_DAYS,  IN_30_DAYS,  DO_NOT_REMIND,  };

  CleaningRepeatReminderRequest._();

  factory CleaningRepeatReminderRequest([void updates(CleaningRepeatReminderRequestBuilder b)]) = _$CleaningRepeatReminderRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningRepeatReminderRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningRepeatReminderRequest> get serializer => _$CleaningRepeatReminderRequestSerializer();
}

class _$CleaningRepeatReminderRequestSerializer implements PrimitiveSerializer<CleaningRepeatReminderRequest> {
  @override
  final Iterable<Type> types = const [CleaningRepeatReminderRequest, _$CleaningRepeatReminderRequest];

  @override
  final String wireName = r'CleaningRepeatReminderRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningRepeatReminderRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'selection';
    yield serializers.serialize(
      object.selection,
      specifiedType: const FullType(CleaningRepeatReminderRequestSelectionEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningRepeatReminderRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningRepeatReminderRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'selection':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CleaningRepeatReminderRequestSelectionEnum),
          ) as CleaningRepeatReminderRequestSelectionEnum;
          result.selection = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningRepeatReminderRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningRepeatReminderRequestBuilder();
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


class CleaningRepeatReminderRequestSelectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IN_14_DAYS')
  static const CleaningRepeatReminderRequestSelectionEnum IN_14_DAYS = _$cleaningRepeatReminderRequestSelectionEnum_IN_14_DAYS;
  @BuiltValueEnumConst(wireName: r'IN_30_DAYS')
  static const CleaningRepeatReminderRequestSelectionEnum IN_30_DAYS = _$cleaningRepeatReminderRequestSelectionEnum_IN_30_DAYS;
  @BuiltValueEnumConst(wireName: r'DO_NOT_REMIND')
  static const CleaningRepeatReminderRequestSelectionEnum DO_NOT_REMIND = _$cleaningRepeatReminderRequestSelectionEnum_DO_NOT_REMIND;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatReminderRequestSelectionEnum unknownDefaultOpenApi = _$cleaningRepeatReminderRequestSelectionEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatReminderRequestSelectionEnum> get serializer => _$cleaningRepeatReminderRequestSelectionEnumSerializer;

  const CleaningRepeatReminderRequestSelectionEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatReminderRequestSelectionEnum> get values => _$cleaningRepeatReminderRequestSelectionEnumValues;
  static CleaningRepeatReminderRequestSelectionEnum valueOf(String name) => _$cleaningRepeatReminderRequestSelectionEnumValueOf(name);
}

