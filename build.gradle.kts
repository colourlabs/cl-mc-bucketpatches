plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "net.colourlabs.bucketpatches"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.asm.tree)
    compileOnly(libs.patchthebucket.api)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    manifest {
        attributes(
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes" to "true"
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
