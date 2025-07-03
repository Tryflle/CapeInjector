package xyz.tryfle.capeinjector.mapping

import com.sun.xml.internal.ws.api.databinding.MappingInfo
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.format.MappingFormat.TINY_2_FILE
import net.fabricmc.mappingio.tree.MemoryMappingTree
import net.fabricmc.mappingio.tree.MappingTree
import java.io.InputStreamReader

data class MappingSet(
    val info: MappingInfo,
    val tree: MappingTree
) {
    companion object {

        fun loadTinyMappings(resourcePath: String = "mappings/1_8/srg-yarn.tiny"): MappingTree {
            val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
                ?: error("Missing mappings file: $resourcePath")

            val tree = MemoryMappingTree()
            MappingReader.read(InputStreamReader(stream), TINY_2_FILE, tree)
            return tree
        }
    }
}