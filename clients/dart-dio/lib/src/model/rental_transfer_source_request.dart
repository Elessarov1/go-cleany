//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_transfer_source_request.g.dart';

/// RentalTransferSourceRequest
///
/// Properties:
/// * [bookingId] 
/// * [context] 
@BuiltValue()
abstract class RentalTransferSourceRequest implements Built<RentalTransferSourceRequest, RentalTransferSourceRequestBuilder> {
  @BuiltValueField(wireName: r'bookingId')
  int get bookingId;

  @BuiltValueField(wireName: r'context')
  RentalTransferSourceRequestContextEnum get context;
  // enum contextEnum {  ARRIVAL,  CHECKOUT,  UNKNOWN,  };

  RentalTransferSourceRequest._();

  factory RentalTransferSourceRequest([void updates(RentalTransferSourceRequestBuilder b)]) = _$RentalTransferSourceRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalTransferSourceRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalTransferSourceRequest> get serializer => _$RentalTransferSourceRequestSerializer();
}

class _$RentalTransferSourceRequestSerializer implements PrimitiveSerializer<RentalTransferSourceRequest> {
  @override
  final Iterable<Type> types = const [RentalTransferSourceRequest, _$RentalTransferSourceRequest];

  @override
  final String wireName = r'RentalTransferSourceRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalTransferSourceRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'bookingId';
    yield serializers.serialize(
      object.bookingId,
      specifiedType: const FullType(int),
    );
    yield r'context';
    yield serializers.serialize(
      object.context,
      specifiedType: const FullType(RentalTransferSourceRequestContextEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalTransferSourceRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalTransferSourceRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'bookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.bookingId = valueDes;
          break;
        case r'context':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTransferSourceRequestContextEnum),
          ) as RentalTransferSourceRequestContextEnum;
          result.context = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalTransferSourceRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalTransferSourceRequestBuilder();
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


class RentalTransferSourceRequestContextEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ARRIVAL')
  static const RentalTransferSourceRequestContextEnum ARRIVAL = _$rentalTransferSourceRequestContextEnum_ARRIVAL;
  @BuiltValueEnumConst(wireName: r'CHECKOUT')
  static const RentalTransferSourceRequestContextEnum CHECKOUT = _$rentalTransferSourceRequestContextEnum_CHECKOUT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTransferSourceRequestContextEnum UNKNOWN = _$rentalTransferSourceRequestContextEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTransferSourceRequestContextEnum unknownDefaultOpenApi = _$rentalTransferSourceRequestContextEnum_unknownDefaultOpenApi;

  static Serializer<RentalTransferSourceRequestContextEnum> get serializer => _$rentalTransferSourceRequestContextEnumSerializer;

  const RentalTransferSourceRequestContextEnum._(String name): super(name);

  static BuiltSet<RentalTransferSourceRequestContextEnum> get values => _$rentalTransferSourceRequestContextEnumValues;
  static RentalTransferSourceRequestContextEnum valueOf(String name) => _$rentalTransferSourceRequestContextEnumValueOf(name);
}

