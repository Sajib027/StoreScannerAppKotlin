package com.kodakalaris.advisor.model

import org.json.JSONException
import org.json.JSONObject

/**
 * request body of updateevent api
 */
class RequestUpdateEventApi
/**
 * State : BEING_WORKED_ON
 */(var State: String, var ResponderId: String?) {

    override fun toString(): String {
        val reqObj = JSONObject()
        try {
            reqObj.put("State", State)
            reqObj.put("ResponderId", ResponderId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return reqObj.toString()
    }

}
