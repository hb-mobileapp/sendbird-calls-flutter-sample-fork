package com.example.sendbird_flutter_calls.plugins

import androidx.annotation.NonNull
import com.example.sendbird_flutter_calls.utils.factory.NativeViewFactory
import com.sendbird.calls.RemoteParticipant
import io.flutter.embedding.engine.plugins.FlutterPlugin

import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding

class PlatformViewPlugin(private val participant: RemoteParticipant?) : FlutterPlugin {
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPluginBinding) {
        val viewFactory = NativeViewFactory(flutterPluginBinding.binaryMessenger, participant)
        flutterPluginBinding.platformViewRegistry.registerViewFactory("customView", viewFactory)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {

    }
}
