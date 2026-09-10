//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_first_card_request.g.dart';

/// RentalFirstCardRequest
///
/// Properties:
/// * [durationMs] 
@BuiltValue()
abstract class RentalFirstCardRequest implements Built<RentalFirstCardRequest, RentalFirstCardRequestBuilder> {
  @BuiltValueField(wireName: r'durationMs')
  int get durationMs;

  RentalFirstCardRequest._();

  factory RentalFirstCardRequest([void updates(RentalFirstCardRequestBuilder b)]) = _$RentalFirstCardRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalFirstCardRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalFirstCardRequest> get serializer => _$RentalFirstCardRequestSerializer();
}

class _$RentalFirstCardRequestSerializer implements PrimitiveSerializer<RentalFirstCardRequest> {
  @override
  final Iterable<Type> types = const [RentalFirstCardRequest, _$RentalFirstCardRequest];

  @override
  final String wireName = r'RentalFirstCardRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalFirstCardRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'durationMs';
    yield serializers.serialize(
      object.durationMs,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalFirstCardRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalFirstCardRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'durationMs':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationMs = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalFirstCardRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalFirstCardRequestBuilder();
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


