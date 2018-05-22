import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm") version "1.2.41"
}

dependencies {
    compile(kotlin("stdlib"))
    compile("org.glassfish.jaxb:txw2:2.2.11")
    compile("io.github.microutils:kotlin-logging:1.4.9")
    compile("org.slf4j:slf4j-simple:1.6.1")
}

repositories {
    jcenter()
}

group = "io.github.alexbogovich"
version = "0.1"


kotlin {
    experimental {
        coroutines = Coroutines.ENABLE
    }
}