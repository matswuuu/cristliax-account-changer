package org.matswuuu.cristalixaccountchanger

data class Account(
    var nick: String = "",
    var token: String = "",
    var backgroundIndex: Int = 0,
    var minimalGraphics: Boolean = false,
    var fullscreen: Boolean = false,
    var discordRPC: Boolean = false,
    var autoEnter: Boolean = false,
    var debugMode: Boolean = false,
    var memoryAmount: Int = -1,
)
