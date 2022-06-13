package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ubb.citizen_u.data.repositories.PublicSpendingRepository
import com.ubb.citizen_u.data.repositories.impl.PublicSpendingRepositoryImpl
import com.ubb.citizen_u.domain.usescases.publicspending.GetAllPublicSpendingUseCase
import com.ubb.citizen_u.domain.usescases.publicspending.PublicSpendingUseCase
import com.ubb.citizen_u.util.DatabaseConstants.PUBLIC_SPENDING_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PublicSpendingModule {

    @Provides
    @Singleton
    fun providesPublicSpendingUseCase(publicSpendingRepository: PublicSpendingRepository): PublicSpendingUseCase =
        PublicSpendingUseCase(
            getAllPublicSpendingUseCase = GetAllPublicSpendingUseCase(publicSpendingRepository)
        )

    @Provides
    @Singleton
    fun providesPublicSpendingRepository(
        @Named(PUBLIC_SPENDING_COL) publicSpendingRef: CollectionReference,
    ): PublicSpendingRepository =
        PublicSpendingRepositoryImpl(
            publicSpendingRef = publicSpendingRef
        )

    @Provides
    @Singleton
    @Named(PUBLIC_SPENDING_COL)
    fun providesPublicSpendingRef(firebaseFirestore: FirebaseFirestore): CollectionReference =
        firebaseFirestore.collection(PUBLIC_SPENDING_COL)
}