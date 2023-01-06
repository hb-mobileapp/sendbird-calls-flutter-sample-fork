package com.example.sendbird_flutter_calls

import android.content.Context
import com.sendbird.calls.*
import com.sendbird.calls.handler.AuthenticateHandler
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.handler.RoomHandler
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class FlutterSendbirdVideoViewController {
    private val context : Context
    private val methodChannel : MethodChannel

    constructor(context : Context, plugin : PlatformViewPlugin){
        this.context = context
        methodChannel = MethodChannel(plugin.messenger, "")
        methodChannel.setMethodCallHandler(methodCallHandler)
    }

    private fun onError(e : SendBirdException){
        val obj: MutableMap<String, Any> = HashMap()
        obj["code"] = e.code
        obj["message"] = e.message ?: ""
        methodChannel.invokeMethod("onSendbirdError", obj)
    }

    private fun initSendbird(applicationId : String){
        SendBirdCall.init(context, applicationId)
    }

    private fun authenticate(userId : String, accessToken : String?){
        val authenticateParams = AuthenticateParams(userId).setAccessToken(accessToken)
        SendBirdCall.authenticate(authenticateParams, object : AuthenticateHandler{
            override fun onResult(user: User?, e: SendBirdException?) {
                e?.let{ return onError(it) }

                if(user == null || e!= null){
                    // TODO(nm-jiwonhae): notify flutter app of the error
                    onError(e!!)
                }
            }
        })
    }

    private fun createRoom(roomType : RoomType = RoomType.SMALL_ROOM_FOR_VIDEO){
        val params = RoomParams(roomType)
        SendBirdCall.createRoom(params, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                e?.let{ return onError(it) }

                if(room == null || e != null){
                    // TODO(nm-jiwonhae): notify flutter app of the error
                    onError(e!!)
                }
            }
        })
    }

    private fun enterRoom(roomId : String, enableVideo : Boolean = true, enableAudio : Boolean = true){
        SendBirdCall.fetchRoomById(roomId, object : RoomHandler{
            override fun onResult(room: Room?, e: SendBirdException?) {
                e?.let{ return onError(it) }

                if(room == null){
                    // TODO(nm-jiwonhae): notify flutter app of the error
                    return
                }
            }
        })

        val room = SendBirdCall.getCachedRoomById(roomId)
        val enterParams = EnterParams().apply {
            setAudioEnabled(enableAudio)
            setVideoEnabled(enableVideo)
        }

        room?.enter(enterParams, object : CompletionHandler{
            override fun onResult(e: SendBirdException?) {
                e?.let{ return onError(it) }
            }
        })
    }

    private fun exitRoom(){
        SendBirdCall.
    }

    private val methodCallHandler = object : MethodChannel.MethodCallHandler{
        override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
            when(call.method){
                "init" -> return
                "start_camera" -> return
                "stop_camera" -> return
                "switch_camera" -> return
                "mute_microphone" -> return
                "unmute_microphone" -> return
                "unmute_microphone" -> return
                "create_room" -> {
                    val callParams = call.arguments as Map<String, String>

                }

                "enter_room" -> {
                    val callParams = call.arguments as Map<String, String>
                    val roomId = callParams.get("roomId")!!
                    val enableVideo : Boolean = callParams.getOrDefault("enableVideo", "true") == "true"
                    val enableAudio : Boolean = callParams.getOrDefault("enableAudio", "true") == "true"
                    val enterResult = enterRoom(roomId = roomId, enableAudio = enableAudio, enableVideo = enableVideo)
                    result.success(true)
                }

                "exit_room" ->{

                }
            }
        }
    }

}