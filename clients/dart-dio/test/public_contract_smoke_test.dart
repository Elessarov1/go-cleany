import 'package:built_value/serializer.dart';
import 'package:loco_place_api/loco_place_api.dart';
import 'package:loco_place_api/safe_action_target.dart';
import 'package:test/test.dart';

void main() {
  test('native session wire payload deserializes through the generated SDK', () {
    final value = standardSerializers.deserializeWith(SessionTokens.serializer, {
      'sessionId': '3a438bd3-30af-4a55-a84d-bc03f012af68',
      'tokenType': 'Bearer',
      'accessToken': 'lp_at_example',
      'refreshToken': 'lp_rt_example',
      'accessExpiresAt': '2026-09-10T10:15:00Z',
      'refreshExpiresAt': '2026-10-10T10:00:00Z',
      'absoluteExpiresAt': '2026-12-09T10:00:00Z',
    });

    expect(value, isNotNull);
    expect(value!.tokenType, SessionTokensTokenTypeEnum.bearer);
    expect(value.absoluteExpiresAt.toUtc(), DateTime.parse('2026-12-09T10:00:00Z'));
  });

  test('unknown provider enum uses the generated forward-compatible fallback', () {
    final value = standardSerializers.deserialize(
      'FUTURE_PROVIDER',
      specifiedType: const FullType(IdentityProvider),
    );

    expect(value, IdentityProvider.unknownDefaultOpenApi);
  });

  test('unknown action type is a safe no-op for an older client', () {
    final value = deserializeActionTargetSafely({
      'type': 'FUTURE_ACTION',
      'entityId': 42,
    });

    expect(value, isNull);
  });
}
