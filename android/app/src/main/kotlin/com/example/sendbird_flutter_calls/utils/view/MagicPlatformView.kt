package com.example.sendbird_flutter_calls.utils.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.sendbird_flutter_calls.R
import com.sendbird.calls.*
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.handler.RoomHandler
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class MagicPlatformView internal constructor(
    private val context: Context?,
    messenger: BinaryMessenger,
    id: Int,
    args: Any?,
    participant: RemoteParticipant?
) : PlatformView, MethodChannel.MethodCallHandler {

    private val methodChannel: MethodChannel = MethodChannel(messenger, "com.sendbird.calls/method")
    private val view: View =
        LayoutInflater.from(context).inflate(R.layout.linear_temp_layout, null)

    init {
        val localParticipantVideoView: SendBirdVideoView =
            view.findViewById(R.id.participant_sendbird_video_view)
        participant?.videoView = localParticipantVideoView
        methodChannel.setMethodCallHandler(this)
    }

    override fun dispose() {

    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "test_call" -> {
                Log.e("MagicPlatformView", "here")
            }

            "start_direct_call" -> {
                Log.d("MagicPlatformView", "start_direct_call")

                SendBirdCall.fetchRoomById("2c797c78-41c0-429e-8d9c-475a6adf83c5", object :
                    RoomHandler {
                    override fun onResult(room: Room?, e: SendBirdException?) {
                        if (room == null || e != null) {
                            Log.e("MagicPlatformView", "fetchRoomById/error - $e")
                            // Handle error.
                            return
                        }
                        Log.d("MagicPlatformView", "testestst")

                        val enterParams = EnterParams()
                            .setAudioEnabled(true)
                            .setVideoEnabled(true)

                        room.enter(enterParams, object :
                            CompletionHandler {
                            override fun onResult(e: SendBirdException?) {
                                if (e != null) {
                                    // Handle error.
                                    Log.e("MagicPlatformView", "error - $e");
                                }

                                // User has successfully entered `room`.
                                val localParticipantVideoView: SendBirdVideoView =
                                    view.findViewById(R.id.local_sendbird_video_view)
                                room.localParticipant?.videoView = localParticipantVideoView

                                room.addListener(
                                    "2c797c78-41c0-429e-8d9c-475a6adf83c5",
                                    object : RoomListener {
                                        override fun onAudioDeviceChanged(
                                            currentAudioDevice: AudioDevice?,
                                            availableAudioDevices: Set<AudioDevice>
                                        ) {

                                        }

                                        override fun onError(
                                            e: SendBirdException,
                                            participant: Participant?
                                        ) {
//                                                TODO("Not yet implemented")
                                        }

                                        override fun onRemoteAudioSettingsChanged(participant: RemoteParticipant) {
//                                                TODO("Not yet implemented")
                                        }

                                        override fun onRemoteParticipantEntered(participant: RemoteParticipant) {
                                        }

                                        override fun onRemoteParticipantExited(participant: RemoteParticipant) {
//                                                TODO("Not yet implemented")
                                        }

                                        override fun onRemoteParticipantStreamStarted(
                                            participant: RemoteParticipant
                                        ) {
                                            Log.d(
                                                "MagicPlatformView",
                                                "onRemoteParticipantStreamStarted"
                                            )

                                            val remoteParticipantVideoView: SendBirdVideoView =
                                                view.findViewById(R.id.participant_sendbird_video_view)
                                            participant.videoView = remoteParticipantVideoView
                                        }

                                        override fun onRemoteVideoSettingsChanged(participant: RemoteParticipant) {
                                            Log.d(
                                                "MagicPlatformView",
                                                "onRemoteVideoSettingsChanged"
                                            )
//                                                TODO("Not yet implemented")
                                        }

                                    })
                            }
                        })
                    }
                })
            }

            else -> result.notImplemented()
        }
    }

    private fun sendFromNative(text: String) {
        methodChannel.invokeMethod("sendFromNative", text)
    }

    override fun getView(): View {
        return view
    }

}
