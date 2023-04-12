package io.dock2dock.networking.clients

import io.dock2dock.application.models.commands.*
import io.dock2dock.application.models.query.CrossdockHandlingUnit
import io.dock2dock.application.models.query.CrossdockLabel
import io.dock2dock.application.models.query.Printer
import io.dock2dock.networking.models.ODataResponse
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
    suspend fun getPrinters(@Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<Printer>>

    @GET("/CrossdockHandlingUnit/")
    suspend fun getCrossdockHandlingUnits(): ApiResponse<ODataResponse<CrossdockHandlingUnit>>
}

