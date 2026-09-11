import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for AuthenticationApi
void main() {
  final instance = LocoPlaceApi().getAuthenticationApi();

  group(AuthenticationApi, () {
    //Future<NativeChallenge> createNativeChallenge(CreateNativeChallengeRequest createNativeChallengeRequest) async
    test('test createNativeChallenge', () async {
      // TODO
    });

    //Future<TelegramLoginAttempt> createTelegramLoginAttempt() async
    test('test createTelegramLoginAttempt', () async {
      // TODO
    });

    //Future<SessionTokens> exchangeTelegramLoginAttempt(String id, ExchangeTelegramLoginAttemptRequest exchangeTelegramLoginAttemptRequest) async
    test('test exchangeTelegramLoginAttempt', () async {
      // TODO
    });

    //Future<SessionTokens> loginWithApple(NativeProviderLoginRequest nativeProviderLoginRequest) async
    test('test loginWithApple', () async {
      // TODO
    });

    //Future<SessionTokens> loginWithGoogle(NativeProviderLoginRequest nativeProviderLoginRequest) async
    test('test loginWithGoogle', () async {
      // TODO
    });

    //Future logoutCurrentSession() async
    test('test logoutCurrentSession', () async {
      // TODO
    });

    //Future<SessionTokens> refreshSession(String idempotencyKey, RefreshSessionRequest refreshSessionRequest) async
    test('test refreshSession', () async {
      // TODO
    });

    //Future revokeAllSessions() async
    test('test revokeAllSessions', () async {
      // TODO
    });

  });
}
