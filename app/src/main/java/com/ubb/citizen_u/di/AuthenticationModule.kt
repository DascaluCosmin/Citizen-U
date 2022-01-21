package com.ubb.citizen_u.di

import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.data.repositories.AuthenticationRepository
import com.ubb.citizen_u.data.repositories.AuthenticationRepositoryImpl
import com.ubb.citizen_u.domain.usescases.authentication.AuthenticationUseCases
import com.ubb.citizen_u.domain.usescases.authentication.GetCurrentUser
import com.ubb.citizen_u.domain.usescases.authentication.SignIn
import com.ubb.citizen_u.domain.usescases.authentication.SignOut
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthenticationModule {

    @Provides
    @Singleton
    fun providesAuthenticationRepository(firebaseAuth: FirebaseAuth): AuthenticationRepository =
        AuthenticationRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun providesAuthenticationUseCases(authenticationRepository: AuthenticationRepository) =
        AuthenticationUseCases(
            signIn = SignIn(authenticationRepository),
            signOut = SignOut(authenticationRepository),
            getCurrentUser = GetCurrentUser(authenticationRepository)
        )
}