package xyz.tryfle.capeinjector.mixin.api

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual
import org.spongepowered.asm.launch.platform.container.IContainerHandle
import org.spongepowered.asm.mixin.transformer.IMixinTransformer
import org.spongepowered.asm.service.*
import java.io.InputStream
import java.net.URL
import java.util.*

class CapeInjectorMixinService : MixinServiceAbstract(), IMixinService, IClassProvider, IClassBytecodeProvider {

    companion object {
        @JvmStatic
        var transformer: IMixinTransformer? = null
            private set

        fun setTransformer(transformer: IMixinTransformer) {
            this.transformer = transformer
        }
    }

    override fun getName(): String = "CapeInjectorMixinService"

    override fun isValid(): Boolean = true

    override fun getClassProvider(): IClassProvider = this

    override fun getBytecodeProvider(): IClassBytecodeProvider = this

    override fun getTransformerProvider(): ITransformerProvider? = null

    override fun getClassTracker(): IClassTracker? = null

    override fun getAuditTrail(): IMixinAuditTrail? = null

    override fun getPlatformAgents(): Collection<String> = Collections.emptyList()

    override fun getPrimaryContainer(): IContainerHandle = ContainerHandleVirtual(getName())

    override fun getResourceAsStream(name: String?): InputStream? {
        return try {
            Thread.currentThread().contextClassLoader?.getResourceAsStream(name)
                ?: ClassLoader.getSystemResourceAsStream(name)
        } catch (e: Exception) {
            null
        }
    }

    override fun getClassPath(): Array<URL> = emptyArray()

    override fun findClass(name: String?): Class<*>? {
        return try {
            Class.forName(name, false, Thread.currentThread().contextClassLoader)
        } catch (e: Exception) {
            try {
                Class.forName(name)
            } catch (e2: Exception) {
                null
            }
        }
    }

    override fun findClass(name: String?, initialize: Boolean): Class<*>? {
        return try {
            Class.forName(name, initialize, Thread.currentThread().contextClassLoader)
        } catch (e: Exception) {
            try {
                Class.forName(name, initialize, ClassLoader.getSystemClassLoader())
            } catch (e2: Exception) {
                null
            }
        }
    }

    override fun findAgentClass(name: String?, initialize: Boolean): Class<*>? = findClass(name, initialize)

    override fun getClassNode(name: String?): ClassNode? {
        return try {
            val classLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
            val resourceName = name!!.replace('.', '/') + ".class"
            val inputStream = classLoader.getResourceAsStream(resourceName)
                ?: return null

            val bytes = inputStream.use { it.readBytes() }
            val reader = ClassReader(bytes)
            val node = ClassNode()
            reader.accept(node, 0)
            node
        } catch (e: Exception) {
            println("[CapeInjector] Failed to get ClassNode for $name: ${e.message}")
            null
        }
    }

    override fun getClassNode(name: String?, runTransformers: Boolean): ClassNode? = getClassNode(name)
}