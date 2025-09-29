plugins {
    kotlin("js") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "care.better.platform"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    js {
        browser {
            binaries.executable()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}