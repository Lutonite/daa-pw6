package ch.heigvd.iict.daa.rest

import android.app.Application
import ch.heigvd.iict.daa.rest.database.ContactsDatabase

class ContactsApplication : Application() {

    private val database by lazy { ContactsDatabase.getDatabase(this) }

    val repository by lazy { ContactsRepository(database.contactsDao()) }
}