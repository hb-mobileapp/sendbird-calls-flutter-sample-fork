package com.example.sendbird_flutter_calls

import android.content.Context
import androidx.annotation.NonNull
import com.sendbird.calls.*
import com.sendbird.calls.SendBirdCall.addListener
import com.sendbird.calls.SendBirdCall.dial
import com.sendbird.calls.handler.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import java.util.*


class MainActivity: FlutterActivity() {
    private val TAG = "TAG"
    private val METHOD_CHANNEL_NAME = "com.sendbird.calls/method"
    private val ERROR_CODE = "Sendbird Calls"
    private var methodChannel: MethodChannel? = null
    private var directCall: DirectCall? = null
    private var groupCallRoom : Room? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Setup
        setupChannels(this, flutterEngine.dartExecutor.binaryMessenger)
    }

    override fun onDestroy() {
        disposeChannels()
        super.onDestroy()
    }

    fun fetchRoomById(roomId : String){
        if(roomId.isNullOrEmpty()){
            return
        }

        SendBirdCall.fetchRoomById(roomId, object : RoomHandler{
            override fun onResult(room: Room?, e: SendBirdException?) {
                TODO("Not yet implemented")
            }
        })
    }


    fun enterGroupCall(roomId : String, isAudioEnabled : Boolean, isVideoEnabled : Boolean){
        groupCallRoom = SendBirdCall.getCachedRoomById(roomId)
        groupCallRoom?.addListener(TAG, RoomListenerImpl())
        val enterParams = EnterParams().setAudioEnabled(isAudioEnabled).setVideoEnabled(isVideoEnabled)
        groupCallRoom?.enter(enterParams, object : CompletionHandler{
            override fun onResult(e: SendBirdException?) {
                // TODO (nm-jiwonhae) : handle send bird exception

            }
        })
    }

    fun exitGroupCall(){
        groupCallRoom?.let{
            try{
                it.exit()
            }catch(e : SendBirdException){
                // TODO (nm-jiwonhae) : handle exit exception
            }

        }
    }

    fun muteMicrophone(){
        groupCallRoom?.localParticipant?.muteMicrophone()
    }

    fun unmuteMicrophone(){
        groupCallRoom?.localParticipant?.unmuteMicrophone()
    }

    fun startLocalVideo(){
        groupCallRoom?.localParticipant?.startVideo()
    }

    fun stopLocalVideo(){
        groupCallRoom?.localParticipant?.stopVideo()
    }

    fun switchCamera(){
        groupCallRoom?.localParticipant?.switchCamera(object : CompletionHandler{
            override fun onResult(e: SendBirdException?) {
                // TODO(nm-jiwonhae) : handle switch camera exception
            }
        })
    }

    fun selectAudioDevice(audioDevice : AudioDevice){
        groupCallRoom?.selectAudioDevice(audioDevice, object : CompletionHandler{
            override fun onResult(e: SendBirdException?) {
                // TODO (nm-jiwonhae) : select audio device
            }
        })
    }

    private fun setupChannels(context:Context, messenger: BinaryMessenger) {
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        methodChannel!!.setMethodCallHandler { call, result ->
            when(call.method) {
                "init" -> {
                    val appId: String? = call.argument("app_id")
                    val userId: String? = call.argument("user_id")
                    when {
                        appId == null -> {
                            result.error(ERROR_CODE, "Failed Init", "Missing app_id")
                        }
                        userId == null -> {
                            result.error(ERROR_CODE, "Failed Init", "Missing user_id")
                        }
                        else -> {
                            initSendbird(context, appId!!, userId!!) { successful ->
                                if (!successful) {
                                    result.error(ERROR_CODE, "Failed init", "Problem initializing Sendbird. Check for valid app_id")
                                } else {
                                    result.success(true)
                                }
                            }
                        }
                    }
                }
                "start_direct_call" -> {
                    val calleeId: String? = call.argument("callee_id")
                    if (calleeId == null) {
                        result.error(ERROR_CODE, "Failed call", "Missing callee_id")
                    }
                    var params = DialParams(calleeId!!)
                    params.setCallOptions(CallOptions())
                    directCall = dial(params, object : DialHandler {
                        override fun onResult(call: DirectCall?, e: SendBirdException?) {
                            if (e != null) {
                                result.error(ERROR_CODE, "Failed call", e.message)
                                return
                            }
                            result.success(true)
                        }
                    })
                    directCall?.setListener(object : DirectCallListener() {
                        override fun onEstablished(call: DirectCall) {}
                        override fun onConnected(call: DirectCall) {}
                        override fun onEnded(call: DirectCall) {}
                    })
                }
                "answer_direct_call"->{
                    directCall?.accept(AcceptParams())
                }
                "end_direct_call" -> {
                    // End a call
                    directCall?.end();
                    result.success(true);
                }
                "start_group_call"->{
                 // TODO(nm-jiwonahe) : implement stat group calling
                }
                "stop_group_call"->{
                    // TODO(nm-jiwonahe) : implement end group calling
                }
                "answer_group_call"->{
                    // TODO(nm-jiwonahe) : implement end group calling
                }

                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun initSendbird(context: Context, appId: String , userId: String, callback: (Boolean)->Unit){
        // Initialize SendBirdCall instance to use APIs in your app.
        if(SendBirdCall.init(context, appId)){
            // Initialization successful

                // Add event listeners
            SendBirdCall.addListener(UUID.randomUUID().toString(), object: SendBirdCallListener() {
                override fun onRinging(call: DirectCall) {

                    methodChannel?.invokeMethod("direct_call_received"){
                    }

                    val ongoingCallCount = SendBirdCall.ongoingCallCount
                    if (ongoingCallCount >= 2) {
                        call.end()
                        return
                    }

                    call.setListener(object : DirectCallListener() {
                        override fun onEstablished(call: DirectCall) {}

                        override fun onConnected(call: DirectCall) {
                            methodChannel?.invokeMethod("direct_call_connected"){
                            }
                        }

                        override fun onEnded(call: DirectCall) {
                            val ongoingCallCount = SendBirdCall.ongoingCallCount
                            if (ongoingCallCount == 0) {
//                                CallService.stopService(context)
                            }
                            methodChannel?.invokeMethod("direct_call_ended"){
                            }
                        }
                        override fun onRemoteAudioSettingsChanged(call: DirectCall) {}

                    })
                }
            })
        }

        // The USER_ID below should be unique to your Sendbird application.
        var params = AuthenticateParams(userId)

        SendBirdCall.authenticate(params, object : AuthenticateHandler {
            override fun onResult(user: User?, e: SendBirdException?) {
                    if (e == null) {
                        // The user has been authenticated successfully and is connected to Sendbird server.
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            })
    }

    private fun disposeChannels(){
        methodChannel!!.setMethodCallHandler(null)
    }

    inner class RoomListenerImpl : RoomListener{
        override fun onAudioDeviceChanged(
            currentAudioDevice: AudioDevice?,
            availableAudioDevices: Set<AudioDevice>
        ) {
            // TODO (nm-jiwonhae) : implement audio device change
        }

        override fun onError(e: SendBirdException, participant: Participant?) {

            // TODO (nm-jiwonhae) : implement onError
        }

        override fun onRemoteAudioSettingsChanged(participant: RemoteParticipant) {

            // TODO (nm-jiwonhae) : implement onRemoteAudioSettingsChanged
        }

        override fun onRemoteParticipantEntered(participant: RemoteParticipant) {

            // TODO (nm-jiwonhae) : implement onRemoteParticipantEntered
        }

        override fun onRemoteParticipantExited(participant: RemoteParticipant) {

            // TODO (nm-jiwonhae) : implement onRemoteParticipantExited
        }

        override fun onRemoteParticipantStreamStarted(participant: RemoteParticipant) {
            // TODO (nm-jiwonhae) : implement onRemoteParticipantStreamStarted
        }

        override fun onRemoteVideoSettingsChanged(participant: RemoteParticipant) {
            // TODO (nm-jiwonhae) : implement onRemoteVideoSettingsChanged
        }

    }
}
