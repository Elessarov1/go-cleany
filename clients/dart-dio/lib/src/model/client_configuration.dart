//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'client_configuration.g.dart';

/// ClientConfiguration
///
/// Properties:
/// * [environment] 
/// * [iosApplicationId] 
/// * [androidApplicationId] 
/// * [minimumIosVersion] 
/// * [minimumAndroidVersion] 
/// * [apiRevision] 
@BuiltValue()
abstract class ClientConfiguration implements Built<ClientConfiguration, ClientConfigurationBuilder> {
  @BuiltValueField(wireName: r'environment')
  String get environment;

  @BuiltValueField(wireName: r'iosApplicationId')
  String get iosApplicationId;

  @BuiltValueField(wireName: r'androidApplicationId')
  String get androidApplicationId;

  @BuiltValueField(wireName: r'minimumIosVersion')
  String get minimumIosVersion;

  @BuiltValueField(wireName: r'minimumAndroidVersion')
  String get minimumAndroidVersion;

  @BuiltValueField(wireName: r'apiRevision')
  String get apiRevision;

  ClientConfiguration._();

  factory ClientConfiguration([void updates(ClientConfigurationBuilder b)]) = _$ClientConfiguration;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ClientConfigurationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ClientConfiguration> get serializer => _$ClientConfigurationSerializer();
}

class _$ClientConfigurationSerializer implements PrimitiveSerializer<ClientConfiguration> {
  @override
  final Iterable<Type> types = const [ClientConfiguration, _$ClientConfiguration];

  @override
  final String wireName = r'ClientConfiguration';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ClientConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'environment';
    yield serializers.serialize(
      object.environment,
      specifiedType: const FullType(String),
    );
    yield r'iosApplicationId';
    yield serializers.serialize(
      object.iosApplicationId,
      specifiedType: const FullType(String),
    );
    yield r'androidApplicationId';
    yield serializers.serialize(
      object.androidApplicationId,
      specifiedType: const FullType(String),
    );
    yield r'minimumIosVersion';
    yield serializers.serialize(
      object.minimumIosVersion,
      specifiedType: const FullType(String),
    );
    yield r'minimumAndroidVersion';
    yield serializers.serialize(
      object.minimumAndroidVersion,
      specifiedType: const FullType(String),
    );
    yield r'apiRevision';
    yield serializers.serialize(
      object.apiRevision,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ClientConfiguration object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ClientConfigurationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'environment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.environment = valueDes;
          break;
        case r'iosApplicationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.iosApplicationId = valueDes;
          break;
        case r'androidApplicationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.androidApplicationId = valueDes;
          break;
        case r'minimumIosVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.minimumIosVersion = valueDes;
          break;
        case r'minimumAndroidVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.minimumAndroidVersion = valueDes;
          break;
        case r'apiRevision':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.apiRevision = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ClientConfiguration deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ClientConfigurationBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}


