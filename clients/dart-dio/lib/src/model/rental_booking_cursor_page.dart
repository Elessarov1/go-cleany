//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_booking.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_booking_cursor_page.g.dart';

/// RentalBookingCursorPage
///
/// Properties:
/// * [items] 
/// * [nextCursor] 
/// * [hasMore] 
@BuiltValue()
abstract class RentalBookingCursorPage implements Built<RentalBookingCursorPage, RentalBookingCursorPageBuilder> {
  @BuiltValueField(wireName: r'items')
  BuiltList<RentalBooking> get items;

  @BuiltValueField(wireName: r'nextCursor')
  String? get nextCursor;

  @BuiltValueField(wireName: r'hasMore')
  bool get hasMore;

  RentalBookingCursorPage._();

  factory RentalBookingCursorPage([void updates(RentalBookingCursorPageBuilder b)]) = _$RentalBookingCursorPage;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalBookingCursorPageBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalBookingCursorPage> get serializer => _$RentalBookingCursorPageSerializer();
}

class _$RentalBookingCursorPageSerializer implements PrimitiveSerializer<RentalBookingCursorPage> {
  @override
  final Iterable<Type> types = const [RentalBookingCursorPage, _$RentalBookingCursorPage];

  @override
  final String wireName = r'RentalBookingCursorPage';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalBookingCursorPage object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'items';
    yield serializers.serialize(
      object.items,
      specifiedType: const FullType(BuiltList, [FullType(RentalBooking)]),
    );
    if (object.nextCursor != null) {
      yield r'nextCursor';
      yield serializers.serialize(
        object.nextCursor,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'hasMore';
    yield serializers.serialize(
      object.hasMore,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalBookingCursorPage object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalBookingCursorPageBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'items':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalBooking)]),
          ) as BuiltList<RentalBooking>;
          result.items.replace(valueDes);
          break;
        case r'nextCursor':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.nextCursor = valueDes;
          break;
        case r'hasMore':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.hasMore = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RentalBookingCursorPage deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalBookingCursorPageBuilder();
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


