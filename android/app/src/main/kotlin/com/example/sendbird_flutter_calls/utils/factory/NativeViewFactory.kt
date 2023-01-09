package com.example.sendbird_flutter_calls.utils.factory

import android.content.Context
import android.util.Log
import com.example.sendbird_flutter_calls.MainActivity
import com.example.sendbird_flutter_calls.utils.view.MagicPlatformView
import com.sendbird.calls.RemoteParticipant
import com.sendbird.calls.SendBirdVideoView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory(private val messenger: BinaryMessenger, private val participant: RemoteParticipant?) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        Log.d("NativeViewFactory", "onCreate")

        val creationParams = args as Map<String?, Any?>?
        Log.i("NativeViewFactory", "creationParams - " + creationParams.toString());

        return MagicPlatformView(context, messenger, viewId, args, participant)
    }
}