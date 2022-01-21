package com.ubb.citizen_u.domain.usescases.authentication

import com.ubb.citizen_u.data.repositories.AuthenticationRepository

class SignOut(
    private val repository: AuthenticationRepository
) {

    suspend operator fun invoke() {
        repository.signOut()
    }
}