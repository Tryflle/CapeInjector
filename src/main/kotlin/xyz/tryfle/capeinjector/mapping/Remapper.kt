import net.fabricmc.mappingio.tree.MemoryMappingTree
import org.objectweb.asm.commons.Remapper
import xyz.tryfle.capeinjector.mapping.MappingSet

class Remapper(
    private val mappingTree: MemoryMappingTree
) : Remapper() {

    private val srcNs = mappingTree.getNamespaceId("official")
    private val dstNs = mappingTree.getNamespaceId("named")

    override fun map(internalName: String): String {
        return mappingTree.mapClassName(internalName, srcNs, dstNs)
    }

    override fun mapMethodName(owner: String, name: String, descriptor: String): String {
        val method = mappingTree.getMethod(owner, name, descriptor, srcNs) ?: return name
        return method.getName(dstNs) ?: name
    }

    override fun mapFieldName(owner: String, name: String, descriptor: String): String {
        val field = mappingTree.getField(owner, name, descriptor, srcNs) ?: return name
        return field.getName(dstNs) ?: name
    }

    companion object {
        fun fromTinyResource(resourcePath: String = "mappings/1_8/srg-yarn.tiny"): Remapper {
            val tree = MappingSet.loadTinyMappings(resourcePath)
            return Remapper(tree as MemoryMappingTree)
        }
    }
}