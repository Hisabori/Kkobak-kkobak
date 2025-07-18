# Kkobak Android App

This project now includes a minimal Flutter module to provide aesthetic UI screens.

## Flutter module
The Flutter module lives in `flutter_module/`. You can build it as an AAR and include it in the Android app.

```bash
cd flutter_module
flutter pub get
flutter build aar
```

After building, the Android module can launch `FlutterMainActivity` defined in
`app/src/main/java/com/example/kkobakkobak/ui/flutter/FlutterMainActivity.kt`.
