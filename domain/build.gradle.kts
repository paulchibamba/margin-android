import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testFixturesImplementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}

tasks.test {
    val fsrsGoldenVectors = rootProject.file("docs/engineering/fsrs-golden.json")
    systemProperty("margin.fsrsGoldenVectors", fsrsGoldenVectors.path)
    inputs.files(fsrsGoldenVectors).withPropertyName("fsrsGoldenVectors")

    val contentPack = rootProject.file("content/pack")
    systemProperty("margin.contentPack", contentPack.path)
    inputs.files(fileTree(contentPack) { include("*.json") }).withPropertyName("contentPack")
}
