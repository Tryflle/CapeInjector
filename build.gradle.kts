plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("legacy-looming") version "1.8-SNAPSHOT"
}

group = "xyz.tryfle"
version = "1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
}

loom {
    customMinecraftManifest.set("https://meta.legacyfabric.net/v2/manifest/1.8.9")
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
//    implementation("io.github.770grappenmaker:mappings-util:0.1.7")
    implementation("io.github.llamalad7:mixinextras-common:0.3.6")
    implementation("net.fabricmc:mapping-io:0.6.1")
    implementation("org.spongepowered:mixin:0.8.5")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings(legacy.yarn("1.8.9", "535"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    minimize()
    manifest {
        attributes(
            "Premain-Class" to "xyz.tryfle.capeinjector.Bootstrap",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
        // clean up artifact
        exclude("net/minecraft/**")
//        exclude("net/fabricmc/**")
        exclude("assets/**")
        exclude("Log4j**")
        exclude("log4j2.xml")
        exclude("LICENSE**")
        exclude("pack.png")
        exclude("yggdrasil**")
    }
}