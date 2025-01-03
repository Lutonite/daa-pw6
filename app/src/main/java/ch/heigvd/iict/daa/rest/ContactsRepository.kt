package ch.heigvd.iict.daa.rest

import android.util.Log
import ch.heigvd.iict.daa.rest.database.ContactsDao
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for contacts.
 *
 *
 */
class ContactsRepository(private val contactsDao: ContactsDao,
                            private val queue: RequestQueue
    ) {

    val allContacts = contactsDao.getAllContactsLiveData()
    companion object {
        private const val BASE_URL = "https://daa.iict.ch"
    }

    suspend fun enroll() = suspendCoroutine<String> { cont ->
            val url = "$BASE_URL/enroll"
            val textRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                Log.i("ContactsRepository", "Enroll response: $response")
                        cont.resume(response)
                },
                { error -> cont.resumeWithException(error)
                Log.e(
                    "ContactsRepository",
                    "Failed to enroll",
                    error
                )
                })
            queue.add(textRequest)
    }

    suspend fun clearAllContacts() = withContext(Dispatchers.IO) {
        contactsDao.clearAllContacts()
    }





}


