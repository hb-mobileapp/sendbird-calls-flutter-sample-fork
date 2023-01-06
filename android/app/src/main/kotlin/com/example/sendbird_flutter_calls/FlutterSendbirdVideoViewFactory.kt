import android.content.Context
import android.view.View
import com.example.sendbird_flutter_calls.FlutterSendbirdVideoViewController
import com.example.sendbird_flutter_calls.PlatformViewPlugin
import com.example.sendbird_flutter_calls.view.FlutterSendBirdVideoView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class FlutterSendbirdVideoViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE){
    lateinit var controller : FlutterSendbirdVideoViewController

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any>?
        controller = FlutterSendbirdVideoViewController(context, PlatformViewPlugin())
        return FlutterSendBirdVideoView(context, viewId, creationParams)
    }
}