package xyz.tryfle.capeinjector.transformer.common

import xyz.tryfle.capeinjector.mixin.api.CapeInjectorMixinService
import xyz.tryfle.capeinjector.init.TinyParser
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ClientMixinTransformer : ClassFileTransformer {

    private var initialized = false
    private var initAttempts = 0
    private val maxInitAttempts = 100

    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray? {
        if (className == null || classfileBuffer == null) return null

        if (className.startsWith("java/") ||
            className.startsWith("sun/") ||
            className.startsWith("com/sun/") ||
            className.startsWith("org/spongepowered/")) {
            return null
        }

        if (className.startsWith("xyz/tryfle/capeinjector/")) {
            if (isMixinClass(className)) {
                return null
            }
            return TinyParser.remapBytes(classfileBuffer)
        }

        return handleTargetClass(className, classfileBuffer)
    }

    private fun handleOwnClass(className: String, classfileBuffer: ByteArray): ByteArray? {
        return try {
            if (isMixinClass(className)) {
                return TinyParser.remapMixinClass(classfileBuffer)
            } else {
                return TinyParser.remapBytes(classfileBuffer) ?: classfileBuffer
            }
        } catch (e: Exception) {
            println("[CapeInjector] Error processing own class $className: ${e.message}")
            classfileBuffer
        }
    }

    private fun handleTargetClass(className: String, classfileBuffer: ByteArray): ByteArray? {
        return try {
            var transformer = CapeInjectorMixinService.transformer

            if (transformer == null && !initialized && initAttempts < maxInitAttempts) {
                initAttempts++
                Thread.sleep(10)
                transformer = CapeInjectorMixinService.transformer

                if (transformer != null) {
                    initialized = true
                }
            }

            if (transformer == null) {
                if (!initialized && initAttempts >= maxInitAttempts) {
                    initialized = true
                }
                return null
            }

            val dotClassName = className.replace('/', '.')

            val result = transformer.transformClass(
                org.spongepowered.asm.mixin.MixinEnvironment.getDefaultEnvironment(),
                dotClassName,
                classfileBuffer
            )

            if (result != null && !result.contentEquals(classfileBuffer)) {
                result
            } else {
                null
            }

        } catch (e: Exception) {
            if (e.message?.contains("ClassNotFoundException") != true) {
                println("[CapeInjector] Error transforming target class $className: ${e.message}")
            }
            null
        }
    }

    private fun isMixinClass(className: String): Boolean {
        return className.contains("/mixin/") || className.contains("/mixins/")
    }
}