package xyz.tryfle.capeinjector.init.common

import Remapper
import xyz.tryfle.capeinjector.transformer.legacy.MappingsTransformer
import java.lang.instrument.Instrumentation

fun bootstrap(inst: Instrumentation, client: String) {
        println("[CI] HELLO CHAT")

        val remapper = Remapper.fromTinyResource("mappings/1_8/srg-yarn.tiny")

        inst.addTransformer(MappingsTransformer(remapper), true)

}