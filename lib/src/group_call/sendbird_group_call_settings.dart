class SendBirdGroupCallSettings{

}


class SendBirdGroupCallOptions {
  Map<String, dynamic> toMap() {
    return {};
  }

  static SendBirdGroupCallOptions fromMap(Map<String, dynamic> map) {
    return SendBirdGroupCallOptions();
  }

  SendBirdGroupCallOptions copy() {
    return SendBirdGroupCallOptions.fromMap(this.toMap());
  }

  Map<String, dynamic> toJson() {
    return this.toMap();
  }

  @override
  String toString() {
    return toMap().toString();
  }
}