import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.10"
    id("com.gradleup.shadow") version "8.3.0"
    id("org.jetbrains.kotlin.kapt") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "org.openredstone.trialore"
version = "1.1"

repositories {
    mavenCentral()

    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.aikar.co/nexus/content/groups/aikar/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "org.danilopianini", name = "khttp", version = "1.6.3")
    implementation(group = "net.luckperms", name = "api", version = "5.1")
    implementation(group = "org.jetbrains.exposed", name = "exposed-core", version = "0.51.1")
    implementation(group = "org.jetbrains.exposed", name = "exposed-jdbc", version = "0.51.1")
    implementation(group = "org.jetbrains.exposed", name = "exposed-java-time", version = "0.51.1")
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.46.0.0")
    implementation(group = "co.aikar", name = "acf-paper", version = "0.5.1-SNAPSHOT")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.15.0")
    compileOnly(group = "io.papermc.paper", name = "paper-api", version = "1.20.4-R0.1-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "22"
    kotlinOptions.javaParameters = true
}

tasks.shadowJar {
    relocate("co.aikar.commands", "trialore.acf")
    relocate("co.aikar.locales", "trialore.locales")
    dependencies {
        exclude(
            dependency(
                "net.luckperms:api:.*"
            )
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
