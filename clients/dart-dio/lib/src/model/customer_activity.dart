//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/customer_activity_item.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_activity.g.dart';

/// CustomerActivity
///
/// Properties:
/// * [activeAndUpcoming] 
/// * [history] 
@BuiltValue()
abstract class CustomerActivity implements Built<CustomerActivity, CustomerActivityBuilder> {
  @BuiltValueField(wireName: r'activeAndUpcoming')
  BuiltList<CustomerActivityItem> get activeAndUpcoming;

  @BuiltValueField(wireName: r'history')
  BuiltList<CustomerActivityItem> get history;

  CustomerActivity._();

  factory CustomerActivity([void updates(CustomerActivityBuilder b)]) = _$CustomerActivity;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerActivityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerActivity> get serializer => _$CustomerActivitySerializer();
}

class _$CustomerActivitySerializer implements PrimitiveSerializer<CustomerActivity> {
  @override
  final Iterable<Type> types = const [CustomerActivity, _$CustomerActivity];

  @override
  final String wireName = r'CustomerActivity';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerActivity object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'activeAndUpcoming';
    yield serializers.serialize(
      object.activeAndUpcoming,
      specifiedType: const FullType(BuiltList, [FullType(CustomerActivityItem)]),
    );
    yield r'history';
    yield serializers.serialize(
      object.history,
      specifiedType: const FullType(BuiltList, [FullType(CustomerActivityItem)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CustomerActivity object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerActivityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'activeAndUpcoming':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CustomerActivityItem)]),
          ) as BuiltList<CustomerActivityItem>;
          result.activeAndUpcoming.replace(valueDes);
          break;
        case r'history':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CustomerActivityItem)]),
          ) as BuiltList<CustomerActivityItem>;
          result.history.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CustomerActivity deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerActivityBuilder();
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


