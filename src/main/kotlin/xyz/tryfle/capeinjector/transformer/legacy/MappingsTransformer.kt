package xyz.tryfle.capeinjector.transformer.legacy

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class MappingsTransformer(
    private val remapper: Remapper
) : ClassFileTransformer {

    private val ignoredPrefixes = listOf(
        "java/",
        "javax/",
        "sun/",
        "com/sun/",
        "jdk/",
        "org/objectweb/asm/",
        "xyz/tryfle/capeinjector/"
    )

    override fun transform(
        loader: ClassLoader?,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray? {
        try {
            if (ignoredPrefixes.find { className.startsWith(it) } != null) {
                return null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun remap(node: ClassNode): ClassNode {
        val newNode = ClassNode()

        node.accept(ClassRemapper(newNode, remapper))

        return newNode
    }
}
