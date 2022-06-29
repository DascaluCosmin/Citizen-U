package com.ubb.citizen_u.domain.usescases.publicspending

import com.ubb.citizen_u.data.repositories.PublicSpendingRepository
import javax.inject.Inject

class GetAllPublicSpendingUseCase @Inject constructor(
    private val publicSpendingRepository: PublicSpendingRepository,
) {
    suspend operator fun invoke() = publicSpendingRepository.getAllPublicSpending()
}
