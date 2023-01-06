package com.example.sendbird_flutter_calls

import FlutterSendbirdVideoViewFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger

class PlatformViewPlugin : FlutterPlugin {
    lateinit var messenger: BinaryMessenger

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        messenger = binding.binaryMessenger
        binding.platformViewRegistry.registerViewFactory("com.sendbird/video_call_view", FlutterSendbirdVideoViewFactory())
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        // TODO(nm-jiwonhae) : Not yet implemented
    }
}