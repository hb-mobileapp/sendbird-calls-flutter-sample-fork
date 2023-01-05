package com.example.sendbird_flutter_calls.view

import android.content.Context
import android.view.View
import com.sendbird.calls.SendBirdVideoView
import io.flutter.plugin.platform.PlatformView

class FlutterSendBirdVideoView(private val context : Context, ) : PlatformView {
    override fun getView(): View? {
        return SendBirdVideoView(context)
    }

    override fun dispose() {
        // TODO(nm-jiwonhae) : implement on flutter sendbird video view displose
    }
}