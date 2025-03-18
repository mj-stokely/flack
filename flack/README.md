# Flack

Flack is a Kotlin Multiplatform library for managing WiFi connections on Android and iOS platforms. It provides a common API for connecting to WiFi networks and monitoring connection status.

## Features

- Connect to WiFi networks with SSID and password
- Support for both secure and open WiFi networks
- Monitor WiFi connection status using Kotlin Flow
- Gracefully handle disconnection
- Support for temporary and persistent connections

## Compatibility

- Android: API 30+ (Android 11.0+)
- iOS: iOS 13.0+

## Installation

Add the dependency to your module's build.gradle.kts:

```kotlin
dependencies {
    implementation("dev.mjstokely:flack:0.1.0")
}
```

## Usage

### Initialization

```kotlin
// Android
val context: Context = ...
val flack = Flack.create(context)

// iOS
val flack = Flack.create()
```

### Connect to a WiFi Network

```kotlin
// Connect to a network
flack.connect(
    ssid = "MyNetwork", 
    password = "MyPassword", 
    isTemporary = false
)
```

### Monitor Connection Status

```kotlin
// Collect connection status changes
flack.connectionStatus.collect { status ->
    when {
        status.isConnected -> println("Connected to ${status.ssid}")
        else -> println("Not connected")
    }
}
```

### Disconnect

```kotlin
// Disconnect from the current network
flack.disconnect()
```

## Android-specific Information

### Required Permissions

Add the following permissions to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

### Temporary vs. Persistent Connections

On Android, the `isTemporary` parameter determines whether the connection persists beyond the app's lifecycle. When `isTemporary` is `true`, the connection will be dropped when your app is no longer in the foreground or is closed.

## iOS-specific Information

### Required Entitlements

To use WiFi connection functionality on iOS, your app requires the "HotspotConfiguration" entitlement.

Add the following to your entitlements file:

```xml
<key>com.apple.developer.networking.HotspotConfiguration</key>
<true/>
```

### App Store Approval

Using the NEHotspotConfiguration API requires specific App Store approval. Your app's use case will be reviewed by Apple during the submission process. Be prepared to justify why your app needs this capability.

### Temporary vs. Permanent Connections

On iOS, the `isTemporary` parameter maps directly to the `joinOnce` property of NEHotspotConfiguration. When `true`, iOS will automatically disconnect from the network once the device goes to sleep or when the user manually disconnects.

## Error Handling

Flack uses Kotlin's Result type for error handling:

```kotlin
try {
    val result = flack.connect("MyNetwork", "MyPassword")
    if (result.isSuccess) {
        // Successfully connected
    } else {
        // Failed to connect
        val exception = result.exceptionOrNull()
        // Handle exception
    }
} catch (e: Exception) {
    // Handle general exceptions
}
```

## Sample Code

```kotlin
// Initialize Flack
val flack = Flack.create(context) // Android
// val flack = Flack.create() // iOS

// Launch in a coroutine scope
lifecycleScope.launch {
    // Observe connection status
    flack.connectionStatus.collect { status ->
        updateUi(status)
    }
}

// Connect to a network
viewModelScope.launch {
    val result = flack.connect("MyNetwork", "MyPassword", false)
    if (result.isSuccess) {
        showSuccess()
    } else {
        showError(result.exceptionOrNull()?.message ?: "Unknown error")
    }
}

// Disconnect
viewModelScope.launch {
    flack.disconnect()
}
```

## License

Licensed under the MIT License. See [LICENSE](LICENSE) for details.