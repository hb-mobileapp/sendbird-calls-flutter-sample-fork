import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'sendbird_channels.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _calleeController = TextEditingController();

  bool _isCalleeAvailable = false;
  bool _areCalling = false;
  bool _areConnected = false;
  bool _isCallActive = false;
  bool _areReceivingCall = false;

  String? callerId;
  String? callerNickname;
  SendbirdChannels? channels;

  // This is used in the platform side to register the view.
  String viewType = 'customView';

  // Pass parameters to the platform side.
  Map<String, dynamic> creationParams = <String, dynamic>{};

  final String appId = '90C24A19-CF1B-442F-8992-7FAA5FF4317D';
  final String userId = '12345';

  bool _checkCallInit = false;

  @override
  void initState() {
    channels = SendbirdChannels(directCallReceived: ((userId, nickname) {
      setState(() {
        callerId = userId;
        callerNickname = nickname;
        _areReceivingCall = true;
        _checkCallInit = false;
      });
    }), directCallConnected: () {
      setState(() {
        _areCalling = false;
        _areReceivingCall = false;
        _isCallActive = true;
        _checkCallInit = true;
      });
    }, directCallEnded: () {
      setState(() {
        _isCallActive = false;
        _areCalling = false;
        _areReceivingCall = false;
        callerId = null;
        callerNickname = null;
      });
    }, onError: ((message) {
      print("home_screen.dart: initState: SendbirdChannels: onError: message: $message");
    }), onLog: ((message) {
      print("home_screen.dart: initState: SendbirdChannels onLog: message: $message");
    }));
    channels
        ?.initSendbird(
          appId: appId,
          userId: userId,
        )
        .then((value) => setState(() {
              _areConnected = value;
            }));

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Center(child: Text('Sendbird Calls'))),
      body: Container(
        padding: EdgeInsets.fromLTRB(20, 10, 20, 10),
        child: Column(children: [
          SizedBox(
            height: 300,
            width: 300,
            child: androidView(),
          ),
          // SizedBox(
          //   height: 150,
          //   width: 300,
          //   child: androidViewTwo(),
          // ),
          Row(children: [
            SizedBox(width: 240, child: Text("Connection status for $userId:")),
            Expanded(child: statusField()),
          ]),
          Container(height: 20),
          // statusField(),
          Row(children: [
            SizedBox(width: 80, child: Text("Calling")),
            Container(width: 10),
            SizedBox(width: 150, child: calleeIdField(_calleeController)),
            Container(width: 10),
            Expanded(
                child: _isCalleeAvailable
                    ? _areConnected && !_isCallActive && !_areCalling
                        ? callButton(_calleeController)
                        : Container()
                    : Container()),
          ]),
          Container(height: 20),
          Row(children: [
            SizedBox(width: 80, child: Text('Receiving')),
            Container(width: 10),
            SizedBox(
              width: 150,
              child: callerNickname != null
                  ? Text('$callerNickname')
                  : callerId != null
                      ? Text("$callerId")
                      : Text("<No incoming calls>"),
            ),
            Expanded(child: receivingCallButton()),
            Container(height: 20),
          ]),
          Container(height: 10),
          _isCallActive || _areCalling ? hangupButton() : Container(),
        ]),
      ),
    );
  }

  Widget androidView() {
    return PlatformViewLink(
        surfaceFactory: (BuildContext context, PlatformViewController controller) {
          return AndroidViewSurface(
            controller: controller as AndroidViewController,
            gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
            hitTestBehavior: PlatformViewHitTestBehavior.opaque,
          );
        },
        onCreatePlatformView: (PlatformViewCreationParams params) {
          return PlatformViewsService.initSurfaceAndroidView(
            id: params.id,
            viewType: viewType,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
            onFocus: () {
              params.onFocusChanged(true);
            },
          )
            ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
            ..create();
        },
        viewType: viewType);
  }

  Widget androidViewTwo() {
    return PlatformViewLink(
        surfaceFactory: (BuildContext context, PlatformViewController controller) {
          return AndroidViewSurface(
            controller: controller as AndroidViewController,
            gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
            hitTestBehavior: PlatformViewHitTestBehavior.opaque,
          );
        },
        onCreatePlatformView: (PlatformViewCreationParams params) {
          return PlatformViewsService.initSurfaceAndroidView(
            id: params.id,
            viewType: 'customViewSecond',
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
            onFocus: () {
              params.onFocusChanged(true);
            },
          )
            ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
            ..create();
        },
        viewType: viewType);
  }

  Widget dialRow() {
    return Expanded(
      child: Row(children: [
        Text("Dial"),
        Container(width: 10),
        calleeIdField(_calleeController),
        Container(width: 10),
        _isCallActive ? hangupButton() : callButton(_calleeController)
      ]),
    );
  }

  Widget receiveRow() {
    return Expanded(
      child: Row(children: [
        Text('Receiving calls'),
        Container(width: 10),
        callerNickname != null
            ? Text('$callerNickname')
            : callerId != null
                ? Text("$callerId")
                : Container(),
        _areReceivingCall ? receivingCallButton() : Container(),
        callerId != null && _isCallActive ? hangupButton() : Container(),
      ]),
    );
  }

  Widget statusField() {
    return Container(
        child: _areConnected
            ? Icon(
                Icons.check_circle,
                color: Colors.green,
                size: 40.0,
              )
            : Icon(
                Icons.remove_circle_outline,
                color: Colors.red,
                size: 40.0,
              ));
  }

  Widget calleeIdField(TextEditingController calleeController) {
    return Container(
      child: TextField(
        controller: calleeController,
        onChanged: (text) {
          setState(() {
            _isCalleeAvailable = text.isNotEmpty;
          });
        },
        decoration: InputDecoration(labelText: "Callee User Id"),
      ),
    );
  }

  Widget callButton(TextEditingController controller) {
    return Container(
      child: ElevatedButton(
        onPressed: () async {
          channels?.startCall(controller.text);
          setState(() {
            _areCalling = true;
          });
        },
        child: Icon(
          Icons.call,
          color: Colors.white,
          size: 20.0,
        ),
        style: ElevatedButton.styleFrom(
          shape: CircleBorder(),
          primary: Colors.green, // <-- Button color
          onPrimary: Colors.green, // <-- Splash color
        ),
      ),
    );
  }

  Widget receivingCallButton() {
    return Container(
      child: ElevatedButton(
        onPressed: () {
          channels?.pickupCall();
        },
        child: Icon(
          Icons.call,
          color: Colors.blue,
          size: 20.0,
        ),
        style: ElevatedButton.styleFrom(
          shape: CircleBorder(),
          primary: Colors.white, // <-- Button color
          onPrimary: Colors.white, // <-- Splash color
        ),
      ),
    );
  }

  Widget hangupButton() {
    return Container(
      padding: EdgeInsets.all(20),
      child: ElevatedButton(
        onPressed: () {
          channels?.endCall();
        },
        child: Icon(
          Icons.call_end,
          color: Colors.white,
        ),
        style: ElevatedButton.styleFrom(
          padding: EdgeInsets.all(20),
          shape: CircleBorder(),
          primary: Colors.red, // <-- Button color
          onPrimary: Colors.red, // <-- Splash color
        ),
      ),
    );
  }
}
