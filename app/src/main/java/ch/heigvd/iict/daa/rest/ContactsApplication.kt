package ch.heigvd.iict.daa.rest

import ContactsRepository
import android.app.Application
import android.content.Context
import ch.heigvd.iict.daa.rest.database.ContactsDatabase
import com.android.volley.toolbox.Volley

class ContactsApplication : Application() {
    private val PREFS_NAME = "ContactsPrefs"
    private val UUID_KEY = "uuid_key"
    
    fun saveUUID(uuid: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(UUID_KEY, uuid)
            .apply()
    }

    fun getUUID(): String? {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(UUID_KEY, null)
    }

    private val database by lazy { ContactsDatabase.getDatabase(this) }
    private val queue by lazy { Volley.newRequestQueue(this) }
    val repository by lazy { ContactsRepository(database.contactsDao(), queue) }

}