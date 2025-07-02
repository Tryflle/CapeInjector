package xyz.tryfle.capeinjector.transformer.legacy

import xyz.tryfle.capeinjector.init.TinyParser
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class MappingsTransformer : ClassFileTransformer {
    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray? {
        return null
//        if (className == null || classfileBuffer == null) return null
//
//        if (className.startsWith("xyz/tryfle/capeinjector/")) {
//            return TinyParser.remapBytes(classfileBuffer)
//        }
//
//        return null
    }
}