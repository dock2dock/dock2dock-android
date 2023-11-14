package io.dock2dock.android.clients

import com.skydoves.sandwich.ApiResponse
import io.dock2dock.android.models.ODataResponse
import io.dock2dock.android.models.commands.*
import io.dock2dock.android.models.query.CrossdockHandlingUnit
import io.dock2dock.android.models.query.CrossdockLabel
import io.dock2dock.android.models.query.CrossdockSalesOrder
import io.dock2dock.android.models.query.Printer
import retrofit2.http.*

interface PublicApiClient
{
    @POST("/CrossdockLabel/create")
    suspend fun createCrossdockLabel(@Body label: CreateCrossdockLabel): ApiResponse<Unit>

    @POST("/CrossdockLabel/delete")
    suspend fun deleteCrossdockLabel(@Body label: DeleteCrossdockLabel): ApiResponse<Unit>

    @POST("/CrossdockLabel/reprint")
    suspend fun reprintCrossdockLabel(@Body label: ReprintCrossdockLabel): ApiResponse<Unit>

    @GET("/CrossdockLabel/")
    suspend fun getLabels(@Query("\$filter") filter: String, @Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<CrossdockLabel>>

    @GET("/SalesOrder/{no}")
    suspend fun getSalesOrder(@Path("no") no: String): ApiResponse<CrossdockSalesOrder>

    @GET("/Printer/")
    suspend fun getPrinters(@Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<Printer>>

    @GET("/CrossdockHandlingUnit/")
    suspend fun getCrossdockHandlingUnits(): ApiResponse<ODataResponse<CrossdockHandlingUnit>>
}

