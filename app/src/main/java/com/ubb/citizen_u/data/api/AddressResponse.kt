package com.ubb.citizen_u.data.api

data class AddressResponse(
    val address: Address,
)

data class Address(
    val road: String,
    val suburb: String,
    val city: String,
    val municipality: String,
    val county: String,
    val postcode: String,
    val country: String,
    val country_code: String,
)