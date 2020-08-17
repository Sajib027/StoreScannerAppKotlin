package com.kodakalaris.advisor.model

import org.json.JSONException
import org.json.JSONObject

/**
 * request body of modifydeviceregistration api
 */
class RequestModifyDeviceRegisterApi
/**
 * StoreId : 1234
 * ResponderName : John
 */
    (var StoreId: String, var Responder: String) {

    override fun toString(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("StoreId", StoreId)
            jsonObject.put("Responder", Responder)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

}