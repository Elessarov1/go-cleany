//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:loco_place_api/src/model/rental_transfer_source_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'transfer_quote_request.g.dart';

/// TransferQuoteRequest
///
/// Properties:
/// * [direction] 
/// * [airportId] 
/// * [vehicleTypeId] 
/// * [rentalSource] 
/// * [benefit] 
@BuiltValue()
abstract class TransferQuoteRequest implements Built<TransferQuoteRequest, TransferQuoteRequestBuilder> {
  @BuiltValueField(wireName: r'direction')
  TransferQuoteRequestDirectionEnum get direction;
  // enum directionEnum {  TO_AIRPORT,  FROM_AIRPORT,  UNKNOWN,  };

  @BuiltValueField(wireName: r'airportId')
  int get airportId;

  @BuiltValueField(wireName: r'vehicleTypeId')
  int get vehicleTypeId;

  @BuiltValueField(wireName: r'rentalSource')
  RentalTransferSourceRequest? get rentalSource;

  @BuiltValueField(wireName: r'benefit')
  TransferQuoteRequestBenefitEnum? get benefit;
  // enum benefitEnum {  RENTAL_FIRST_TRANSFER,  UNKNOWN,  ,  };

  TransferQuoteRequest._();

  factory TransferQuoteRequest([void updates(TransferQuoteRequestBuilder b)]) = _$TransferQuoteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TransferQuoteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TransferQuoteRequest> get serializer => _$TransferQuoteRequestSerializer();
}

class _$TransferQuoteRequestSerializer implements PrimitiveSerializer<TransferQuoteRequest> {
  @override
  final Iterable<Type> types = const [TransferQuoteRequest, _$TransferQuoteRequest];

  @override
  final String wireName = r'TransferQuoteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TransferQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'direction';
    yield serializers.serialize(
      object.direction,
      specifiedType: const FullType(TransferQuoteRequestDirectionEnum),
    );
    yield r'airportId';
    yield serializers.serialize(
      object.airportId,
      specifiedType: const FullType(int),
    );
    yield r'vehicleTypeId';
    yield serializers.serialize(
      object.vehicleTypeId,
      specifiedType: const FullType(int),
    );
    if (object.rentalSource != null) {
      yield r'rentalSource';
      yield serializers.serialize(
        object.rentalSource,
        specifiedType: const FullType.nullable(RentalTransferSourceRequest),
      );
    }
    if (object.benefit != null) {
      yield r'benefit';
      yield serializers.serialize(
        object.benefit,
        specifiedType: const FullType.nullable(TransferQuoteRequestBenefitEnum),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    TransferQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TransferQuoteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'direction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(TransferQuoteRequestDirectionEnum),
          ) as TransferQuoteRequestDirectionEnum;
          result.direction = valueDes;
          break;
        case r'airportId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.airportId = valueDes;
          break;
        case r'vehicleTypeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.vehicleTypeId = valueDes;
          break;
        case r'rentalSource':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RentalTransferSourceRequest),
          ) as RentalTransferSourceRequest?;
          if (valueDes == null) continue;
          result.rentalSource.replace(valueDes);
          break;
        case r'benefit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(TransferQuoteRequestBenefitEnum),
          ) as TransferQuoteRequestBenefitEnum?;
          if (valueDes == null) continue;
          result.benefit = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TransferQuoteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TransferQuoteRequestBuilder();
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


class TransferQuoteRequestDirectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TO_AIRPORT')
  static const TransferQuoteRequestDirectionEnum TO_AIRPORT = _$transferQuoteRequestDirectionEnum_TO_AIRPORT;
  @BuiltValueEnumConst(wireName: r'FROM_AIRPORT')
  static const TransferQuoteRequestDirectionEnum FROM_AIRPORT = _$transferQuoteRequestDirectionEnum_FROM_AIRPORT;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferQuoteRequestDirectionEnum UNKNOWN = _$transferQuoteRequestDirectionEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferQuoteRequestDirectionEnum unknownDefaultOpenApi = _$transferQuoteRequestDirectionEnum_unknownDefaultOpenApi;

  static Serializer<TransferQuoteRequestDirectionEnum> get serializer => _$transferQuoteRequestDirectionEnumSerializer;

  const TransferQuoteRequestDirectionEnum._(String name): super(name);

  static BuiltSet<TransferQuoteRequestDirectionEnum> get values => _$transferQuoteRequestDirectionEnumValues;
  static TransferQuoteRequestDirectionEnum valueOf(String name) => _$transferQuoteRequestDirectionEnumValueOf(name);
}

class TransferQuoteRequestBenefitEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RENTAL_FIRST_TRANSFER')
  static const TransferQuoteRequestBenefitEnum RENTAL_FIRST_TRANSFER = _$transferQuoteRequestBenefitEnum_RENTAL_FIRST_TRANSFER;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const TransferQuoteRequestBenefitEnum UNKNOWN = _$transferQuoteRequestBenefitEnum_UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const TransferQuoteRequestBenefitEnum unknownDefaultOpenApi = _$transferQuoteRequestBenefitEnum_unknownDefaultOpenApi;

  static Serializer<TransferQuoteRequestBenefitEnum> get serializer => _$transferQuoteRequestBenefitEnumSerializer;

  const TransferQuoteRequestBenefitEnum._(String name): super(name);

  static BuiltSet<TransferQuoteRequestBenefitEnum> get values => _$transferQuoteRequestBenefitEnumValues;
  static TransferQuoteRequestBenefitEnum valueOf(String name) => _$transferQuoteRequestBenefitEnumValueOf(name);
}

