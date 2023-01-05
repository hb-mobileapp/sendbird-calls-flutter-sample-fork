import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

import 'groupcall_view.dart';
import 'sendbird_group_call_settings.dart';

class SendBirdGroupCallView extends StatefulWidget implements GroupCallView {
  const SendBirdGroupCallView({required this.groupCallOptions});

  final SendBirdGroupCallSettings groupCallOptions;

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

    if (Platform.isAndroid) {
      return PlatformViewLink(
          viewType: 'com.sendbird/flutter_groupcall',
          surfaceFactory: (
            BuildContext context,
            PlatformViewController controller,
          ) {
            return Container();
          },
          onCreatePlatformView: (PlatformViewCreationParams params) {
            return _createAndroidViewController(
              hybridComposition: true,
              id: params.id,
              viewType: 'com.pichillilorenzo/flutter_inappwebview',
              layoutDirection: Directionality.maybeOf(context) ?? TextDirection.rtl,
              creationParams: <String, dynamic>{},
            );
          });
    }

    if (Platform.isIOS) {

    }
    return Container();
  }

  void _inferInitialSettings(SendBirdGroupCallSettings settings) {}
}
