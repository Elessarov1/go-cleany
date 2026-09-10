//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_transfer_context_option.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_transfer_context.g.dart';

/// RentalTransferContext
///
/// Properties:
/// * [rentalBookingId] 
/// * [transferFlowAvailable] 
/// * [options] 
@BuiltValue()
abstract class RentalTransferContext implements Built<RentalTransferContext, RentalTransferContextBuilder> {
  @BuiltValueField(wireName: r'rentalBookingId')
  int get rentalBookingId;

  @BuiltValueField(wireName: r'transferFlowAvailable')
  bool get transferFlowAvailable;

  @BuiltValueField(wireName: r'options')
  BuiltList<RentalTransferContextOption> get options;

  RentalTransferContext._();

  factory RentalTransferContext([void updates(RentalTransferContextBuilder b)]) = _$RentalTransferContext;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalTransferContextBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalTransferContext> get serializer => _$RentalTransferContextSerializer();
}

class _$RentalTransferContextSerializer implements PrimitiveSerializer<RentalTransferContext> {
  @override
  final Iterable<Type> types = const [RentalTransferContext, _$RentalTransferContext];

  @override
  final String wireName = r'RentalTransferContext';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalTransferContext object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'rentalBookingId';
    yield serializers.serialize(
      object.rentalBookingId,
      specifiedType: const FullType(int),
    );
    yield r'transferFlowAvailable';
    yield serializers.serialize(
      object.transferFlowAvailable,
      specifiedType: const FullType(bool),
    );
    yield r'options';
    yield serializers.serialize(
      object.options,
      specifiedType: const FullType(BuiltList, [FullType(RentalTransferContextOption)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalTransferContext object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalTransferContextBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'rentalBookingId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rentalBookingId = valueDes;
          break;
        case r'transferFlowAvailable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.transferFlowAvailable = valueDes;
          break;
        case r'options':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalTransferContextOption)]),
          ) as BuiltList<RentalTransferContextOption>;
          result.options.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalTransferContext deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalTransferContextBuilder();
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


