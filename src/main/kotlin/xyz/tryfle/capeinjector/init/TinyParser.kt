package xyz.tryfle.capeinjector.init

import com.grappenmaker.mappings.ClasspathLoaders
import com.grappenmaker.mappings.MappingsLoader
import com.grappenmaker.mappings.MappingsRemapper
import com.grappenmaker.mappings.LambdaAwareRemapper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

object TinyParser {
    lateinit var remapper: MappingsRemapper
        private set

    fun parseTiny(path: String) {
        println("[CapeInjector] Parsing Tiny mappings from $path")

        val rawLines: List<String> = this::class.java.classLoader.getResourceAsStream(path)
            ?.bufferedReader()
            ?.readLines()
            ?: throw IllegalArgumentException("Resource not found: $path")

        println("[CapeInjector] Mappings header: ${rawLines.take(5)}")

        val cleanLines = buildList {
            for (line in rawLines) {
                if (line.startsWith("\t\tc") || line.startsWith("\t\tp")) continue
                if (line.startsWith("      c") || line.startsWith("      p")) continue
                add(line)
            }
        }

        if (cleanLines.firstOrNull()?.startsWith("tiny\t2") != true) {
            error("Invalid Tiny v2 mapping file")
        }

        val header = cleanLines.first().split("\t")
        println("[CapeInjector] Available namespaces: ${header.drop(2)}")

        val mappings = MappingsLoader.loadMappings(cleanLines)

        println("[CapeInjector] Sample mappings:")
        mappings.classes.take(3).forEach { clazz ->
            println("  Class: ${clazz.names}")
        }

        remapper = MappingsRemapper(
            mappings,
            from = "named",
            to = "obfuscated",
            loader = ClasspathLoaders.fromSystemLoader()
        )

        println("[CapeInjector] Mappings loaded and remapper initialized.")
    }

    fun remapMixinClass(input: ByteArray?): ByteArray? {
        if (input == null) return null

        return try {
            val reader = ClassReader(input)
            val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)

            val mixinRemappingVisitor = MixinRemappingVisitor(writer, remapper)
            reader.accept(mixinRemappingVisitor, 0)

            writer.toByteArray()
        } catch (e: Exception) {
            val reader = ClassReader(input)
            println("[CapeInjector] Failed to remap Mixin class: ${reader.className} - ${e.message}")
            e.printStackTrace()
            input
        }
    }

    fun remapBytes(input: ByteArray?): ByteArray? {
        if (input == null) return null

        return try {
            val reader = ClassReader(input)
            val className = reader.className

            if (!shouldRemap(className)) {
                return input
            }

            val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)

            if (isMixinClass(className)) {
                val mixinRemappingVisitor = MixinRemappingVisitor(writer, remapper)
                reader.accept(mixinRemappingVisitor, 0)
            } else {
                reader.accept(LambdaAwareRemapper(writer, remapper), 0)
            }

            val result = writer.toByteArray()
            result
        } catch (e: Exception) {
            val reader = ClassReader(input)
            println("[CapeInjector] Failed to remap class: ${reader.className} - ${e.message}")
            e.printStackTrace()
            input
        }
    }

    private fun shouldRemap(className: String): Boolean {
        return className.startsWith("xyz/tryfle/capeinjector/") && !isMixinClass(className)
    }

    private fun isMixinClass(className: String): Boolean {
        return className.contains("/mixin/") || className.contains("/mixins/")
    }

    private class MixinRemappingVisitor(
        classWriter: ClassWriter,
        private val remapper: MappingsRemapper
    ) : LambdaAwareRemapper(classWriter, remapper) {

        override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
            val av = super.visitAnnotation(descriptor, visible)

            if (descriptor == "Lorg/spongepowered/asm/mixin/Mixin;") {
                return MixinAnnotationRemapper(av, remapper)
            }

            return av
        }
    }

    private class MixinAnnotationRemapper(
        annotationVisitor: AnnotationVisitor?,
        private val remapper: MappingsRemapper
    ) : AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {

        override fun visit(name: String?, value: Any?) {
            when (name) {
                "value" -> {
                    if (value is String) {
                        val remappedValue = remapper.mapType(value)
                        super.visit(name, remappedValue)
                    } else {
                        super.visit(name, value)
                    }
                }
                "targets" -> {
                    if (value is String) {
                        val remappedValue = remapper.mapType(value.replace('.', '/')).replace('/', '.')
                        super.visit(name, remappedValue)
                    } else {
                        super.visit(name, value)
                    }
                }
                else -> super.visit(name, value)
            }
        }

        override fun visitArray(name: String?): AnnotationVisitor? {
            val av = super.visitArray(name)

            if (name == "targets" || name == "value") {
                return MixinArrayRemapper(av, remapper)
            }

            return av
        }
    }

    private class MixinArrayRemapper(
        annotationVisitor: AnnotationVisitor?,
        private val remapper: MappingsRemapper
    ) : AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {

        override fun visit(name: String?, value: Any?) {
            if (value is String) {
                val remappedValue = if (value.contains('.')) {
                    remapper.mapType(value.replace('.', '/')).replace('/', '.')
                } else {
                    remapper.mapType(value)
                }
                super.visit(name, remappedValue)
            } else {
                super.visit(name, value)
            }
        }
    }
}