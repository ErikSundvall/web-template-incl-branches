plugins {
    kotlin("js") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    
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