import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val VersionCatalog.nodeVersion get() = getVersion("node").requiredVersion
internal val VersionCatalog.yarnVersion get() = getVersion("yarn").requiredVersion

internal val VersionCatalog.kotlinxCoroutinesCore get() = getLibrary("kotlinx-coroutines-core")
internal val VersionCatalog.kotlinxSerialization get() = getLibrary("kotlinx-serialization-json")

internal val VersionCatalog.koinCore get() = getLibrary("koin-core")

internal val VersionCatalog.firebaseBom get() = getBundle("firebase-bom")

internal val VersionCatalog.kotlinWrappersBom get() = getLibrary("kotlin-wrappers-bom")
internal val VersionCatalog.kotlinWrappersExtensions get() = getLibrary("kotlin-wrappers-extensions")

internal val VersionCatalog.kotestAssertionsCore get() = getLibrary("kotest-assertions-core")
internal val VersionCatalog.kotestFrameworkEngine get() = getLibrary("kotest-framework-engine")
internal val VersionCatalog.kotestRunnerJunit5 get() = getLibrary("kotest-runner-junit5")

internal val VersionCatalog.kotlinxCoroutinesTest get() = getLibrary("kotlinx-coroutines-test")
internal val VersionCatalog.mockkAndroid get() = getLibrary("mockk-android")

internal val Project.libs: VersionCatalog get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

private fun VersionCatalog.getVersion(alias: String) = findVersion(alias).get()
private fun VersionCatalog.getLibrary(library: String) = findLibrary(library).get()
private fun VersionCatalog.getBundle(bundle: String) = findBundle(bundle).get()
