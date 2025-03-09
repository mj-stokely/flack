package dev.mjstokely.flackexample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform