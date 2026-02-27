import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":shared:ui"))
    testImplementation(libs.junit)
}

compose.desktop {
    application {
        mainClass = "com.vikingschess.desktop.MainKt"

        nativeDistributions {
            packageName = "Hnefatafl"
            packageVersion = "1.0.3"
            description = "Modern Hnefatafl with polished desktop UI"
            copyright = "© Yoav"
            vendor = "MasterYoav"
            includeAllModules = true

            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Exe,
                TargetFormat.Deb,
            )

            macOS {
                iconFile.set(rootProject.file("assets/icon.icns"))
            }
            windows {
                iconFile.set(rootProject.file("assets/icon.ico"))
            }
            linux {
                iconFile.set(rootProject.file("assets/icon.png"))
            }
        }
    }
}
