package com.example.sendbird_flutter_calls

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sendbird_flutter_calls.plugins.PlatformViewPlugin
import com.example.sendbird_flutter_calls.utils.factory.NativeViewFactory

import com.sendbird.calls.*
import com.sendbird.calls.handler.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import java.util.*


class MainActivity : FlutterActivity() {
    private val TAG = "MainActivity"

    private val METHOD_CHANNEL_NAME = "com.sendbird.calls/method"
    private val ERROR_CODE = "Sendbird Calls"
    private var methodChannel: MethodChannel? = null
    private var directCall: DirectCall? = null

    private val ROOM_ID = "2c797c78-41c0-429e-8d9c-475a6adf83c5"

    private var remoteMember: RemoteParticipant? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        GeneratedPluginRegistrant.registerWith(flutterEngine)
        flutterEngine.plugins.add(PlatformViewPlugin(null))
        // Setup
        setupChannels(this, flutterEngine.dartExecutor.binaryMessenger, flutterEngine)
    }

    override fun onDestroy() {
        disposeChannels()
        super.onDestroy()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupChannels(
        context: Context,
        messenger: BinaryMessenger,
        flutterEngine: FlutterEngine
    ) {
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        requestPermissions()
        methodChannel!!.setMethodCallHandler { call, result ->
            when (call.method) {
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
                            initSendbird(context, appId, userId) { successful ->
                                if (!successful) {
                                    result.error(
                                        ERROR_CODE,
                                        "Failed init",
                                        "Problem initializing Sendbird. Check for valid app_id"
                                    )
                                } else {
                                    result.success(true)
                                }
                            }
                        }
                    }
                }

                "start_direct_call" -> {
                    Log.d(TAG, "test - $ROOM_ID")
                }

                "answer_direct_call" -> {
                    directCall?.accept(AcceptParams())
                }

                "end_direct_call" -> {
                    // End a call
                    directCall?.end();
                    result.success(true);
                }

                "test_call" -> {
                    Log.d(TAG, "check Main Activity")
                }

                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun getStream(participant: RemoteParticipant, flutterEngine: FlutterEngine) {
        Log.d(TAG, "getStream - ${participant.participantId}")




//        flutterEngine.platformViewsController.attach(context, "custonView", flutterEngine.dartExecutor).get = participant.videoView
//        flutterEngine.platformViewsController.registry.registerViewFactory(
//            "customViewSecond",
//            NativeViewFactory(flutterEngine.dartExecutor.binaryMessenger)
//        )


//        flutterEngine.platformViewsController.getPlatformViewById(0).apply {
//            this?.findViewById<SendBirdVideoView>(R.id.participant_sendbird_video_view) = participant.videoView
//        }
//        Log.d(TAG, "test - ${flutterEngine.plugins.toString()}")
//        methodChannel?.invokeMethod("stream_start") {
//            Log.d(TAG, "getStream!! ${participant.participantId}")
//        }
    }

    private fun initSendbird(
        context: Context,
        appId: String,
        userId: String,
        callback: (Boolean) -> Unit
    ) {
        // Initialize SendBirdCall instance to use APIs in your app.
        if (SendBirdCall.init(context, appId)) {
            // Initialization successful

            // Add event listeners
            SendBirdCall.addListener(UUID.randomUUID().toString(), object : SendBirdCallListener() {
                override fun onRinging(call: DirectCall) {
                    methodChannel?.invokeMethod("direct_call_received") {
                    }

                    val ongoingCallCount = SendBirdCall.ongoingCallCount
                    if (ongoingCallCount >= 2) {
                        call.end()
                        return
                    }

                    call.setListener(object : DirectCallListener() {
                        override fun onEstablished(call: DirectCall) {}

                        override fun onConnected(call: DirectCall) {
                            methodChannel?.invokeMethod("direct_call_connected") {
                            }
                        }

                        override fun onEnded(call: DirectCall) {
                            val ongoingCallCount = SendBirdCall.ongoingCallCount
                            if (ongoingCallCount == 0) {
//                                CallService.stopService(context)
                            }
                            methodChannel?.invokeMethod("direct_call_ended") {
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

    private fun disposeChannels() {
        methodChannel!!.setMethodCallHandler(null)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun Activity.requestPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                1
            )
            return false
        }

        return true
    }
}
