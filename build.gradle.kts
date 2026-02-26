plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.3.1")
        android.set(true)
        outputToConsole.set(true)
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("config/detekt/detekt.yml"))
}

tasks.register("verify") {
    group = "verification"
    description = "Runs lint, static analysis, tests and compile checks"
    dependsOn(
        ":ktlintCheck",
        ":detekt",
        ":shared:logic:jvmTest",
        ":shared:ui:jvmTest",
        ":app-desktop:test",
        ":shared:logic:compileKotlinJvm",
        ":shared:ui:compileKotlinJvm",
        ":app-desktop:compileKotlin",
    )
}
