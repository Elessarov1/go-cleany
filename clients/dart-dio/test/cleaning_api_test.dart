import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for CleaningApi
void main() {
  final instance = LocoPlaceApi().getCleaningApi();

  group(CleaningApi, () {
    //Future createCleaningOrder(String idempotencyKey, JsonObject body) async
    test('test createCleaningOrder', () async {
      // TODO
    });

  });
}
