import android.util.Log
import ch.heigvd.iict.daa.rest.database.ContactsDao
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.ContactDTO
import ch.heigvd.iict.daa.rest.models.PhoneType
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ContactsRepository(
    private val contactsDao: ContactsDao,
    private val queue: RequestQueue
) {
    val allContacts = contactsDao.getAllContactsLiveData()

    companion object {
        private const val BASE_URL = "https://daa.iict.ch"
        private const val HEADER_UUID = "X-UUID"
    }

    private suspend fun <T> getRequest(
        method: Int,
        endpoint: String,
        uuid: String? = null,
        parser: (String) -> T
    ) = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request = object : StringRequest(
                method,
                "$BASE_URL$endpoint",
                { response ->
                    try {
                        Log.d("ContactsRepository", "Received response: $response")
                        continuation.resume(parser(response))
                    } catch (e: Exception) {
                        Log.e("ContactsRepository", "Failed to parse response", e)
                        continuation.resumeWithException(e)
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                    Log.e("ContactsRepository", "Failed to parse response", error)

                }
            ) {
                override fun getHeaders() = uuid?.let {
                    hashMapOf(HEADER_UUID to it)
                } ?: hashMapOf()
            }
            queue.add(request)
        }
    }

    // TODO make this work
    private suspend fun makeRequest(
        method: Int,
        endpoint: String,
        body: JSONObject,
        uuid: String
    ) = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request = object : JsonObjectRequest(
                method,
                "$BASE_URL$endpoint",
                body,
                { response ->
                    Log.d("ContactsRepository", "Received response: $response")
                    continuation.resume(response)
                },
                { error ->
                    continuation.resumeWithException(error)
                    Log.e("ContactsRepository", "Failed to get response", error)
                }
            ) {
                override fun getHeaders() = hashMapOf(HEADER_UUID to uuid)
            }

            queue.add(request)
        }
    }

    suspend fun enroll(): String = getRequest(
        Request.Method.GET,
        "/enroll"
    ) { it }

    suspend fun fetchContacts(uuid: String) = withContext(Dispatchers.IO) {
        val contacts = getRequest(
            Request.Method.GET,
            "/contacts",
            uuid
        ) { json ->
            Gson().fromJson(json, Array<ContactDTO>::class.java)
        }

        contacts.forEach { dto ->
            contactsDao.insert(dto.toContact())
        }
        Log.d("ContactsRepository", "Fetched ${contacts.size} contacts")
        contacts.toList()
    }

    suspend fun create(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        // Save with PENDING state first
        val localContact = contact.copy(state = Contact.State.CREATED)
        val localId = contactsDao.insert(localContact)

        try {
            // Try server sync
            val body = JSONObject(Gson().toJson(contact.toDTO()))
            Log.i("ContactsRepository", "Create body: $body")
            val response = makeRequest(Request.Method.POST, "/contacts", body, uuid)
            val serverContact = Gson().fromJson(response.toString(), ContactDTO::class.java)
            // Update with SYNCED state and server ID if successful
            contactsDao.update(
                localContact.copy(
                    serverId = serverContact.id,
                    state = Contact.State.SYNCED
                )
            )
        } catch (e: Exception) {
            Log.e("ContactsRepository", "Failed to sync with server", e)
            // Keep state (NOT SYNCED)
        }
    }

    suspend fun update(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        // Save with UPDATED state first
        val localContact = contact.copy(state = Contact.State.UPDATED)
        contactsDao.update(localContact)

        try {
            // Try server sync
            val body = JSONObject(Gson().toJson(contact.toDTO()))
            Log.i("ContactsRepository", "Updating contact: $body")
            contactsDao.update(localContact.copy(state = Contact.State.UPDATED))
            val response = makeRequest(
                Request.Method.PUT,
                "/contacts/${contact.serverId}",
                body,
                uuid
            )

            // Update to SYNCED if successful
            contactsDao.update(localContact.copy(state = Contact.State.SYNCED))
        } catch (e: Exception) {
            Log.e("ContactsRepository", "Failed to sync with server", e)
            // Keep UPDATED state
        }
    }


    suspend fun clearAllContacts() = withContext(Dispatchers.IO) {
        contactsDao.clearAllContacts()
    }
}

fun ContactDTO.toContact() = Contact(
    id = null,
    serverId = id,
    name = name,
    firstname = firstname,
    birthday = parseBirthday(birthday),
    email = email,
    address = address,
    zip = zip,
    city = city,
    type = PhoneType.valueOf(type),
    phoneNumber = phoneNumber
)

fun Contact.toDTO() = ContactDTO(
    id = serverId,
    name = name,
    firstname = firstname ?: "",
    birthday = birthday?.let {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            .format(it.time)
    } ?: "",
    email = email ?: "",
    address = address ?: "",
    zip = zip ?: "",
    city = city ?: "",
    type = type?.name ?: "",
    phoneNumber = phoneNumber ?: ""
)

private fun parseBirthday(dateStr: String): Calendar? {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            .parse(dateStr)?.let { date ->
                Calendar.getInstance().apply { time = date }
            }
    } catch (e: Exception) {
        null
    }
}