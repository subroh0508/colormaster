plugins {
    kotlin("multiplatform")
    `android-multiplatform`
}

kotlinMpp {
    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val jsMain by getting
    }
}
