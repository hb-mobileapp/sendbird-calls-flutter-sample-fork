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
    private val TAG = MainActivity::class.java.simpleName

    private val METHOD_CHANNEL_NAME = "com.sendbird.calls/method"
    private val ERROR_CODE = "Sendbird Calls"
    private var methodChannel: MethodChannel? = null
    private var directCall: DirectCall? = null

    private val ROOM_ID = "3a35bc15-9658-4cff-aee0-5d8d0611e2fd"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Setup
        setupChannels(this, flutterEngine.dartExecutor.binaryMessenger)
    }

    override fun onDestroy() {
        disposeChannels()
        super.onDestroy()
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
                    Log.d(TAG, "test - $ROOM_ID")

                    if (requestPermissions()) {
                        SendBirdCall.fetchRoomById(ROOM_ID, object : RoomHandler {
                            override fun onResult(room: Room?, e: SendBirdException?) {
                                if (room == null || e != null) {
                                    Log.e(TAG, "fetchRoomById/error - $e");
                                    // Handle error.
                                    return
                                }

                                // `room` with the identifier `ROOM_ID` is fetched from Sendbird Server.

                                Log.d(TAG, "here")
                                val enterParams = EnterParams()
                                    .setAudioEnabled(true)
                                    .setVideoEnabled(true)
                                room.enter(enterParams, object : CompletionHandler {
                                    override fun onResult(e: SendBirdException?) {
                                        if (e != null) {
                                            // Handle error.
                                            Log.e(TAG, "error - $e");
                                        }

                                        // User has successfully entered `room`.
                                    }
                                })
                            }
                        })
                    } else {
                        Log.e(TAG, "error!!")
                    }
                }
                "answer_direct_call"->{
                    directCall?.accept(AcceptParams())
                }
                "end_direct_call" -> {
                    // End a call
                    directCall?.end();
                    result.success(true);
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun Activity.requestPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT
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
