//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'session_tokens.g.dart';

/// SessionTokens
///
/// Properties:
/// * [sessionId] 
/// * [tokenType] 
/// * [accessToken] 
/// * [refreshToken] 
/// * [accessExpiresAt] 
/// * [refreshExpiresAt] 
/// * [absoluteExpiresAt] 
@BuiltValue()
abstract class SessionTokens implements Built<SessionTokens, SessionTokensBuilder> {
  @BuiltValueField(wireName: r'sessionId')
  String get sessionId;

  @BuiltValueField(wireName: r'tokenType')
  SessionTokensTokenTypeEnum get tokenType;
  // enum tokenTypeEnum {  Bearer,  };

  @BuiltValueField(wireName: r'accessToken')
  String get accessToken;

  @BuiltValueField(wireName: r'refreshToken')
  String get refreshToken;

  @BuiltValueField(wireName: r'accessExpiresAt')
  DateTime get accessExpiresAt;

  @BuiltValueField(wireName: r'refreshExpiresAt')
  DateTime get refreshExpiresAt;

  @BuiltValueField(wireName: r'absoluteExpiresAt')
  DateTime get absoluteExpiresAt;

  SessionTokens._();

  factory SessionTokens([void updates(SessionTokensBuilder b)]) = _$SessionTokens;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SessionTokensBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SessionTokens> get serializer => _$SessionTokensSerializer();
}

class _$SessionTokensSerializer implements PrimitiveSerializer<SessionTokens> {
  @override
  final Iterable<Type> types = const [SessionTokens, _$SessionTokens];

  @override
  final String wireName = r'SessionTokens';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SessionTokens object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sessionId';
    yield serializers.serialize(
      object.sessionId,
      specifiedType: const FullType(String),
    );
    yield r'tokenType';
    yield serializers.serialize(
      object.tokenType,
      specifiedType: const FullType(SessionTokensTokenTypeEnum),
    );
    yield r'accessToken';
    yield serializers.serialize(
      object.accessToken,
      specifiedType: const FullType(String),
    );
    yield r'refreshToken';
    yield serializers.serialize(
      object.refreshToken,
      specifiedType: const FullType(String),
    );
    yield r'accessExpiresAt';
    yield serializers.serialize(
      object.accessExpiresAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'refreshExpiresAt';
    yield serializers.serialize(
      object.refreshExpiresAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'absoluteExpiresAt';
    yield serializers.serialize(
      object.absoluteExpiresAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SessionTokens object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SessionTokensBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sessionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.sessionId = valueDes;
          break;
        case r'tokenType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SessionTokensTokenTypeEnum),
          ) as SessionTokensTokenTypeEnum;
          result.tokenType = valueDes;
          break;
        case r'accessToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.accessToken = valueDes;
          break;
        case r'refreshToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.refreshToken = valueDes;
          break;
        case r'accessExpiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.accessExpiresAt = valueDes;
          break;
        case r'refreshExpiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.refreshExpiresAt = valueDes;
          break;
        case r'absoluteExpiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.absoluteExpiresAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SessionTokens deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SessionTokensBuilder();
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


class SessionTokensTokenTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'Bearer')
  static const SessionTokensTokenTypeEnum bearer = _$sessionTokensTokenTypeEnum_bearer;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const SessionTokensTokenTypeEnum unknownDefaultOpenApi = _$sessionTokensTokenTypeEnum_unknownDefaultOpenApi;

  static Serializer<SessionTokensTokenTypeEnum> get serializer => _$sessionTokensTokenTypeEnumSerializer;

  const SessionTokensTokenTypeEnum._(String name): super(name);

  static BuiltSet<SessionTokensTokenTypeEnum> get values => _$sessionTokensTokenTypeEnumValues;
  static SessionTokensTokenTypeEnum valueOf(String name) => _$sessionTokensTokenTypeEnumValueOf(name);
}

