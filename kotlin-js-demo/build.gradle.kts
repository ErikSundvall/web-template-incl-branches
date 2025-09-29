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
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}

// Configure the generated JavaScript
tasks {
    compileKotlinJs {
        kotlinOptions {
            moduleKind = "umd"
            outputFile = "${project.buildDir}/js/packages/${project.name}/kotlin/${project.name}.js"
            sourceMap = true
            metaInfo = true
        }
    }
}