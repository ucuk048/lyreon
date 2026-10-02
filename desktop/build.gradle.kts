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

    // Support Skiko native runtimes for macOS (Apple Silicon ARM64 & Intel x64)
    implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.150.1")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.150.1")

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

    // JavaFX Media for native audio playback
    // Use target/host platform classifier to prevent bundling conflicting foreign-arch native libraries (e.g. x86_64 dylib into ARM64 JVM)
    val jfxVersion = "21.0.2"
    val osName = System.getProperty("os.name", "").lowercase()
    val osArch = System.getProperty("os.arch", "").lowercase()

    val targetJfxPlatform = when {
        project.hasProperty("targetPlatform") -> project.property("targetPlatform").toString()
        osName.contains("mac") || osName.contains("darwin") -> {
            if (osArch == "aarch64" || osArch == "arm64") "mac-aarch64" else "mac"
        }
        osName.contains("win") -> "win"
        osName.contains("linux") -> "linux"
        else -> "win"
    }

    implementation("org.openjfx:javafx-base:$jfxVersion:$targetJfxPlatform")
    implementation("org.openjfx:javafx-graphics:$jfxVersion:$targetJfxPlatform")
    implementation("org.openjfx:javafx-media:$jfxVersion:$targetJfxPlatform")
    implementation("org.openjfx:javafx-swing:$jfxVersion:$targetJfxPlatform")
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "com.lyreon.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Pkg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Lyreon"
            packageVersion = "3.5.0"
            description = "Lyreon - Hear What Words Can't Say"
            vendor = "rixz-dev"

            modules(
                "java.instrument",
                "jdk.httpserver",
                "jdk.unsupported",
                "java.management",
                "java.naming",
                "java.sql"
            )

            macOS {
                bundleID = "com.lyreon.desktop"
                dockName = "Lyreon"
                appStore = false
                iconFile.set(project.file("src/main/resources/icon.icns"))
                packageBuildVersion = "3.5.0"

                infoPlist {
                    extraKeysRawXml = """
                        <key>NSAppTransportSecurity</key>
                        <dict>
                            <key>NSAllowsArbitraryLoads</key>
                            <true/>
                            <key>NSAllowsLocalNetworking</key>
                            <true/>
                        </dict>
                    """.trimIndent()
                }
            }

            windows {
                menuGroup = "Lyreon"
                shortcut = true
                dirChooser = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}

tasks.register<Jar>("packageMacArm64Jar") {
    group = "compose desktop"
    description = "Packages a standalone runnable fat JAR targeting macOS Apple Silicon (ARM64)"
    archiveBaseName.set("Lyreon-macos-arm64")
    archiveVersion.set("3.5.0")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.lyreon.desktop.MainKt"
    }

    from(sourceSets["main"].output)
    dependsOn(configurations.named("runtimeClasspath"))
    from({
        configurations.named("runtimeClasspath").get()
            .filter { it.name.endsWith(".jar") }
            .filterNot { file ->
                val name = file.name
                name.endsWith("-win.jar") ||
                name.contains("windows") ||
                (name.contains("javafx-") && name.endsWith("-mac.jar")) ||
                name.contains("macos-x64") ||
                name.contains("linux")
            }
            .map { zipTree(it) }
    })
    exclude("**/*.dll", "**/*.exe", "**/*.so")
}

tasks.register<Jar>("packageWindowsX64Jar") {
    group = "compose desktop"
    description = "Packages a standalone runnable fat JAR targeting Windows (x64)"
    archiveBaseName.set("Lyreon-windows-x64")
    archiveVersion.set("3.5.0")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.lyreon.desktop.MainKt"
    }

    from(sourceSets["main"].output)
    dependsOn(configurations.named("runtimeClasspath"))
    from({
        configurations.named("runtimeClasspath").get()
            .filter { it.name.endsWith(".jar") }
            .filterNot { file ->
                val name = file.name
                name.contains("mac") || name.contains("linux")
            }
            .map { zipTree(it) }
    })
    exclude("**/*.dylib", "**/*.so")
}

