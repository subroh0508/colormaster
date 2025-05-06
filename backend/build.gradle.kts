plugins {
    kotlin("jvm")
    application
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    
    // Ktor サーバーフレームワーク
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    
    // SQLDelight
    implementation(libs.sqldelight.jvm.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.sqldelight.primitive.adapters)
    
    // ロギング
    implementation(libs.logback.classic)
    
    // テスト
    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("net.subroh0508.colormaster.backend.ApplicationKt")
}

sqldelight {
    databases {
        create("ColorMasterDatabase") {
            packageName.set("net.subroh0508.colormaster.backend.database")
        }
    }
}
