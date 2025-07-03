package xyz.tryfle.capeinjector.init.common

import java.lang.instrument.Instrumentation

fun bootstrap(inst: Instrumentation, client: String) {
        println("[CI] HELLO CHAT")
}