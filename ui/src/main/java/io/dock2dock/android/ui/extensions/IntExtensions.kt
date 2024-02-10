package io.dock2dock.android.ui.extensions

fun Int.toIntString(): String {
    return if (this == 0) {
        ""
    } else {
        this.toString()
    }
}