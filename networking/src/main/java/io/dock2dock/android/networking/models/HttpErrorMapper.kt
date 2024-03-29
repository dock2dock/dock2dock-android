package io.dock2dock.android.networking.models

import com.google.gson.GsonBuilder
import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse

// An error response mapper.
// Create an instance of your custom model using the `ApiResponse.Failure.Error` in the `map`.
object HttpErrorMapper : ApiErrorModelMapper<Dock2DockApiError> {

    override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): Dock2DockApiError {
        val stringResponse = apiErrorResponse.errorBody?.string()

        val gson = GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        return try {
            gson.fromJson(stringResponse, Dock2DockApiError::class.java)
        } catch (ex: Exception) {
            Dock2DockApiError(apiErrorResponse.statusCode.code, stringResponse.toString(), "Unknown", Dock2DockApiErrorContext("", ""))
        }
    }
}
