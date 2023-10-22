package io.dock2dock.android.extensions

fun Int.toIntString(): String {
    return if (this == 0) {
        ""
    } else {
        this.toString()
    }
}