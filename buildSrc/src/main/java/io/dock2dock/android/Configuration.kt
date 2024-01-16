package io.dock2dock.android

object Configuration {
    const val minSdk = 23
    const val majorVersion = 0
    const val minorVersion = 1
    const val patchVersion = "5-alpha"
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.dock2dock"
}