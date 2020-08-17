package com.kodakalaris.advisor.model

import org.json.JSONException
import org.json.JSONObject

/**
 * Device Register Api Request body modal
 */
class RequestDeviceRegisterApi {
    /**
     * Platform : fcm
     * StoreId : 1234
     * Responder : John
     */
    var PNSIdentifier: String? = null
    var Platform: String? = null
    var StoreId: String? = null
    var Responder: String? = null

    constructor() {}
    constructor(PNSIdentifier: String?, Platform: String?, StoreId: String?, Responder: String?) {
        this.PNSIdentifier = PNSIdentifier
        this.Platform = Platform
        this.StoreId = StoreId
        this.Responder = Responder
    }

    override fun toString(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("PNSIdentifier", PNSIdentifier)
            jsonObject.put("Platform", Platform)
            jsonObject.put("StoreId", StoreId)
            jsonObject.put("Responder", Responder)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }
}