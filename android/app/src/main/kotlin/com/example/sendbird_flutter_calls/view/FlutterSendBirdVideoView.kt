package com.example.sendbird_flutter_calls.view

import android.content.Context
import android.view.View
import com.example.sendbird_flutter_calls.SendbirdFlutterPlugin
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdVideoView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class FlutterSendBirdVideoView : PlatformView {
    private val context : Context

    constructor(context : Context){
        this.context = context
    }

    override fun getView(): View? {
        return SendBirdVideoView(context)
    }

    override fun dispose() {
        // TODO(nm-jiwonhae) : implement on flutter sendbird video view dispose
    }
}