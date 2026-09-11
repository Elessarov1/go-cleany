//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_search_criteria.dart';
import 'package:loco_place_api/src/model/rental_search_property.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_search.g.dart';

/// RentalSearch
///
/// Properties:
/// * [searchExecutionId] 
/// * [criteria] 
/// * [calculatedAt] 
/// * [properties] 
/// * [nextCursor] 
/// * [hasMore] 
@BuiltValue()
abstract class RentalSearch implements Built<RentalSearch, RentalSearchBuilder> {
  @BuiltValueField(wireName: r'searchExecutionId')
  String get searchExecutionId;

  @BuiltValueField(wireName: r'criteria')
  RentalSearchCriteria get criteria;

  @BuiltValueField(wireName: r'calculatedAt')
  DateTime get calculatedAt;

  @BuiltValueField(wireName: r'properties')
  BuiltList<RentalSearchProperty> get properties;

  @BuiltValueField(wireName: r'nextCursor')
  String? get nextCursor;

  @BuiltValueField(wireName: r'hasMore')
  bool get hasMore;

  RentalSearch._();

  factory RentalSearch([void updates(RentalSearchBuilder b)]) = _$RentalSearch;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RentalSearchBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RentalSearch> get serializer => _$RentalSearchSerializer();
}

class _$RentalSearchSerializer implements PrimitiveSerializer<RentalSearch> {
  @override
  final Iterable<Type> types = const [RentalSearch, _$RentalSearch];

  @override
  final String wireName = r'RentalSearch';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RentalSearch object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'searchExecutionId';
    yield serializers.serialize(
      object.searchExecutionId,
      specifiedType: const FullType(String),
    );
    yield r'criteria';
    yield serializers.serialize(
      object.criteria,
      specifiedType: const FullType(RentalSearchCriteria),
    );
    yield r'calculatedAt';
    yield serializers.serialize(
      object.calculatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'properties';
    yield serializers.serialize(
      object.properties,
      specifiedType: const FullType(BuiltList, [FullType(RentalSearchProperty)]),
    );
    yield r'nextCursor';
    yield object.nextCursor == null ? null : serializers.serialize(
      object.nextCursor,
      specifiedType: const FullType.nullable(String),
    );
    yield r'hasMore';
    yield serializers.serialize(
      object.hasMore,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RentalSearch object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RentalSearchBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'searchExecutionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.searchExecutionId = valueDes;
          break;
        case r'criteria':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalSearchCriteria),
          ) as RentalSearchCriteria;
          result.criteria.replace(valueDes);
          break;
        case r'calculatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.calculatedAt = valueDes;
          break;
        case r'properties':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RentalSearchProperty)]),
          ) as BuiltList<RentalSearchProperty>;
          result.properties.replace(valueDes);
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
  RentalSearch deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RentalSearchBuilder();
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


