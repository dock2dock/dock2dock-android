package com.dock2dock.android.networking.clients

import com.dock2dock.android.application.models.commands.*
import com.dock2dock.android.application.models.query.CrossdockHandlingUnit
import com.dock2dock.android.application.models.query.CrossdockLabel
import com.dock2dock.android.application.models.query.Printer
import com.dock2dock.android.networking.models.ODataResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*

interface PublicApiClient
{
    @POST("/CrossdockLabel/create")
    suspend fun createCrossdockLabel(@Body label: CreateCrossdockLabel): ApiResponse<Unit>

    @POST("/CrossdockLabel/delete")
    suspend fun deleteCrossdockLabel(@Body label: DeleteCrossdockLabel): ApiResponse<Unit>

    @GET("/CrossdockLabel/")
    suspend fun getLabels(@Query("\$filter") filter: String, @Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<CrossdockLabel>>

    @GET("/Printer/")
    suspend fun getPrinters(): ApiResponse<ODataResponse<Printer>>

    @GET("/CrossdockHandlingUnit/")
    suspend fun getCrossdockHandlingUnits(): ApiResponse<ODataResponse<CrossdockHandlingUnit>>
}

