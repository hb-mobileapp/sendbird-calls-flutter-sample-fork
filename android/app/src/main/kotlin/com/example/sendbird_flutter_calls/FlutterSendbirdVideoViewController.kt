package com.example.sendbird_flutter_calls

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class FlutterSendbirdVideoViewController {

    constructor(plugin : SendbirdFlutterPlugin){
        val methodChannel = MethodChannel(plugin.messenger, "")
        methodChannel.setMethodCallHandler(methodCallHandler)
    }

    private val methodCallHandler = object : MethodChannel.MethodCallHandler{
        override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
            when(call.method){
                "start_camera" -> return
                "stop_camera" -> return
                "switch_camera" -> return
                "mute_microphone" -> return
                "unmute_microphone" -> return
                "unmute_microphone" -> return
                "" -> return
                "" -> return
            }
        }
    }

}