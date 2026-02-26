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
    }
}
