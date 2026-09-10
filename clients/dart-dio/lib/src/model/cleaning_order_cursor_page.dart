//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/cleaning_order.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cleaning_order_cursor_page.g.dart';

/// CleaningOrderCursorPage
///
/// Properties:
/// * [items] 
/// * [nextCursor] 
/// * [hasMore] 
@BuiltValue()
abstract class CleaningOrderCursorPage implements Built<CleaningOrderCursorPage, CleaningOrderCursorPageBuilder> {
  @BuiltValueField(wireName: r'items')
  BuiltList<CleaningOrder> get items;

  @BuiltValueField(wireName: r'nextCursor')
  String? get nextCursor;

  @BuiltValueField(wireName: r'hasMore')
  bool get hasMore;

  CleaningOrderCursorPage._();

  factory CleaningOrderCursorPage([void updates(CleaningOrderCursorPageBuilder b)]) = _$CleaningOrderCursorPage;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CleaningOrderCursorPageBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CleaningOrderCursorPage> get serializer => _$CleaningOrderCursorPageSerializer();
}

class _$CleaningOrderCursorPageSerializer implements PrimitiveSerializer<CleaningOrderCursorPage> {
  @override
  final Iterable<Type> types = const [CleaningOrderCursorPage, _$CleaningOrderCursorPage];

  @override
  final String wireName = r'CleaningOrderCursorPage';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CleaningOrderCursorPage object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'items';
    yield serializers.serialize(
      object.items,
      specifiedType: const FullType(BuiltList, [FullType(CleaningOrder)]),
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
    CleaningOrderCursorPage object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CleaningOrderCursorPageBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'items':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CleaningOrder)]),
          ) as BuiltList<CleaningOrder>;
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
  CleaningOrderCursorPage deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CleaningOrderCursorPageBuilder();
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


