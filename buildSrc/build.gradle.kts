plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    implementation("com.android.tools.build:gradle:3.5.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70-eap-274")
}
