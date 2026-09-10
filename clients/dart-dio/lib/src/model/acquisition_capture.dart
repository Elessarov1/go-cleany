//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'acquisition_capture.g.dart';

/// AcquisitionCapture
///
/// Properties:
/// * [targetPath] - Legacy channel-entry redirect, not a business read-model action.
@BuiltValue()
abstract class AcquisitionCapture implements Built<AcquisitionCapture, AcquisitionCaptureBuilder> {
  /// Legacy channel-entry redirect, not a business read-model action.
  @BuiltValueField(wireName: r'targetPath')
  String get targetPath;

  AcquisitionCapture._();

  factory AcquisitionCapture([void updates(AcquisitionCaptureBuilder b)]) = _$AcquisitionCapture;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AcquisitionCaptureBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AcquisitionCapture> get serializer => _$AcquisitionCaptureSerializer();
}

class _$AcquisitionCaptureSerializer implements PrimitiveSerializer<AcquisitionCapture> {
  @override
  final Iterable<Type> types = const [AcquisitionCapture, _$AcquisitionCapture];

  @override
  final String wireName = r'AcquisitionCapture';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AcquisitionCapture object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'targetPath';
    yield serializers.serialize(
      object.targetPath,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    AcquisitionCapture object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AcquisitionCaptureBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'targetPath':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.targetPath = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AcquisitionCapture deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AcquisitionCaptureBuilder();
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


