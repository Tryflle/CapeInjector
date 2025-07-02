package xyz.tryfle.capeinjector

import xyz.tryfle.capeinjector.bus.EventBus
import xyz.tryfle.capeinjector.init.Initialize
import java.lang.instrument.Instrumentation

object CapeInjector {

    lateinit var globalInst: Instrumentation
    val bus: EventBus = EventBus()

    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        // Pass global instrumentation instance
        globalInst = inst
        // Version detection:
        if (agentArgs == null || agentArgs.isEmpty()) {
            println("[CapeInjector] No agent arguments provided. Please provide simple version. eg: 1.8, 1.8.9, 1.21, etc.")
            return
        }
        if (agentArgs == "1.8" || agentArgs == "1.8.9") {
            // Init
            Initialize.init1_8()
        }
        println("[CapeInjector] Attached to JVM.")
    }
}