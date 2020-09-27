plugins {
    `android-library`
    kotlin("android")
    kotlin("android.extensions")
}

dependencies {
    implementation(Libraries.Kotlin.android)
    implementation(Libraries.Kotlin.reflect)

    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Jetpack.appCompat)
    implementation(Libraries.Jetpack.coreKtx)
    implementation(Libraries.Jetpack.constraintLayout)
    implementation(Libraries.Jetpack.coordinatorLayout)
    implementation(Libraries.Jetpack.recyclerView)
    implementation(Libraries.Jetpack.lifecycleViewModel)
    implementation(Libraries.Jetpack.lifecycleLiveData)
    implementation(Libraries.Jetpack.material)

    api(Libraries.Jetpack.Navigation.runtimeKtx)
    api(Libraries.Jetpack.Navigation.fragmentKtx)
    api(Libraries.Jetpack.Navigation.uiKtx)
}
