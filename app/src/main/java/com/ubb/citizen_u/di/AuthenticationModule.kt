package com.ubb.citizen_u.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import com.ubb.citizen_u.data.repositories.impl.AuthenticationRepositoryImpl
import com.ubb.citizen_u.domain.usescases.authentication.*
import com.ubb.citizen_u.util.DatabaseConstants.USERS_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun providesAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
        @Named(USERS_COL) usersRef: CollectionReference
    ): AuthenticationRepository =
        AuthenticationRepositoryImpl(
            firebaseAuth = firebaseAuth,
            usersRef = usersRef
        )

    @Provides
    @Singleton
    fun providesAuthenticationUseCases(authenticationRepository: AuthenticationRepository) =
        AuthenticationUseCases(
            signInUseCase = SignInUseCase(authenticationRepository),
            signOutUseCase = SignOutUseCase(authenticationRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authenticationRepository),
            resetUserPasswordUseCase = ResetUserPasswordUseCase(authenticationRepository),
            registerUserUseCase = RegisterUserUseCase(authenticationRepository)
        )
}