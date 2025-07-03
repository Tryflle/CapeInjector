package xyz.tryfle.capeinjector.transformer.common

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ClientMixinTransformer : ClassFileTransformer {

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