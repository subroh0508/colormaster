plugins {
    kotlin("jvm")
    application
}

dependencies {

    
    // Ktor サーバーフレームワーク
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    
    // ロギング
    implementation(libs.logback.classic)
    
    // テスト
    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("net.subroh0508.colormaster.backend.ApplicationKt")
}
