package io.dock2dock.android

object Configuration {
    const val minSdk = 23
    const val majorVersion = 0
    const val minorVersion = 2
    const val patchVersion = "7-alpha"
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.dock2dock"
}