import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:sendbird_flutter_calls/src/types/sendbird_error.dart';

import 'groupcall_view.dart';
import 'sendbird_group_call_controller.dart';
import 'sendbird_group_call_settings.dart';

class SendBirdGroupCallView extends StatefulWidget implements GroupCallView {
  const SendBirdGroupCallView({required this.groupCallOptions, required this.gestureRecognizers, this.onReceivedError});

  final SendBirdGroupCallSettings groupCallOptions;
  final Set<Factory<OneSequenceGestureRecognizer>>? gestureRecognizers;
  final void Function(SendbirdGroupCallController controller, SendbirdError error)? onReceivedError;

  @override
  _SendBirdGroupCallViewState createState() => _SendBirdGroupCallViewState();
}

class _SendBirdGroupCallViewState extends State<SendBirdGroupCallView> {
  AndroidViewController _createAndroidViewController({
    required bool hybridComposition,
    required int id,
    required String viewType,
    required TextDirection layoutDirection,
    required Map<String, dynamic> creationParams,
  }) {
    if (hybridComposition) {
      return PlatformViewsService.initExpensiveAndroidView(
        id: id,
        viewType: viewType,
        layoutDirection: layoutDirection,
        creationParams: creationParams,
        creationParamsCodec: const StandardMessageCodec(),
      );
    }
    return PlatformViewsService.initSurfaceAndroidView(
      id: id,
      viewType: viewType,
      layoutDirection: layoutDirection,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }

  @override
  Widget build(BuildContext context) {
    _inferInitialSettings(widget.groupCallOptions);

    // TODO (nm-jiwonhae) : implement creationg params if required
    final Map<String, dynamic> creationParams = <String, dynamic>{};

    if (Platform.isAndroid) {
      return PlatformViewLink(
          viewType: 'com.sendbird/video_call_view',
          surfaceFactory: (
            BuildContext context,
            PlatformViewController controller,
          ) {
            return AndroidViewSurface(
              controller: controller as AndroidViewController,
              gestureRecognizers: widget.gestureRecognizers ??
                  const <Factory<OneSequenceGestureRecognizer>>{},
              hitTestBehavior: PlatformViewHitTestBehavior.opaque,
            );
          },
          onCreatePlatformView: (PlatformViewCreationParams params) {
            return _createAndroidViewController(
              hybridComposition: true,
              id: params.id,
              viewType: 'com.sendbird/flutter_groupcall',
              layoutDirection: Directionality.maybeOf(context) ?? TextDirection.rtl,
              creationParams: creationParams,
            )..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)..create();
          });
    }

    if (Platform.isIOS) {

    }
    return Container();
  }

  SendBirdGroupCallOptions _inferInitialSettings(SendBirdGroupCallSettings settings) {
    // TODO(nm-jiwonhae) : create settings
    return SendBirdGroupCallOptions();
  }
}
