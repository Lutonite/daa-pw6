package ch.heigvd.iict.daa.rest

import android.app.Application
import android.content.Context
import android.util.Log
import ch.heigvd.iict.daa.rest.database.ContactsDatabase
import com.android.volley.toolbox.Volley
import com.google.android.gms.security.ProviderInstaller
import javax.net.ssl.SSLContext

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


    override fun onCreate() {
        super.onCreate()
        updateSecurityProvider()
    }

    private fun updateSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this)
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: Exception) {
            Log.e("ContactsApplication", "Error updating security provider", e)
        }
    }
}