import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for TransferApi
void main() {
  final instance = LocoPlaceApi().getTransferApi();

  group(TransferApi, () {
    //Future createTransferBooking(String idempotencyKey, JsonObject body) async
    test('test createTransferBooking', () async {
      // TODO
    });

  });
}
