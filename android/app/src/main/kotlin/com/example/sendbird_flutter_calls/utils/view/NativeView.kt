package com.example.sendbird_flutter_calls.utils.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import com.sendbird.calls.SendBirdVideoView
import io.flutter.plugin.platform.PlatformView

internal class NativeView (
    context: Context,
    id: Int,
    creationParams: Map<String?, Any?>?,
    private val test: SendBirdVideoView?,
) :
    PlatformView {
    private val textView: TextView
    private val testSecond : SendBirdVideoView? = null

    override fun getView(): View {
        if (test != null) {
            return test
        }
        return textView
    }

    override fun dispose() {}

    init {
        Log.e("NativeView", test.toString())
        textView = TextView(context)
        textView.textSize = 40f
        textView.setBackgroundColor(Color.rgb(255, 255, 255))
        textView.text = "Rendered on a native Android view (id: $id)"
    }
}
