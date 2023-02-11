package com.dock2dock.android.networking.clients

import com.dock2dock.android.networking.models.Authentication
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.http.*

internal interface IdentityApiClient {
    @FormUrlEncoded
    @POST("connect/token")
    suspend fun getToken(@Field("client_id") clientId: String,
                 @Field("client_secret") clientSecret: String,
                 @Field("grant_type") grantType: String = "client_credentials"): ApiResponse<Authentication>
}