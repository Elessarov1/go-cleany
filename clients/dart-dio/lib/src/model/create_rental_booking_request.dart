//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:loco_place_api/src/model/date.dart';
import 'package:loco_place_api/src/model/rental_term_type.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_rental_booking_request.g.dart';

/// CreateRentalBookingRequest
///
/// Properties:
/// * [propertyId] 
/// * [termType] 
/// * [checkInDate] 
/// * [checkOutDate] 
/// * [months] 
/// * [guests] 
/// * [phone] 
/// * [comment] 
/// * [expectedTotalPrice] - Legacy optimistic-price input; response money always uses the Money object.
/// * [expectedCurrency] 
/// * [searchExecutionId] 
@BuiltValue()
abstract class CreateRentalBookingRequest implements Built<CreateRentalBookingRequest, CreateRentalBookingRequestBuilder> {
  @BuiltValueField(wireName: r'propertyId')
  int get propertyId;

  @BuiltValueField(wireName: r'termType')
  RentalTermType get termType;
  // enum termTypeEnum {  DATE_RANGE,  MONTHLY,  UNKNOWN,  };

  @BuiltValueField(wireName: r'checkInDate')
  Date get checkInDate;

  @BuiltValueField(wireName: r'checkOutDate')
  Date? get checkOutDate;

  @BuiltValueField(wireName: r'months')
  int? get months;

  @BuiltValueField(wireName: r'guests')
  int get guests;

  @BuiltValueField(wireName: r'phone')
  String get phone;

  @BuiltValueField(wireName: r'comment')
  String? get comment;

  /// Legacy optimistic-price input; response money always uses the Money object.
  @BuiltValueField(wireName: r'expectedTotalPrice')
  num get expectedTotalPrice;

  @BuiltValueField(wireName: r'expectedCurrency')
  String get expectedCurrency;

  @BuiltValueField(wireName: r'searchExecutionId')
  String? get searchExecutionId;

  CreateRentalBookingRequest._();

  factory CreateRentalBookingRequest([void updates(CreateRentalBookingRequestBuilder b)]) = _$CreateRentalBookingRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateRentalBookingRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateRentalBookingRequest> get serializer => _$CreateRentalBookingRequestSerializer();
}

class _$CreateRentalBookingRequestSerializer implements PrimitiveSerializer<CreateRentalBookingRequest> {
  @override
  final Iterable<Type> types = const [CreateRentalBookingRequest, _$CreateRentalBookingRequest];

  @override
  final String wireName = r'CreateRentalBookingRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateRentalBookingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'propertyId';
    yield serializers.serialize(
      object.propertyId,
      specifiedType: const FullType(int),
    );
    yield r'termType';
    yield serializers.serialize(
      object.termType,
      specifiedType: const FullType(RentalTermType),
    );
    yield r'checkInDate';
    yield serializers.serialize(
      object.checkInDate,
      specifiedType: const FullType(Date),
    );
    if (object.checkOutDate != null) {
      yield r'checkOutDate';
      yield serializers.serialize(
        object.checkOutDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.months != null) {
      yield r'months';
      yield serializers.serialize(
        object.months,
        specifiedType: const FullType.nullable(int),
      );
    }
    yield r'guests';
    yield serializers.serialize(
      object.guests,
      specifiedType: const FullType(int),
    );
    yield r'phone';
    yield serializers.serialize(
      object.phone,
      specifiedType: const FullType(String),
    );
    if (object.comment != null) {
      yield r'comment';
      yield serializers.serialize(
        object.comment,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'expectedTotalPrice';
    yield serializers.serialize(
      object.expectedTotalPrice,
      specifiedType: const FullType(num),
    );
    yield r'expectedCurrency';
    yield serializers.serialize(
      object.expectedCurrency,
      specifiedType: const FullType(String),
    );
    if (object.searchExecutionId != null) {
      yield r'searchExecutionId';
      yield serializers.serialize(
        object.searchExecutionId,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateRentalBookingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateRentalBookingRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'propertyId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.propertyId = valueDes;
          break;
        case r'termType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RentalTermType),
          ) as RentalTermType;
          result.termType = valueDes;
          break;
        case r'checkInDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.checkInDate = valueDes;
          break;
        case r'checkOutDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.checkOutDate = valueDes;
          break;
        case r'months':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.months = valueDes;
          break;
        case r'guests':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.guests = valueDes;
          break;
        case r'phone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phone = valueDes;
          break;
        case r'comment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.comment = valueDes;
          break;
        case r'expectedTotalPrice':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.expectedTotalPrice = valueDes;
          break;
        case r'expectedCurrency':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.expectedCurrency = valueDes;
          break;
        case r'searchExecutionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.searchExecutionId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateRentalBookingRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateRentalBookingRequestBuilder();
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


