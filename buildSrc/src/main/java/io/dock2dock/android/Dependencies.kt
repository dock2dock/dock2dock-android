package io.dock2dock.android

object Versions {
    internal const val ANDROIDX_ACTIVITY = "1.6.1"
    internal const val ANDROIDX_APPCOMPACT = "1.6.0"
    internal const val ANDROIDX_COMPOSE = "1.4.2"
    internal const val ANDROIDX_COMPOSE_MATERIAL = "1.3.1"
    internal const val ANDROIDX_CORE = "1.9.0"
    internal const val ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE = "2.6.2"
    internal const val ANDROIDX_LIFECYCLE_RUNTIME_KTX = "2.5.1"
    internal const val ANDROIDX_LIFECYCLE_COMMON = "2.8.4"
    internal const val ANDROIDX_TEST_JUNIT = "1.1.5"
    internal const val ANDROIDX_TEST_ESPRESSO = "3.5.1"
    internal const val DOKAR3_SHEETS = "0.5.4"
    internal const val GOOGLE_MATERIAL = "1.8.0"
    internal const val GOOGLE_MATERIAL_ICONS_EXTENDED = "1.6.8"
    internal const val J_UNIT = "4.13.2"
    internal const val GOOGLE_GSON = "2.10"
    internal const val ANDROIDX_NAVIGATION = "2.8.1"

    internal const val SKYDOVES_SANDWICH = "1.3.3"
}

object Dependencies {
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.ANDROIDX_CORE}"
    const val androidxAppCompact = "androidx.appcompat:appcompat:${Versions.ANDROIDX_APPCOMPACT}"
    const val androidxLifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.ANDROIDX_COMPOSE}"
    const val androidxLifecycleRunTimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.ANDROIDX_LIFECYCLE_RUNTIME_KTX}"
    const val androidxActivity = "androidx.activity:activity-compose:${Versions.ANDROIDX_ACTIVITY}"
    const val androidxComposeUi = "androidx.compose.ui:ui:${Versions.ANDROIDX_COMPOSE}"
    const val androidxComposeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.ANDROIDX_COMPOSE}"
    const val androidxComposeMaterial = "androidx.compose.material:material:${Versions.ANDROIDX_COMPOSE_MATERIAL}"
    const val androidxComposeRuntimeLivedata = "androidx.compose.runtime:runtime-livedata:${Versions.ANDROIDX_COMPOSE}"
    const val androidxMaterialIconsExtended = "androidx.compose.material:material-icons-extended:${Versions.GOOGLE_MATERIAL_ICONS_EXTENDED}"
    const val androidxLifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_JUNIT}"
    const val androidxTestEspresso = "androidx.test.espresso:espresso-core:${Versions.ANDROIDX_TEST_ESPRESSO}"
    const val androidxLifecycleCommon = "androidx.lifecycle:lifecycle-common:${Versions.ANDROIDX_LIFECYCLE_COMMON}"
    const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.ANDROIDX_COMPOSE}"
    const val dokar3Sheets = "io.github.dokar3:sheets:${Versions.DOKAR3_SHEETS}"
    const val googleGson = "com.google.code.gson:gson:${Versions.GOOGLE_GSON}"
    const val googleMaterial = "com.google.android.material:material:${Versions.GOOGLE_MATERIAL}"
    const val jUnit = "junit:junit:${Versions.J_UNIT}"
    const val skydovesSandwich = "com.github.skydoves:sandwich:${Versions.SKYDOVES_SANDWICH}"
    const val androidxNavigationRuntimeKtx = "androidx.navigation:navigation-runtime-ktx:${Versions.ANDROIDX_NAVIGATION}"
    const val androidxNavigationCompose = "androidx.navigation:navigation-compose:${Versions.ANDROIDX_NAVIGATION}"


}