//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rental_term_type.g.dart';

class RentalTermType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DATE_RANGE')
  static const RentalTermType DATE_RANGE = _$DATE_RANGE;
  @BuiltValueEnumConst(wireName: r'MONTHLY')
  static const RentalTermType MONTHLY = _$MONTHLY;
  @BuiltValueEnumConst(wireName: r'UNKNOWN')
  static const RentalTermType UNKNOWN = _$UNKNOWN;
  @BuiltValueEnumConst(wireName: r'unknown_default_open_api', fallback: true)
  static const RentalTermType unknownDefaultOpenApi = _$unknownDefaultOpenApi;

  static Serializer<RentalTermType> get serializer => _$rentalTermTypeSerializer;

  const RentalTermType._(String name): super(name);

  static BuiltSet<RentalTermType> get values => _$values;
  static RentalTermType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class RentalTermTypeMixin = Object with _$RentalTermTypeMixin;

