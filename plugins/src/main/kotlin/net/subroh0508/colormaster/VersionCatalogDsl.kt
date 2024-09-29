package net.subroh0508.colormaster

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.version(alias: String) = findVersion(alias).get().requiredVersion
internal fun VersionCatalog.library(library: String) = findLibrary(library).get().get()
internal fun VersionCatalog.bundle(bundle: String) = findBundle(bundle).get().get()
