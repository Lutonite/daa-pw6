package ch.heigvd.iict.daa.rest

import android.app.Application
import ch.heigvd.iict.daa.rest.database.ContactsDatabase
import ch.heigvd.iict.daa.rest.rest.ContactApiService

/**
 * Main application class for the contacts application
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class ContactsApplication : Application() {

    private val database by lazy { ContactsDatabase.getDatabase(this) }

    private val contactService by lazy { ContactApiService.create() }

    val repository by lazy { ContactsRepository(database.contactsDao(), contactService) }
}