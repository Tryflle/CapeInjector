package xyz.tryfle.capeinjector.feature.legacy

import xyz.tryfle.capeinjector.bus.Subscribe
import xyz.tryfle.capeinjector.bus.TickEvent

class Test {

    @Subscribe
    fun test(e: TickEvent) {
    }
}