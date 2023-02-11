package com.dock2dock.android.networking.models

import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message

// An error response mapper.
// Create an instance of your custom model using the `ApiResponse.Failure.Error` in the `map`.
object HttpErrorMapper : ApiErrorModelMapper<Dock2DockApiError> {

    override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): Dock2DockApiError {
        return Dock2DockApiError(apiErrorResponse.statusCode.code.toString(), apiErrorResponse.message())
    }
}
