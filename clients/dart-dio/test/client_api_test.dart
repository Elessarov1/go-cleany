import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for ClientApi
void main() {
  final instance = LocoPlaceApi().getClientApi();

  group(ClientApi, () {
    //Future<ClientConfiguration> getClientConfiguration() async
    test('test getClientConfiguration', () async {
      // TODO
    });

  });
}
