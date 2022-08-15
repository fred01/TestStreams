import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spaceUsername: String by project
val spacePassword: String by project

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/sps/yaks")
        credentials {
            username = spaceUsername
            password = spacePassword
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.4.0-stream-strings")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.0-stream-strings") {
        exclude("org.jetbrains.kotlinx")
    }
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}


