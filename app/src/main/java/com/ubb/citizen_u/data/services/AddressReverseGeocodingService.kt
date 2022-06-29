package com.ubb.citizen_u.data.services

import com.ubb.citizen_u.data.api.AddressApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AddressReverseGeocodingService {

    val addressReverseGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AddressApi::class.java)
    }
}