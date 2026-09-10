import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for AccountApi
void main() {
  final instance = LocoPlaceApi().getAccountApi();

  group(AccountApi, () {
    //Future confirmAccountDeletion(String id, SensitiveProof sensitiveProof) async
    test('test confirmAccountDeletion', () async {
      // TODO
    });

    //Future confirmIdentityLink(String id, SensitiveProof sensitiveProof) async
    test('test confirmIdentityLink', () async {
      // TODO
    });

    //Future<ReauthenticationChallenge> createAccountDeletionRequest() async
    test('test createAccountDeletionRequest', () async {
      // TODO
    });

    //Future<IdentityLinkAttempt> createIdentityLink(CreateIdentityLinkRequest createIdentityLinkRequest) async
    test('test createIdentityLink', () async {
      // TODO
    });

    //Future<GetAccountIdentities200Response> getAccountIdentities() async
    test('test getAccountIdentities', () async {
      // TODO
    });

    //Future<NotificationPreferences> getNotificationPreferences() async
    test('test getNotificationPreferences', () async {
      // TODO
    });

    //Future registerCommunicationEndpoint(RegisterCommunicationEndpointRequest registerCommunicationEndpointRequest) async
    test('test registerCommunicationEndpoint', () async {
      // TODO
    });

    //Future unlinkIdentity(int identityId, SensitiveProof sensitiveProof) async
    test('test unlinkIdentity', () async {
      // TODO
    });

    //Future unregisterCommunicationEndpoint() async
    test('test unregisterCommunicationEndpoint', () async {
      // TODO
    });

    //Future<NotificationPreferences> updateNotificationPreferences(NotificationPreferences notificationPreferences) async
    test('test updateNotificationPreferences', () async {
      // TODO
    });

    //Future verifyIdentityLink(String id, VerifyIdentityLinkRequest verifyIdentityLinkRequest) async
    test('test verifyIdentityLink', () async {
      // TODO
    });

  });
}
