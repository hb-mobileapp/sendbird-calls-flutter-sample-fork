import 'package:flutter/services.dart';

import 'groupcall_view.dart';

class SendbirdGroupCallController{
  late MethodChannel _channel;

  SendbirdGroupCallController(dynamic id, GroupCallView view){
    this._channel =
        MethodChannel('com.sendbird/sendbird_videoview_$id');
    this._channel.setMethodCallHandler((call) async {
      try {
        return await handleMethod(call);
      } on Error catch (e) {
        print(e);
        print(e.stackTrace);
      }
    });
  }
  Future<dynamic> handleMethod(MethodCall call, ) async {
    Map<String, dynamic> args = <String, dynamic>{};

    switch(call.method){
      case "start_camera":
        break;
      case "stop_camera":
        break;
      case "switch_camera":
        break;
      case "mute_microphone":
        break;
      case "unmute_microphone":
        break;
      case "answer_group_call":
        break;
      case "exit_group_call":
        break;
      case "start_group_call":
        break;
      case "start_direct_call":
        break;
      case "stop_direct_call":
        break;
    }
  }
}