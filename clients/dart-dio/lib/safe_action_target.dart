import 'package:loco_place_api/src/model/action_target.dart';
import 'package:loco_place_api/src/serializers.dart';

const _supportedActionTypes = <String>{
  'OPEN_TRANSACTION',
  'OPEN_CLEANING_HISTORY',
  'REPEAT_CLEANING',
  'REPEAT_TRANSFER',
  'START_RENTAL_CLEANING',
  'START_RENTAL_TRANSFER',
  'OPEN_ADMIN_TRANSACTION',
  'OPEN_SUPPORT_CASE',
};

/// Deserializes a known v1 action and returns `null` for a future action type.
///
/// Clients must treat `null` as a safe no-op and may still render the surrounding
/// notification or activity item. This avoids turning an additive server action
/// into a crash in an older application release.
ActionTarget? deserializeActionTargetSafely(Map<String, Object?> json) {
  final type = json['type'];
  if (type is! String || !_supportedActionTypes.contains(type)) return null;
  return standardSerializers.deserializeWith(ActionTarget.serializer, json);
}
