package xyz.tryfle.capeinjector

import xyz.tryfle.capeinjector.init.common.Detector
import java.lang.instrument.Instrumentation
import java.net.URL
import java.net.URLClassLoader
import kotlin.system.exitProcess

object Bootstrap {

    lateinit var inst: Instrumentation
    var initialized = false

    @JvmStatic
    fun premain(agentArgs: String?, instr: Instrumentation) {

        inst = instr

        println("[CI] Attached to JVM.")
        println("[CI] Detected: (${Detector.detectClient()})")

        init()
    }

    fun isSupported(client: String): Boolean {
        return when (client) {
            "Lunar" -> false
            "Baldlion" -> false
            "Forge" -> true
            "Vanilla" -> true
            else -> false
        }
    }

    fun init() {
        println("[CI] Initializing...")
        if (initialized) return

        initialized = true

        val client = try {
            Detector.detectClient()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            try {
                exitProcess(1)
            } catch (_: Throwable) {
                println("[CI] what?")
            }
        }
        if (!isSupported(client as String)) {
            println("[CI] $client is not currently supported, exiting so we don't crash")

            return
        }

        val minecraftLoader =
            inst
                .allLoadedClasses
                .find { it.name.startsWith("net.minecraft.") }
                ?.classLoader ?: return
        val agentPath = Bootstrap::class.java.getProtectionDomain().codeSource.location.toURI().toURL()

//        if (client == "Baldlion") {
//            println("[CI] Why... also fun fact I internally call badlion baldlion.")
//        }

        if (client == "Lunar" || client == "Forge") {
            val targetLoader = if (client == "Lunar") minecraftLoader.parent else minecraftLoader
            val addUrl =
                URLClassLoader::class.java
                    .getDeclaredMethod("addURL", URL::class.java)
                    .also { it.isAccessible = true }

            addUrl.invoke(targetLoader, agentPath)

            println("[CI] Added agent to class loader: ${targetLoader::class.java.name}")

            loadAgent(targetLoader)
        }

        minecraftLoader
            .loadClass("xyz.tryfle.capeinjector.init.common.InitializerKt")
            .declaredMethods
            .find { it.name == "bootstrap" }!!
            .invoke(null, inst, client)
    }

    fun loadAgent(loader: ClassLoader?) {
        val jar = Bootstrap::class.java.protectionDomain.codeSource.location.toURI().toURL()
        URLClassLoader::class.java
            .getDeclaredMethod("addURL", URL::class.java)
            .also { it.isAccessible = true }.invoke(loader, jar)
        println("[CI] Loaded agent into class loader.")
    }
}