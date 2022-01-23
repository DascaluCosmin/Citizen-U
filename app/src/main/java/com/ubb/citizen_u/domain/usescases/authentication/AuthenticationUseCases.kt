package com.ubb.citizen_u.domain.usescases.authentication

data class AuthenticationUseCases(
    val signInUseCase: SignInUseCase,
    val signOutUseCase: SignOutUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val resetUserPasswordUseCase: ResetUserPasswordUseCase,
    val registerUserUseCase: RegisterUserUseCase,
)