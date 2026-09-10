import 'dart:io';

import 'package:loco_place_api/loco_place_api.dart';

Future<void> main() async {
  final baseUrl = Platform.environment['LOCO_API_BASE_URL'];
  if (baseUrl == null || baseUrl.isEmpty) {
    stderr.writeln('LOCO_API_BASE_URL is required');
    exitCode = 2;
    return;
  }

  final api = LocoPlaceApi(basePathOverride: baseUrl);
  final response = await api.getClientApi().getClientConfiguration();
  final configuration = response.data;
  if (configuration == null || configuration.apiRevision.isEmpty) {
    throw StateError('Public client configuration is incomplete');
  }
  if (!configuration.iosApplicationId.startsWith('com.locoplace.app') ||
      !configuration.androidApplicationId.startsWith('com.locoplace.app')) {
    throw StateError('Unexpected application identifiers');
  }
  stdout.writeln('Loco Place API ${configuration.apiRevision} (${configuration.environment})');

  final services = (await api.getPlatformApi().getPlatformServices()).data;
  final rentalConfiguration = (await api.getRentalApi().getRentalConfiguration()).data;
  if (services == null || services.isEmpty || rentalConfiguration == null) {
    throw StateError('Public catalog or Rental configuration is incomplete');
  }

  final accessToken = Platform.environment['LOCO_ACCESS_TOKEN'];
  if (accessToken == null || accessToken.isEmpty) {
    stdout.writeln('Public configuration smoke passed; authenticated reads were skipped.');
    return;
  }

  api.setBearerAuth('bearerAuth', accessToken);
  final accountApi = api.getAccountApi();
  final identities = (await accountApi.getAccountIdentities()).data;
  final home = (await accountApi.getCustomerHome()).data;
  final activity = (await accountApi.getCustomerActivity()).data;
  final notifications = (await accountApi.getNotifications(size: 20)).data;
  final cleaningConfiguration = (await api.getCleaningApi().getCleaningConfiguration()).data;
  final transferConfiguration = (await api.getTransferApi().getTransferConfiguration()).data;
  if (identities == null ||
      home == null ||
      activity == null ||
      notifications == null ||
      cleaningConfiguration == null ||
      transferConfiguration == null) {
    throw StateError('Authenticated public read models are incomplete');
  }
  stdout.writeln(
    'Authenticated reads passed: ${identities.identities.length} identities, '
    '${activity.activeAndUpcoming.length + activity.history.length} activities, '
    '${notifications.items.length} notifications, all three service configurations.',
  );
}
