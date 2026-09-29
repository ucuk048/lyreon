import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Rhino for YouTube signature deciphering
    implementation("org.mozilla:rhino:1.7.15")

    // JSON parsing
    implementation("org.json:json:20240303")

    // JavaFX Media for native audio playback on Windows
    val jfxVersion = "21.0.2"
    implementation("org.openjfx:javafx-base:$jfxVersion:win")
    implementation("org.openjfx:javafx-graphics:$jfxVersion:win")
    implementation("org.openjfx:javafx-media:$jfxVersion:win")
}



kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "com.lyreon.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Lyreon"
            packageVersion = "3.5.0"
            description = "Lyreon - Hear What Words Can't Say"
            vendor = "rixz-dev"
            windows {
                menuGroup = "Lyreon"
                shortcut = true
                dirChooser = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}
