# Flack - KMP WiFi Connection Library

## Build & Test Commands
- Build project: `./gradlew build`
- Run tests: `./gradlew test`
- Build Android: `./gradlew flack:assembleDebug`
- Build iOS: `./gradlew flack:compileKotlinIosArm64`
- Run single test: `./gradlew test --tests "dev.mjstokely.flack.SomeTestName"`
- Android lint: `./gradlew lint`

## Code Style Guidelines
- Kotlin official code style (kotlin.code.style=official)
- 4-space indentation, no tabs
- 120 character line limit
- PascalCase for classes/interfaces (FlackConnection)
- camelCase for properties/functions (connectToNetwork)
- Use trailing commas in parameter lists
- Group imports by package
- Prefer immutable properties (val over var)
- Use Result<T> for error handling
- Use coroutines and Flow for async operations
- Write KDoc comments for public APIs
- Use expect/actual for platform-specific implementations

## Project Structure
- /flack - Core library module
- /composeApp - Example app using Compose Multiplatform