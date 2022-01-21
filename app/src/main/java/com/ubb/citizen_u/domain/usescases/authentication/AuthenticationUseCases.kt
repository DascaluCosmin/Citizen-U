package com.ubb.citizen_u.domain.usescases.authentication

data class AuthenticationUseCases(
    val signIn: SignIn,
    val getCurrentUser: GetCurrentUser
)