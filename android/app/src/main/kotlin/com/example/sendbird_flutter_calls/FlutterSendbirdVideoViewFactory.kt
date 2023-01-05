import android.content.Context
import com.example.sendbird_flutter_calls.view.FlutterSendBirdVideoView
import com.sendbird.calls.SendBirdVideoView
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class FlutterSendbirdVideoViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE){
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any>?
        return FlutterSendBirdVideoView(context!!)
    }

}