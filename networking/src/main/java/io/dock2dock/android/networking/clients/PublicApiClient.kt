package io.dock2dock.android.networking.clients

import com.skydoves.sandwich.ApiResponse
import io.dock2dock.android.application.models.commands.*
import io.dock2dock.android.application.models.query.*
import io.dock2dock.android.application.models.responses.AddPackageToShippingContainerResponse
import io.dock2dock.android.application.models.responses.CreateShippingContainerResponse
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
    suspend fun getCrossdockHandlingUnits(@Query("\$orderBy") orderBy: String = "Name asc"): ApiResponse<ODataResponse<CrossdockHandlingUnit>>

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

    @POST("/FreightShippingContainer/complete")
    suspend fun completeShippingContainer(@Body body: CompleteShippingContainerRequest): ApiResponse<Unit>

    @POST("/FreightShippingContainer/create")
    suspend fun createShippingContainer(@Body body: CreateShippingContainerRequest): ApiResponse<CreateShippingContainerResponse>

    @POST("/FreightShippingContainer/addPackage")
    suspend fun addPackageToShippingContainer(@Body body: AddPackageToShippingContainerRequest): ApiResponse<AddPackageToShippingContainerResponse>

    @POST("/FreightShippingContainer/removePackage")
    suspend fun removePackageFromShippingContainer(@Body body: RemovePackageFromShippingContainerRequest): ApiResponse<Unit>

    @POST("/FreightShippingContainer/Reprint")
    suspend fun reprintShippingContainer(@Body body: ReprintShippingContainerRequest): ApiResponse<Unit>

    @GET("/FreightShippingContainer/")
    suspend fun getShippingContainers(@Query("\$orderBy") orderBy: String = "DateCreated desc", @Query("\$top") top: Int = 25): ApiResponse<ODataResponse<ShippingContainer>>

    @GET("/FreightShippingContainer/{id}/?\$expand=ShippingContainerPackages")
    suspend fun getShippingContainer(@Path("id") id: String): ApiResponse<ShippingContainer>

    @GET("/FreightConsignmentProduct/")
    suspend fun getConsignmentProducts(@Query("\$filter") filter: String? = "IsHiddenOnScanner eq false", @Query("\$orderBy") orderBy: String = "Name asc"): ApiResponse<ODataResponse<ConsignmentProduct>>

    @GET("/FreightConsignmentItem/")
    suspend fun getConsignmentHeaderItems(@Query("\$filter") filter: String): ApiResponse<ODataResponse<ConsignmentHeaderItem>>

    @POST("/FreightConsignmentItem/printShippingLabels")
    suspend fun consignmentHeaderItemPrintShippingLabels(@Body body: PrintConsignmentItemShippingLabelsRequest): ApiResponse<Unit>

    @POST("/FreightConsignment/printManualShippingLabels")
    suspend fun consignmentHeaderPrintManualShippingLabels(@Body body: PrintManualConsignmentShippingLabelsRequest): ApiResponse<Unit>

    @GET("/FreightConsignmentManifest/")
    suspend fun getConsignmentManifests(@Query("\$filter") filter: String? = null,
                                        @Query("\$orderBy") orderBy: String = "DateCreated desc",
                                        @Query("\$top") top: Int = 25,
                                        @Query("\$expand") expand: String = "PickupAddress"): ApiResponse<ODataResponse<FreightConsignmentManifest>>

    @GET("/FreightConsignmentManifest/{id}")
    suspend fun getConsignmentManifest(@Path("id") id: String): ApiResponse<FreightConsignmentManifest>

    @GET("/FreightConsignmentManifestItem/")
    suspend fun getConsignmentManifestItems(@Query("\$filter") filter: String): ApiResponse<ODataResponse<ConsignmentManifestItem>>

    @POST("/FreightConsignmentManifestItem/printShippingLabels")
    suspend fun consignmentManifestItemPrintShippingLabels(@Body body: PrintConsignmentManifestItemShippingLabelsRequest): ApiResponse<Unit>

    @GET("/FreightPickupLocation/")
    suspend fun getFreightPickupLocations(@Query("\$filter") filter: String? = null): ApiResponse<ODataResponse<FreightPickupLocation>>
}

