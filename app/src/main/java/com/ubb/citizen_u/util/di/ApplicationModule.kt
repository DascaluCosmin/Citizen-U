package com.ubb.citizen_u.util.di

import com.ubb.citizen_u.util.networking.NetworkReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun providesNetworkReceiver(): NetworkReceiver = NetworkReceiver()
}