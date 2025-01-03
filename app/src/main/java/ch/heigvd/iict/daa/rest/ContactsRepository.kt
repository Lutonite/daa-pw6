import android.util.Log
import ch.heigvd.iict.daa.rest.database.ContactsDao
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.ContactDTO
import ch.heigvd.iict.daa.rest.models.PhoneType
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    private suspend fun <T> makeRequest(
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

    suspend fun enroll(): String = makeRequest(
        Request.Method.GET,
        "/enroll"
    ) { it }

    suspend fun fetchContacts(uuid: String) = withContext(Dispatchers.IO) {
        val contacts = makeRequest(
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