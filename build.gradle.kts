plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
}

group = "xyz.tryfle"
version = "1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("org.json:json:20250517")
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.770grappenmaker:mappings-util:0.1.7")
    implementation("org.spongepowered:mixin:0.8.5")
    implementation("commons-io:commons-io:2.14.0")
    compileOnly(fileTree("lib") {
        (files(layout.projectDirectory.file("lib/mcp-mapped.jar")))
    })
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Premain-Class" to "xyz.tryfle.capeinjector.CapeInjector",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}