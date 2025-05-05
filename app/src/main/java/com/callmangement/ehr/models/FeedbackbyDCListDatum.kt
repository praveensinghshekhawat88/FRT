package com.callmangement.ehr.models

import java.io.Serializable

class FeedbackbyDCListDatum(
    var year: Int,
    var month: Int,
    var action: Any,
    @JvmField var feedBackId: Int,
    var isStatus: Boolean,
    var createdBy: Int,
    var updatedBy: Int,
    @JvmField var latitude: String,
    @JvmField var longitude: String,
    @JvmField var updatedOn: FeedbackbyDCListUpdatedOn,
    var createdOn: FeedbackbyDCListCreatedOn,
    var msg: String,
    @JvmField var feedBackBy: String,
    var districtId: Int,
    var userId: Any,
    @JvmField var fpscode: String,
    @JvmField var districtName: String,
    @JvmField var selfyWithFPSShopImage: String,
    @JvmField var dealerSignatureImage: String,
    @JvmField var dcVisitingChallan: String,
    var isActive: Boolean,
    @JvmField var remarks: Any,
    @JvmField var locationAddress: String,
    @JvmField var logInTime: String,
    @JvmField var logOutTime: String
) :
    Serializable
