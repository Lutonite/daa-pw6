package ch.heigvd.iict.daa.rest.rest

import ch.heigvd.iict.daa.rest.models.ContactDTO
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.time.OffsetDateTime
import java.util.UUID

private const val HEADER_UUID = "X-UUID"

/**
 * Retrofit API service to interact with the remote contact API
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
interface ContactApiService {
    /**
     * Enroll and get a new UUID token
     * @return UUID token as String
     */
    @GET("enroll")
    suspend fun enroll(): UUID

    /**
     * Get all contacts for the given UUID
     * @param uuid The UUID token obtained from enroll
     * @return List of contacts
     */
    @GET("contacts")
    suspend fun getContacts(
        @Header(HEADER_UUID) uuid: UUID
    ): List<ContactDTO>

    /**
     * Get a specific contact by ID
     * @param uuid The UUID token obtained from enroll
     * @param contactId The ID of the contact to retrieve
     * @return The requested contact
     */
    @GET("contacts/{id}")
    suspend fun getContact(
        @Header(HEADER_UUID) uuid: UUID,
        @Path("id") contactId: Long
    ): ContactDTO

    /**
     * Create a new contact
     * @param uuid The UUID token obtained from enroll
     * @param contact The contact object to create (id should be null)
     * @return The created contact with assigned ID
     */
    @POST("contacts")
    suspend fun createContact(
        @Header(HEADER_UUID) uuid: UUID,
        @Body contact: ContactDTO
    ): ContactDTO

    /**
     * Update an existing contact
     * @param uuid The UUID token obtained from enroll
     * @param contactId The ID of the contact to update
     * @param contact The updated contact object
     * @return The updated contact
     */
    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Header(HEADER_UUID) uuid: UUID,
        @Path("id") contactId: Long,
        @Body contact: ContactDTO
    ): ContactDTO

    /**
     * Delete a contact
     * @param uuid The UUID token obtained from enroll
     * @param contactId The ID of the contact to delete
     */
    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Header(HEADER_UUID) uuid: UUID,
        @Path("id") contactId: Long
    )

    companion object {
        // Extension function to create the API service
        fun create(baseUrl: String = "https://daa.iict.ch/"): ContactApiService {
            val gson = GsonBuilder()
                .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter)
                .create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ContactApiService::class.java)
        }
    }
}
