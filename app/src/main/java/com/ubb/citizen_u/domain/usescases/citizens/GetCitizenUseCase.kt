package com.ubb.citizen_u.domain.usescases.citizens

import com.ubb.citizen_u.data.repositories.CitizenRepository
import javax.inject.Inject

class GetCitizenUseCase @Inject constructor(
    private val citizenRepository: CitizenRepository
) {
    suspend operator fun invoke(userId: String) =
        citizenRepository.getCitizen(userId)
}