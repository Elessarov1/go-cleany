//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'exchange_telegram_login_attempt_request.g.dart';

/// ExchangeTelegramLoginAttemptRequest
///
/// Properties:
/// * [verifier] 
/// * [clientType] 
@BuiltValue()
abstract class ExchangeTelegramLoginAttemptRequest implements Built<ExchangeTelegramLoginAttemptRequest, ExchangeTelegramLoginAttemptRequestBuilder> {
  @BuiltValueField(wireName: r'verifier')
  String get verifier;

  @BuiltValueField(wireName: r'clientType')
  ExchangeTelegramLoginAttemptRequestClientTypeEnum get clientType;
  // enum clientTypeEnum {  IOS,  ANDROID,  UNKNOWN,  };

  ExchangeTelegramLoginAttemptRequest._();

  factory ExchangeTelegramLoginAttemptRequest([void updates(ExchangeTelegramLoginAttemptRequestBuilder b)]) = _$ExchangeTelegramLoginAttemptRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ExchangeTelegramLoginAttemptRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ExchangeTelegramLoginAttemptRequest> get serializer => _$ExchangeTelegramLoginAttemptRequestSerializer();
}

class _$ExchangeTelegramLoginAttemptRequestSerializer implements PrimitiveSerializer<ExchangeTelegramLoginAttemptRequest> {
  @override
  final Iterable<Type> types = const [ExchangeTelegramLoginAttemptRequest, _$ExchangeTelegramLoginAttemptRequest];

  @override
  final String wireName = r'ExchangeTelegramLoginAttemptRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ExchangeTelegramLoginAttemptRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'verifier';
    yield serializers.serialize(
      object.verifier,
      specifiedType: const FullType(String),
    );
    yield r'clientType';
    yield serializers.serialize(
      object.clientType,
      specifiedType: const FullType(ExchangeTelegramLoginAttemptRequestClientTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ExchangeTelegramLoginAttemptRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ExchangeTelegramLoginAttemptRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'verifier':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.verifier = valueDes;
          break;
        case r'clientType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ExchangeTelegramLoginAttemptRequestClientTypeEnum),
          ) as ExchangeTelegramLoginAttemptRequestClientTypeEnum;
          result.clientType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ExchangeTelegramLoginAttemptRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ExchangeTelegramLoginAttemptRequestBuilder();
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


class ExchangeTelegramLoginAttemptRequestClientTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'IOS')
  static const ExchangeTelegramLoginAttemptRequestClientTypeEnum IOS = _$exchangeTelegramLoginAttemptRequestClientTypeEnum_IOS;
  @BuiltValueEnumConst(wireName: r'ANDROID')
  static const ExchangeTelegramLoginAttemptRequestClientTypeEnum ANDROID = _$exchangeTelegramLoginAttemptRequestClientTypeEnum_ANDROID;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const ExchangeTelegramLoginAttemptRequestClientTypeEnum UNKNOWN = _$exchangeTelegramLoginAttemptRequestClientTypeEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const ExchangeTelegramLoginAttemptRequestClientTypeEnum unknownDefaultOpenApi = _$exchangeTelegramLoginAttemptRequestClientTypeEnum_unknownDefaultOpenApi;

  static Serializer<ExchangeTelegramLoginAttemptRequestClientTypeEnum> get serializer => _$exchangeTelegramLoginAttemptRequestClientTypeEnumSerializer;

  const ExchangeTelegramLoginAttemptRequestClientTypeEnum._(String name): super(name);

  static BuiltSet<ExchangeTelegramLoginAttemptRequestClientTypeEnum> get values => _$exchangeTelegramLoginAttemptRequestClientTypeEnumValues;
  static ExchangeTelegramLoginAttemptRequestClientTypeEnum valueOf(String name) => _$exchangeTelegramLoginAttemptRequestClientTypeEnumValueOf(name);
}

