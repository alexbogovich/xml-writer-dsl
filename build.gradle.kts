import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm") version "1.2.50"
    `maven-publish`
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
version = "0.2"


kotlin {
    experimental {
        coroutines = Coroutines.ENABLE
    }
}

publishing {
    (publications) {
        "mavenJava"(MavenPublication::class) {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
            artifact(tasks["jar"])
        }
    }
}