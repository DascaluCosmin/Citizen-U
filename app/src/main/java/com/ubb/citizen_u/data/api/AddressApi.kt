package com.ubb.citizen_u.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressApi {

    @GET("/reverse")
    suspend fun getAddress(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
    ): Response<AddressResponse>
}