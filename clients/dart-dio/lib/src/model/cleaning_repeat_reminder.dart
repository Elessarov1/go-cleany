//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_repeat_reminder.g.dart';

/// CleaningRepeatReminder
///
/// Properties:
/// * [selection] 
/// * [status] 
/// * [scheduledDate] 
/// * [notifiedAt] 
/// * [editable] 
@BuiltValue()
abstract class CleaningRepeatReminder implements Built<CleaningRepeatReminder, CleaningRepeatReminderBuilder> {
  @BuiltValueField(wireName: r'selection')
  CleaningRepeatReminderSelectionEnum? get selection;
  // enum selectionEnum {  IN_14_DAYS,  IN_30_DAYS,  DO_NOT_REMIND,  UNKNOWN,  ,  };

  @BuiltValueField(wireName: r'status')
  CleaningRepeatReminderStatusEnum? get status;
  // enum statusEnum {  PENDING,  NOTIFIED,  DISABLED,  SUPERSEDED,  EXPIRED,  UNKNOWN,  ,  };

  @BuiltValueField(wireName: r'scheduledDate')
  Date? get scheduledDate;

  @BuiltValueField(wireName: r'notifiedAt')
  DateTime? get notifiedAt;

  @BuiltValueField(wireName: r'editable')
  bool get editable;

  CleaningRepeatReminder._();

  factory CleaningRepeatReminder([void updates(CleaningRepeatReminderBuilder b)]) = _$CleaningRepeatReminder;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningRepeatReminderBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningRepeatReminder> get serializer => _$CleaningRepeatReminderSerializer();
}

class _$CleaningRepeatReminderSerializer implements PrimitiveSerializer<CleaningRepeatReminder> {
  @override
  final Iterable<Type> types = const [CleaningRepeatReminder, _$CleaningRepeatReminder];

  @override
  final String wireName = r'CleaningRepeatReminder';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningRepeatReminder object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.selection != null) {
      yield r'selection';
      yield serializers.serialize(
        object.selection,
        specifiedType: const FullType.nullable(CleaningRepeatReminderSelectionEnum),
      );
    }
    if (object.status != null) {
      yield r'status';
      yield serializers.serialize(
        object.status,
        specifiedType: const FullType.nullable(CleaningRepeatReminderStatusEnum),
      );
    }
    if (object.scheduledDate != null) {
      yield r'scheduledDate';
      yield serializers.serialize(
        object.scheduledDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.notifiedAt != null) {
      yield r'notifiedAt';
      yield serializers.serialize(
        object.notifiedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'editable';
    yield serializers.serialize(
      object.editable,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CleaningRepeatReminder object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningRepeatReminderBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'selection':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CleaningRepeatReminderSelectionEnum),
          ) as CleaningRepeatReminderSelectionEnum?;
          if (valueDes == null) continue;
          result.selection = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CleaningRepeatReminderStatusEnum),
          ) as CleaningRepeatReminderStatusEnum?;
          if (valueDes == null) continue;
          result.status = valueDes;
          break;
        case r'scheduledDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.scheduledDate = valueDes;
          break;
        case r'notifiedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.notifiedAt = valueDes;
          break;
        case r'editable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.editable = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CleaningRepeatReminder deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningRepeatReminderBuilder();
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


class CleaningRepeatReminderSelectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IN_14_DAYS')
  static const CleaningRepeatReminderSelectionEnum IN_14_DAYS = _$cleaningRepeatReminderSelectionEnum_IN_14_DAYS;
  @BuiltValueEnumConst(wireName: r'IN_30_DAYS')
  static const CleaningRepeatReminderSelectionEnum IN_30_DAYS = _$cleaningRepeatReminderSelectionEnum_IN_30_DAYS;
  @BuiltValueEnumConst(wireName: r'DO_NOT_REMIND')
  static const CleaningRepeatReminderSelectionEnum DO_NOT_REMIND = _$cleaningRepeatReminderSelectionEnum_DO_NOT_REMIND;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningRepeatReminderSelectionEnum UNKNOWN = _$cleaningRepeatReminderSelectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatReminderSelectionEnum unknownDefaultOpenApi = _$cleaningRepeatReminderSelectionEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatReminderSelectionEnum> get serializer => _$cleaningRepeatReminderSelectionEnumSerializer;

  const CleaningRepeatReminderSelectionEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatReminderSelectionEnum> get values => _$cleaningRepeatReminderSelectionEnumValues;
  static CleaningRepeatReminderSelectionEnum valueOf(String name) => _$cleaningRepeatReminderSelectionEnumValueOf(name);
}

class CleaningRepeatReminderStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PENDING')
  static const CleaningRepeatReminderStatusEnum PENDING = _$cleaningRepeatReminderStatusEnum_PENDING;
  @BuiltValueEnumConst(wireName: r'NOTIFIED')
  static const CleaningRepeatReminderStatusEnum NOTIFIED = _$cleaningRepeatReminderStatusEnum_NOTIFIED;
  @BuiltValueEnumConst(wireName: r'DISABLED')
  static const CleaningRepeatReminderStatusEnum DISABLED = _$cleaningRepeatReminderStatusEnum_DISABLED;
  @BuiltValueEnumConst(wireName: r'SUPERSEDED')
  static const CleaningRepeatReminderStatusEnum SUPERSEDED = _$cleaningRepeatReminderStatusEnum_SUPERSEDED;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const CleaningRepeatReminderStatusEnum EXPIRED = _$cleaningRepeatReminderStatusEnum_EXPIRED;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const CleaningRepeatReminderStatusEnum UNKNOWN = _$cleaningRepeatReminderStatusEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const CleaningRepeatReminderStatusEnum unknownDefaultOpenApi = _$cleaningRepeatReminderStatusEnum_unknownDefaultOpenApi;

  static Serializer<CleaningRepeatReminderStatusEnum> get serializer => _$cleaningRepeatReminderStatusEnumSerializer;

  const CleaningRepeatReminderStatusEnum._(String name): super(name);

  static BuiltSet<CleaningRepeatReminderStatusEnum> get values => _$cleaningRepeatReminderStatusEnumValues;
  static CleaningRepeatReminderStatusEnum valueOf(String name) => _$cleaningRepeatReminderStatusEnumValueOf(name);
}

