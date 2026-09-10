//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'referral_summary.g.dart';

/// ReferralSummary
///
/// Properties:
/// * [referralCode] 
/// * [availableRewards] 
/// * [referralProgramUnlocked] 
@BuiltValue()
abstract class ReferralSummary implements Built<ReferralSummary, ReferralSummaryBuilder> {
  @BuiltValueField(wireName: r'referralCode')
  String? get referralCode;

  @BuiltValueField(wireName: r'availableRewards')
  int get availableRewards;

  @BuiltValueField(wireName: r'referralProgramUnlocked')
  bool get referralProgramUnlocked;

  ReferralSummary._();

  factory ReferralSummary([void updates(ReferralSummaryBuilder b)]) = _$ReferralSummary;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReferralSummaryBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReferralSummary> get serializer => _$ReferralSummarySerializer();
}

class _$ReferralSummarySerializer implements PrimitiveSerializer<ReferralSummary> {
  @override
  final Iterable<Type> types = const [ReferralSummary, _$ReferralSummary];

  @override
  final String wireName = r'ReferralSummary';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReferralSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.referralCode != null) {
      yield r'referralCode';
      yield serializers.serialize(
        object.referralCode,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'availableRewards';
    yield serializers.serialize(
      object.availableRewards,
      specifiedType: const FullType(int),
    );
    yield r'referralProgramUnlocked';
    yield serializers.serialize(
      object.referralProgramUnlocked,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReferralSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReferralSummaryBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'referralCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.referralCode = valueDes;
          break;
        case r'availableRewards':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.availableRewards = valueDes;
          break;
        case r'referralProgramUnlocked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.referralProgramUnlocked = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ReferralSummary deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReferralSummaryBuilder();
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


