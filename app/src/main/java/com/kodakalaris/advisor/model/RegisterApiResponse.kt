package com.kodakalaris.advisor.model

/**
 * Api Response body modal
 */
class RegisterApiResponse {
    /**
     * Error : 0
     * Message :
     */
    var Error = 0
    var DeviceId: String? = null
    var Message: String? = null
    var Platform: String? = null
    var Responder: String? = null
    var StoreId: String? = null

    override fun toString(): String {
        return "RegisterApiResponse{" +
                "Error=" + Error +
                ", DeviceId='" + DeviceId + '\'' +
                ", Message='" + Message + '\'' +
                ", Platform='" + Platform + '\'' +
                ", Responder='" + Responder + '\'' +
                ", StoreId='" + StoreId + '\'' +
                '}'
    }
}