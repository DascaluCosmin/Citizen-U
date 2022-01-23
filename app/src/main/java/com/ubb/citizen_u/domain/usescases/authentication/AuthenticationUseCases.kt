package com.ubb.citizen_u.domain.usescases.authentication

data class AuthenticationUseCases(
    val signIn: SignIn,
    val signOut: SignOut,
    val getCurrentUser: GetCurrentUser,
    val resetUserPassword: ResetUserPassword,
)