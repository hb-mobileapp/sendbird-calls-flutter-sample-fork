package com.example.sendbird_flutter_calls.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sendbird_flutter_calls.R;
import com.sendbird.calls.SendBirdVideoView;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class TestSurfaceViewSecond implements PlatformView, MethodChannel.MethodCallHandler {
    public TestSurfaceViewSecond(Context context, int id, Map<String, Object> creationParams, SendBirdVideoView videoView) {
//        super(context);
        initView(context, videoView);
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void dispose() {

    }

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Paint paint;

    private View view;
    private SendBirdVideoView tempView;

    public void updateView(SendBirdVideoView videoView) {
        if (view != null) {
//            view.getRootView().
            SendBirdVideoView myView = view.findViewById(R.id.participant_sendbird_video_view);
            myView = videoView;
        }
    }

    private void initView(Context context, SendBirdVideoView videoView) {
        if (videoView != null) {
            SendBirdVideoView myView = view.findViewById(R.id.participant_sendbird_video_view);
            myView = videoView;
        }
        view = LayoutInflater.from(context).inflate(R.layout.linear_temp_layout, null);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
//            case "ping": {
//                result.success(null);
//                break;
//            }
//            default:
//                result.notImplemented();
//                break;
        }
    }
}
