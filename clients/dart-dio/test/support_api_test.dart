import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for SupportApi
void main() {
  final instance = LocoPlaceApi().getSupportApi();

  group(SupportApi, () {
    //Future<SupportCase> createSupportCase(CreateSupportCaseRequest createSupportCaseRequest) async
    test('test createSupportCase', () async {
      // TODO
    });

    //Future<TransactionSupport> getTransactionSupport(String service, int sourceEntityId) async
    test('test getTransactionSupport', () async {
      // TODO
    });

    //Future<TransactionSupport> submitTransactionFeedback(CreateTransactionFeedbackRequest createTransactionFeedbackRequest) async
    test('test submitTransactionFeedback', () async {
      // TODO
    });

  });
}
