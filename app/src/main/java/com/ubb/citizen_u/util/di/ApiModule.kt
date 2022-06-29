package com.ubb.citizen_u.util.di

import com.ubb.citizen_u.data.api.AddressApi
import com.ubb.citizen_u.data.services.AddressReverseGeocodingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesAddressApi(): AddressApi = AddressReverseGeocodingService.addressReverseGeocodingApi
}