//package com.example.sendbird_flutter_calls.utils.view
//
//import android.content.Context
//import android.graphics.Color
//import android.view.View
//import android.widget.RelativeLayout
//import android.widget.TextView
//import io.flutter.plugin.common.MethodCall
//import io.flutter.plugin.common.MethodChannel
//import io.flutter.plugin.common.MethodChannel.MethodCallHandler
//import io.flutter.plugin.platform.PlatformView
//
//internal class VideoCallView(context: Context, id: Int, creationParams: Map<String?, Any?>?) :
//    PlatformView, MethodCallHandler {
////    private val videoCallContainer: RelativeLayout
//
//    override fun getView(): View {
//        return textView
//    }
//
//    override fun dispose() {}
//
//    init {
//        textView = TextView(context)
//        textView.textSize = 40f
//        textView.setBackgroundColor(Color.rgb(255, 255, 255))
//        textView.text = "Rendered on a native Android view (id: $id)"
//    }
//
//    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
//        TODO("Not yet implemented")
//    }
//}