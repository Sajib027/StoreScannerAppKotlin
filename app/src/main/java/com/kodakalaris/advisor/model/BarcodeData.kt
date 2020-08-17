package com.kodakalaris.advisor.model

import com.google.gson.annotations.SerializedName

/**
 * modal for barcode data,
 * required at the time of parsing barcode
 */
class BarcodeData {
    /**
     * store-info : {"store-id":"1234","name":"dm Stuttgart-Hedelfingen","addr-1":"Hedelfinger Str. 60","addr-2":"","city":"Stuttgart","state":"BW","postal-code":"70327","country-code":"DEU","phone":"+49 (0)711 123456789","retailer-id":"Kodak Moments"}
     */
    @SerializedName("store-info")
    var storeinfo: StoreBean? = null

    class StoreBean {
        /**
         * store-id : 1234
         * name : dm Stuttgart-Hedelfingen
         * addr-1 : Hedelfinger Str. 60
         * addr-2 :
         * city : Stuttgart
         * state : BW
         * postal-code : 70327
         * country-code : DEU
         * phone : +49 (0)711 123456789
         * retailer-id : Kodak Moments
         */
        @SerializedName("store-id")
        var storeid: String? = null
        var name: String? = null

        @SerializedName("addr-1")
        var addr1: String? = null

        @SerializedName("addr-2")
        var addr2: String? = null
        var city: String? = null
        var state: String? = null

        @SerializedName("postal-code")
        var postalcode: String? = null

        @SerializedName("country-code")
        var countrycode: String? = null
        var phone: String? = null

        @SerializedName("retailer-id")
        var retailerid: String? = null

    }
}
