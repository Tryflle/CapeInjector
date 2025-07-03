package xyz.tryfle.capeinjector.init.legacy

import xyz.tryfle.capeinjector.Bootstrap
import xyz.tryfle.capeinjector.transformer.legacy.MappingsTransformer

object LegacyInitializer {

    fun init() {
        val remapper = Remapper.fromTinyResource("mappings/1_8/srg-yarn.tiny")
        Bootstrap.inst.addTransformer(MappingsTransformer(remapper), true)
    }
}