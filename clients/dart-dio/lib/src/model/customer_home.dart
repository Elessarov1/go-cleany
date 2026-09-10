//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/customer_home_primary_action.dart';
import 'package:loco_place_api/src/model/customer_home_repeat_opportunity.dart';
import 'package:loco_place_api/src/model/customer_activity_item.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_home.g.dart';

/// CustomerHome
///
/// Properties:
/// * [hasActivity] 
/// * [activeTransaction] 
/// * [activeTransactionCount] 
/// * [primaryAction] 
/// * [repeatOpportunity] 
@BuiltValue()
abstract class CustomerHome implements Built<CustomerHome, CustomerHomeBuilder> {
  @BuiltValueField(wireName: r'hasActivity')
  bool get hasActivity;

  @BuiltValueField(wireName: r'activeTransaction')
  CustomerActivityItem? get activeTransaction;

  @BuiltValueField(wireName: r'activeTransactionCount')
  int get activeTransactionCount;

  @BuiltValueField(wireName: r'primaryAction')
  CustomerHomePrimaryAction? get primaryAction;

  @BuiltValueField(wireName: r'repeatOpportunity')
  CustomerHomeRepeatOpportunity? get repeatOpportunity;

  CustomerHome._();

  factory CustomerHome([void updates(CustomerHomeBuilder b)]) = _$CustomerHome;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerHomeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerHome> get serializer => _$CustomerHomeSerializer();
}

class _$CustomerHomeSerializer implements PrimitiveSerializer<CustomerHome> {
  @override
  final Iterable<Type> types = const [CustomerHome, _$CustomerHome];

  @override
  final String wireName = r'CustomerHome';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerHome object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'hasActivity';
    yield serializers.serialize(
      object.hasActivity,
      specifiedType: const FullType(bool),
    );
    if (object.activeTransaction != null) {
      yield r'activeTransaction';
      yield serializers.serialize(
        object.activeTransaction,
        specifiedType: const FullType.nullable(CustomerActivityItem),
      );
    }
    yield r'activeTransactionCount';
    yield serializers.serialize(
      object.activeTransactionCount,
      specifiedType: const FullType(int),
    );
    if (object.primaryAction != null) {
      yield r'primaryAction';
      yield serializers.serialize(
        object.primaryAction,
        specifiedType: const FullType.nullable(CustomerHomePrimaryAction),
      );
    }
    if (object.repeatOpportunity != null) {
      yield r'repeatOpportunity';
      yield serializers.serialize(
        object.repeatOpportunity,
        specifiedType: const FullType.nullable(CustomerHomeRepeatOpportunity),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CustomerHome object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerHomeBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'hasActivity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.hasActivity = valueDes;
          break;
        case r'activeTransaction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CustomerActivityItem),
          ) as CustomerActivityItem?;
          if (valueDes == null) continue;
          result.activeTransaction.replace(valueDes);
          break;
        case r'activeTransactionCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.activeTransactionCount = valueDes;
          break;
        case r'primaryAction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CustomerHomePrimaryAction),
          ) as CustomerHomePrimaryAction?;
          if (valueDes == null) continue;
          result.primaryAction.replace(valueDes);
          break;
        case r'repeatOpportunity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(CustomerHomeRepeatOpportunity),
          ) as CustomerHomeRepeatOpportunity?;
          if (valueDes == null) continue;
          result.repeatOpportunity.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CustomerHome deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerHomeBuilder();
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


