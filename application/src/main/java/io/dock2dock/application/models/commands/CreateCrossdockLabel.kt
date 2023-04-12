package io.dock2dock.application.models.commands

data class CreateCrossdockLabel(val salesOrderNo: String,
                                 var handlingUnitId: String,
                                 var quantity: Int,
                                 var printerId: String)

;