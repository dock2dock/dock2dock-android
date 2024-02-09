package io.dock2dock.android.networking.clients

import com.skydoves.sandwich.ApiResponse
import io.dock2dock.android.application.models.commands.CreateCrossdockLabel
import io.dock2dock.android.application.models.commands.*
import io.dock2dock.android.application.models.query.*
import io.dock2dock.android.networking.models.ODataResponse
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

    @GET("/LicensePlate/{no}")
    suspend fun getLicensePlate(@Path("no") no: String): ApiResponse<LicensePlate>

    @GET("/LicensePlate/")
    suspend fun getLicensePlates(@Query("\$filter") filter: String, @Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<LicensePlate>>

    @GET("/LicensePlateLine/")
    suspend fun getLicensePlateLines(@Query("\$filter") filter: String, @Query("\$orderBy") orderBy: String): ApiResponse<ODataResponse<LicensePlateLine>>

    @POST("/LicensePlate/Create")
    suspend fun createLicensePlate(@Body body: CreateLicensePlateRequest): ApiResponse<CreateLicensePlateResponse>

    @POST("/LicensePlate/Complete")
    suspend fun completeLicensePlate(@Body body: CompleteLicensePlateRequest): ApiResponse<Unit>

    @POST("/LicensePlate/Reprint")
    suspend fun reprintLicensePlate(@Body body: ReprintLicensePlateRequest): ApiResponse<Unit>
}

