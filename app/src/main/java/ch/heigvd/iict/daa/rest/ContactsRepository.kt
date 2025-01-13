package ch.heigvd.iict.daa.rest

import android.util.Log
import ch.heigvd.iict.daa.rest.database.ContactsDao
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.toContact
import ch.heigvd.iict.daa.rest.models.toDTO
import ch.heigvd.iict.daa.rest.rest.ContactApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repository handling data operations for contacts on the local database and remote server
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class ContactsRepository(
    private val contactsDao: ContactsDao,
    private val contactService: ContactApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val allContacts = contactsDao.getContactsLiveData()

    suspend fun enroll(): UUID? = try {
        contactService.enroll()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to enroll with server", e)
        null
    }

    suspend fun fetchContacts(uuid: UUID) = withContext(dispatcher) {
        try {
            val contacts = contactService.getContacts(uuid)
            contacts.forEach { dto -> contactsDao.insert(dto.toContact()) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync fetch with server", e)
        }
    }

    suspend fun getContact(localContactId: Long, uuid: UUID?): Contact? = withContext(dispatcher) {
        val localContact = contactsDao.getContactById(localContactId)
        // NOTE: we only fetch contacts which were already properly synced on the server,
        //       so that local modifications are not discarded. A better conflict resolution could
        //       later be implemented here.
        if (localContact?.serverId == null || !localContact.synced) {
            return@withContext localContact
        }

        if (uuid == null) {
            return@withContext localContact
        }

        return@withContext try {
            val serverContact = contactService
                .getContact(uuid, localContact.serverId!!)
                .toContact(localContact.id)

            contactsDao.update(serverContact)
            serverContact
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync fetch with server", e)
            localContact
        }
    }

    suspend fun saveContact(contact: Contact, uuid: UUID?) = withContext(dispatcher) {
        // Save locally as CREATED state, then attempt to sync
        contact.state = Contact.State.CREATED
        if (contact.id == null) {
            contact.id = contactsDao.insert(contact)
        } else {
            contact.state = Contact.State.UPDATED
            contactsDao.update(contact)
        }

        if (uuid == null) {
            return@withContext
        }

        // Attempt to sync with server
        try {
            val response = if (contact.serverId != null) {
                contactService.updateContact(uuid, contact.serverId!!, contact.toDTO())
            } else {
                contactService.createContact(uuid, contact.toDTO())
            }

            val serverContact = response.toContact(contact.id)
            contactsDao.update(serverContact)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync save with server", e)
            // Will stay at current state until next sync
        }
    }

    suspend fun deleteContact(contact: Contact, uuid: UUID?) = withContext(dispatcher) {
        // If it is not synced with a server entity we can delete it straight away
        if (contact.serverId == null) {
            contactsDao.delete(contact)
            return@withContext
        }

        // Save with DELETED state first
        contact.state = Contact.State.DELETED
        contactsDao.update(contact)

        if (uuid == null) {
            return@withContext
        }

        // Attempt to sync with server
        try {
            contactService.deleteContact(uuid, contact.serverId!!)
            contactsDao.delete(contact)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync deletion with server", e)
            // Keep DELETED state
        }
    }

    suspend fun refreshContacts(uuid: UUID?) = withContext(dispatcher) {
        if (uuid == null) {
            return@withContext
        }

        contactsDao.getContacts(
            Contact.State.CREATED,
            Contact.State.UPDATED,
            Contact.State.DELETED
        ).forEach { contact ->
            when (contact.state) {
                Contact.State.CREATED, Contact.State.UPDATED -> saveContact(contact, uuid)
                Contact.State.DELETED -> deleteContact(contact, uuid)
                else -> {}
            }
        }
    }

    suspend fun deleteContacts() = withContext(dispatcher) {
        contactsDao.clearAllContacts()
    }

    companion object {
        private val TAG = ContactsRepository::class.simpleName
    }
}