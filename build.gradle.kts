import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.2.50"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.1"
    id("com.github.ben-manes.versions") version "0.18.0"
}

dependencies {
    compile(kotlin("stdlib", "1.2.50"))
    compile("org.glassfish.jaxb:txw2:2.3.0.1")
    compile("io.github.microutils:kotlin-logging:1.5.4")
}

repositories {
    jcenter()
}

group = "io.github.alexbogovich"
version = "0.2.2"


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
            pom.withXml {
                asNode().appendNode("dependencies").let { depNode ->
                    configurations.compile.allDependencies.forEach {
                        depNode.appendNode("dependency").apply {
                            appendNode("groupId", it.group)
                            appendNode("artifactId", it.name)
                            appendNode("version", it.version)
                        }
                    }
                }
            }
        }
    }
}

bintray {
    user = findProperty("bintrayUser") as String?
    key = findProperty("bintrayApiKey") as String?
    publish = true
    setPublications("mavenJava")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "repo"
        name = project.name
        userOrg = "alexbogovich"
        websiteUrl = "https://github.com/alexbogovich/xml-writer-dsl"
        githubRepo = "alexbogovich/xml-writer-dsl"
        vcsUrl = "https://github.com/alexbogovich/xml-writer-dsl.git"
        description = "DSL for XML generation"
        setLabels("kotlin")
        setLicenses("MIT")
        desc = description
    })
}

tasks {
    withType<GenerateMavenPom> {
        destination = file("$buildDir/libs/${project.name}-$version.pom")
    }
    "dependencyUpdates"(DependencyUpdatesTask::class) {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }
}