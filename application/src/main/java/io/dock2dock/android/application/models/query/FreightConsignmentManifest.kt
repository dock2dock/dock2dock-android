package io.dock2dock.android.application.models.query

import java.util.Date

data class FreightConsignmentManifest(
    val id: String,
    val no: String,
    val statusId: String,
    val statusName: String,
    val carrierName: String,
    val carrierName2: String,
    val dispatchDate: Date,
    val preservationStateName: String,
    val singleDestination: Boolean,
    val runSheetNo: String?,
    val pallets : Int,
    val totalWeight: Double,
    val consignmentHeaderCount: Int,
    val crossdockPartnerIntegrationId: String?,
    val crossdockPartnerIntegrationName: String?,
    val crossdockWarehouseLocationId: String?,
    val crossdockWarehouseLocationName: String?,
    val crossdockArrivalDate: Date?,
    val dateCreated: Date
) {
    val crossdockEnabled: Boolean
        get() = crossdockPartnerIntegrationId?.isNotEmpty() ?: false
}