package dev.mjstokely.flack

internal actual fun FlackConnection(): FlackConnection = AndroidFlackConnection(AndroidContext.getApplicationContext())