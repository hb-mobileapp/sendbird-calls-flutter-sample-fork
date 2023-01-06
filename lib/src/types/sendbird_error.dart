import 'sendbird_error_type.dart';

class SendbirdError {
  const SendbirdError({required this.type, required this.message});

  final SendbirdErrorType type;
  final String message;
}