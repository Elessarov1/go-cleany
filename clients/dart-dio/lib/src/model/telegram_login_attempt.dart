//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'telegram_login_attempt.g.dart';

/// TelegramLoginAttempt
///
/// Properties:
/// * [attemptId] 
/// * [botUrl] 
/// * [verifier] 
/// * [expiresAt] 
@BuiltValue()
abstract class TelegramLoginAttempt implements Built<TelegramLoginAttempt, TelegramLoginAttemptBuilder> {
  @BuiltValueField(wireName: r'attemptId')
  String get attemptId;

  @BuiltValueField(wireName: r'botUrl')
  String get botUrl;

  @BuiltValueField(wireName: r'verifier')
  String get verifier;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  TelegramLoginAttempt._();

  factory TelegramLoginAttempt([void updates(TelegramLoginAttemptBuilder b)]) = _$TelegramLoginAttempt;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TelegramLoginAttemptBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TelegramLoginAttempt> get serializer => _$TelegramLoginAttemptSerializer();
}

class _$TelegramLoginAttemptSerializer implements PrimitiveSerializer<TelegramLoginAttempt> {
  @override
  final Iterable<Type> types = const [TelegramLoginAttempt, _$TelegramLoginAttempt];

  @override
  final String wireName = r'TelegramLoginAttempt';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TelegramLoginAttempt object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'attemptId';
    yield serializers.serialize(
      object.attemptId,
      specifiedType: const FullType(String),
    );
    yield r'botUrl';
    yield serializers.serialize(
      object.botUrl,
      specifiedType: const FullType(String),
    );
    yield r'verifier';
    yield serializers.serialize(
      object.verifier,
      specifiedType: const FullType(String),
    );
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TelegramLoginAttempt object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TelegramLoginAttemptBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'attemptId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.attemptId = valueDes;
          break;
        case r'botUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.botUrl = valueDes;
          break;
        case r'verifier':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.verifier = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TelegramLoginAttempt deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TelegramLoginAttemptBuilder();
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


