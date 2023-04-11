package io.dock2dock.networking.models

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

        return gson.fromJson(stringResponse, Dock2DockApiError::class.java);
    }
}
