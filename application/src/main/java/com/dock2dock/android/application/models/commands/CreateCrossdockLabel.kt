package com.dock2dock.android.application.models.commands

data class CreateCrossdockLabel(val salesOrderId: String,
                                 var handlingUnitId: String,
                                 var quantity: Int,
                                 var printerId: String)

;