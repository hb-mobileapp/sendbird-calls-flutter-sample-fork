import 'package:flutter/services.dart';

class AndroidSendBirdGroupCallController{
  AndroidSendBirdGroupCallController({required MethodChannel channel}){
    this._channel = _channel;
  }

  late MethodChannel _channel;

  Future<bool> startGroupCalling(String roomId) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("roomId", () => roomId);
    return await _channel.invokeMethod('start_group_call', args);
  }

  Future<bool> stopGroupCalling() async {
    Map<String, dynamic> args = <String, dynamic>{};
    return await _channel.invokeMethod('stop_group_call', args);
  }

  Future<bool> answerGroupCall() async {
    Map<String, dynamic> args = <String, dynamic>{};
    return await _channel.invokeMethod('answer_group_call', args);
  }
}