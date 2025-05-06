plugins {
    kotlin("jvm")
    application
}

dependencies {
    // サーバーフレームワーク
    val ktorVersion = "2.3.5"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    
    // ロギング
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // テスト
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("net.subroh0508.colormaster.backend.ApplicationKt")
}
