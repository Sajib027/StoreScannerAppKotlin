package com.kodakalaris.advisor.network

import com.kodakalaris.advisor.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit 2 Network interface to connect to api
 */
interface ApiService {
    /**
     * register the device corresponding to store with responder name, also passing firebase token
     * @param params request body containing firebase token, responder name, storeid
     * @return
     */
    @POST("api/devices/create")
    fun registerDevice(@Body params: RequestDeviceRegisterApi): Call<RegisterApiResponse>

    /**
     * modify the device registration
     * @param deviceId unique id return from above api response
     * @param params request body containing storeid & responder name
     * @return
     */
    @PUT("api/devices/{deviceId}")
    fun modifyDevice(
        @Path("deviceId") deviceId: String?,
        @Body params: RequestModifyDeviceRegisterApi?
    ): Call<RegisterApiResponse?>?

    /**
     * unregister the device corresponding to last registered store before registering to new store
     * @param deviceId
     * @return
     */
    @DELETE("api/devices/{deviceId}")
    fun unregisterDevice(@Path("deviceId") deviceId: String?): Call<RegisterApiResponse>

    /**
     * get list of all open events
     * @param storeId storeid of the store
     * @param eventStateFilter event state ref. Constants.EVENT_STATE
     * @param start start date (optional)
     * @param kNumber kios number (optional)
     * @param respondedTo responder id (optional)
     * @return
     */
    @GET("api/events")
    fun getEvents(
        @Query("storeId") storeId: String?,
        @Query("eventStateFilter") eventStateFilter: String?,
        @Query("start") start: String?,
        @Query("kNumber") kNumber: String?,
        @Query("respondedTo") respondedTo: String?
    ): Call<GetEventsApiResponse?>?

    /**
     * get events info corresponding to eventid
     * @param eventId eventid of the event
     * @return
     */
    @GET("api/events/{eventId}")
    fun getEventInfo(@Path("eventId") eventId: String?): Call<EventInfoApiResponse?>?

    /**
     * update the event, pick the event from open or pick the event from colleagues
     * @param eventId eventid of the event
     * @param params request body of the updateevent api which contain eventstate & responderid
     * @return
     */
    @PATCH("api/events/{eventId}")
    fun updateEvent(
        @Path("eventId") eventId: String?,
        @Body params: RequestUpdateEventApi?
    ): Call<EventInfoApiResponse?>?

    /**
     * Get the store configuration
     */
    @GET("api/storeconfig/{StoreId}")
    fun getStoreConfiguration(@Path("StoreId") storeId: String?): Call<StoreConfigurationResponse?>?

    /**
     * Update the store configuration
     */
    @PUT("api/storeconfig/{StoreId}")
    fun updateStoreConfiguration(
        @Path("StoreId") storeId: String?,
        @Body params: StoreConfigurationUpdateRequest?
    ): Call<StoreConfigurationResponse?>?

    /**
     * create store configuration
     */
    @POST("api/storeconfig")
    fun createStoreConfiguration(@Body params: StoreConfigurationUpdateRequest?): Call<StoreConfigurationResponse?>?
}
