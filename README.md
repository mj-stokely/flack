# Flack

Flack is a Kotlin Multiplatform library for managing WiFi connections on Android and iOS platforms. It provides a common API for connecting to WiFi networks and monitoring connection status.

## Features

- Connect to WiFi networks with SSID and password
- Support for both secure and open WiFi networks
- Monitor WiFi connection status using Kotlin Flow
- Gracefully handle disconnection
- Support for temporary and persistent connections
- Cross-platform implementation for Android and iOS

## Requirements

- Kotlin 1.9.0+
- Android API 30+ (Android 11.0+)
- iOS 13.0+

## Repository Structure

- `/flack` - Main library module with the Flack implementation
  - `/src/commonMain` - Common Kotlin code shared across platforms
  - `/src/androidMain` - Android-specific implementation
  - `/src/appleMain` - iOS-specific implementation
  - `/src/commonTest` - Tests for the common code
  
- `/composeApp` - Sample application using Compose Multiplatform
  - Demonstrates how to use the Flack library with shared UI code
  
- `/iosApp` - iOS application wrapper
  - Entry point for the iOS app that embeds the Compose UI

## Sample App

The project includes a sample application that demonstrates how to use the Flack library. The sample app is built with Compose Multiplatform, providing a shared UI implementation for both Android and iOS.

### Features of the Sample App

- View current WiFi connection status (connected/disconnected)
- Connect to a WiFi network by providing SSID and password
- Option to create temporary or persistent connections
- Disconnect from the current WiFi network
- Real-time connection status updates

### Running the Sample App

**Android:**
1. Open the project in Android Studio
2. Select the 'composeApp' configuration
3. Run on an Android device or emulator (API 30+)
4. Grant the location and WiFi permissions when prompted

**iOS:**
1. Open the project in Android Studio
2. Select the 'iosApp' configuration
3. Run on an iOS device or simulator (iOS 13.0+)
4. Grant the location and hotspot configuration permissions when prompted

## Usage Example

```kotlin
// Android
val context: Context = ... // Use application context
val flack = Flack.create(context)

// iOS
val flack = Flack.create()

// Connect to a network
viewModelScope.launch {
    flack.connect(
        ssid = "MyNetwork", 
        password = "MyPassword", 
        isTemporary = false
    )
}

// Observe connection status
lifecycleScope.launch {
    flack.connectionStatus.collect { status ->
        when {
            status.isConnected -> println("Connected to ${status.ssid}")
            else -> println("Not connected")
        }
    }
}

// Disconnect
viewModelScope.launch {
    flack.disconnect()
}
```

## Platform-Specific Implementation

### Android
- Uses WifiNetworkSpecifier for connecting to WiFi networks
- Uses ConnectivityManager with NetworkCallback for monitoring
- Requires location permissions for WiFi scanning and connection

### iOS
- Uses NEHotspotConfiguration API for connecting to WiFi networks
- Uses NWPathMonitor for monitoring connection status
- Requires hotspot configuration permissions

## Required Permissions

### Android
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!-- For Android 13+ we need this additional permission -->
<uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES" />
```

### iOS
In Info.plist:
```xml
<key>NSHotspotConfigurationDescription</key>
<string>Connect to WiFi networks for testing Flack library</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>Your location is used for WiFi scanning and connection</string>
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>Your location is required for WiFi scanning and connection</string>
```

## Documentation

For detailed documentation on how to use the library, please see the [library's README](/flack/README.md).

## License

MIT License - See [LICENSE](LICENSE) file for details.

## Learn More

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Android WiFi APIs](https://developer.android.com/reference/android/net/wifi/package-summary)
- [iOS Network Extension Framework](https://developer.apple.com/documentation/networkextension)