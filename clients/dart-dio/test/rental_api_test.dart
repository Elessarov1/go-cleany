import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for RentalApi
void main() {
  final instance = LocoPlaceApi().getRentalApi();

  group(RentalApi, () {
    //Future createRentalBooking(String idempotencyKey, JsonObject body) async
    test('test createRentalBooking', () async {
      // TODO
    });

  });
}
