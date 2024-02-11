package io.dock2dock.android.networking.models
import com.google.gson.annotations.SerializedName

data class ODataResponse<T>(val value: List<T>)
{
    var count: Long? = null
        private set(value) {
            field = value
        }

    /// <summary>
    /// Version 2.0 -> OData 8.0 Support
    /// </summary>
    @SerializedName("@odata.count")
    var _count: Long = 0
        set(value) {
            count = value
        }
}