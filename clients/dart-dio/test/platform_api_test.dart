import 'package:test/test.dart';
import 'package:loco_place_api/loco_place_api.dart';


/// tests for PlatformApi
void main() {
  final instance = LocoPlaceApi().getPlatformApi();

  group(PlatformApi, () {
    //Future<BuiltList<PlatformServiceState>> getPlatformServices() async
    test('test getPlatformServices', () async {
      // TODO
    });

  });
}
