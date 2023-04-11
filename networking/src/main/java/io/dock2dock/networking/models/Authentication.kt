package io.dock2dock.networking.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Authentication(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresInt: Int
)