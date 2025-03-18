# Keep public API
-keep class dev.mjstokely.flack.Flack { *; }
-keep class dev.mjstokely.flack.ConnectionStatus { *; }
-keep class dev.mjstokely.flack.FlackConnection { *; }
-keep class dev.mjstokely.flack.FlackException { *; }
-keep class dev.mjstokely.flack.ConnectionException { *; }
-keep class dev.mjstokely.flack.DisconnectionException { *; }
-keep class dev.mjstokely.flack.PermissionException { *; }
-keep class dev.mjstokely.flack.UnsupportedFeatureException { *; }

# Keep native method names
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep kotlinx.coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Android-specific
-keepclassmembers class dev.mjstokely.flack.AndroidFlackConnection {
    *;
}

# iOS/Apple-specific
-keepclassmembers class dev.mjstokely.flack.AppleFlackConnection {
    *;
}