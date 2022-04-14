package com.ubb.citizen_u.util.di

import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    private const val CONFIDENCE_THRESHOLD = 0.45f

    @Provides
    @Singleton
    fun providesImageLabeler(): ImageLabeler {
        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build()

        val options = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(CONFIDENCE_THRESHOLD)
            .build()
        return ImageLabeling.getClient(options)
    }
}