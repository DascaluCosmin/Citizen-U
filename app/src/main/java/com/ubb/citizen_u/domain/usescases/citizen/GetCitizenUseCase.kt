package com.ubb.citizen_u.domain.usescases.citizen

import com.ubb.citizen_u.data.repositories.CitizenRepository

class GetCitizenUseCase(
    private val citizenRepository: CitizenRepository
) {
    suspend operator fun invoke(userId: String) =
        citizenRepository.getCitizen(userId)
}