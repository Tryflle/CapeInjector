package xyz.tryfle.capeinjector.transformer.legacy

import org.objectweb.asm.commons.Remapper
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
        "xyz/tryfle/capeinjector/",
        "joptsimple",
        "com/google",
        "org/prismlauncher/"
    )

    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray? {
        return null
    }
}