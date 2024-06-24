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
    implementation("io.javalin", "javalin", "5.6.3")
    implementation("org.json", "json", "20231013")
    implementation("com.zaxxer", "HikariCP", "5.1.0")
    implementation("mysql", "mysql-connector-java", "8.0.33")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("ch.qos.logback", "logback-classic", "1.4.11")

    testImplementation("junit:junit:4.13.2")

}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "com.github.redreaperlp.cdpusher.Main"
    }
}

application {
    mainClass.set("com.github.redreaperlp.cdpusher.Main")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}