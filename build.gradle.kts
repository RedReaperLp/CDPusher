import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.redreaperlp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org:jaudiotagger:2.0.3")
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "com.github.redreaperlp.cdpusher.Main"
    }
}

application {
    mainClass.set("com.github.redreaperlp.cdpusher.Main")
}