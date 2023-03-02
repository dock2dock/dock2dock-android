package com.dock2dock.android.networking.models

data class Dock2DockApiError(
    val statusCode: Int,
    val message: String,
    val code: String,
    val context: Dock2DockApiErrorContext
)
data class Dock2DockApiErrorContext(val url: String, val method: String);

object Dock2DockErrorCode {
    const val Unauthorised = "UNAUTHORISED"
}