package ch.heigvd.iict.daa.rest.models


data class ContactDTO(
    val id: Long?,
    val name: String,
    val firstname: String,
    val birthday: String,  // ISO format
    val email: String,
    val address: String,
    val zip: String,
    val city: String,
    val type: String,
    val phoneNumber: String
) {
}