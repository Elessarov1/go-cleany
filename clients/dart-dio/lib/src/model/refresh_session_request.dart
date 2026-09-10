//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'refresh_session_request.g.dart';

/// RefreshSessionRequest
///
/// Properties:
/// * [refreshToken] 
@BuiltValue()
abstract class RefreshSessionRequest implements Built<RefreshSessionRequest, RefreshSessionRequestBuilder> {
  @BuiltValueField(wireName: r'refreshToken')
  String get refreshToken;

  RefreshSessionRequest._();

  factory RefreshSessionRequest([void updates(RefreshSessionRequestBuilder b)]) = _$RefreshSessionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RefreshSessionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RefreshSessionRequest> get serializer => _$RefreshSessionRequestSerializer();
}

class _$RefreshSessionRequestSerializer implements PrimitiveSerializer<RefreshSessionRequest> {
  @override
  final Iterable<Type> types = const [RefreshSessionRequest, _$RefreshSessionRequest];

  @override
  final String wireName = r'RefreshSessionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RefreshSessionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'refreshToken';
    yield serializers.serialize(
      object.refreshToken,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RefreshSessionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RefreshSessionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'refreshToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.refreshToken = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RefreshSessionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RefreshSessionRequestBuilder();
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


