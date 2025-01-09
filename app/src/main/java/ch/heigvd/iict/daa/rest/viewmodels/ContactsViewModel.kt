package ch.heigvd.iict.daa.rest.viewmodels

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ch.heigvd.iict.daa.rest.ContactsApplication
import ch.heigvd.iict.daa.rest.models.Contact
import kotlinx.coroutines.launch
import java.util.UUID

private const val CONTACTS_PREFS_CONTEXT_KEY = "contacts_prefs"
private const val CONTACTS_PREFS_KEY_TOKEN = "enrollment_token"

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository
    private val sharedPreferences = application.getSharedPreferences(
        CONTACTS_PREFS_CONTEXT_KEY,
        Context.MODE_PRIVATE
    )

    private var userEnrollmentToken: UUID?
        get() = sharedPreferences
            .getString(CONTACTS_PREFS_KEY_TOKEN, null)
            ?.let { UUID.fromString(it) }
        set(value) = with(sharedPreferences.edit()) {
            putString(CONTACTS_PREFS_KEY_TOKEN, value?.toString())
            apply()
        }

    val allContacts = repository.allContacts

    private var _editionMode: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _selectedContact: MutableLiveData<Contact?> = MutableLiveData(null)
    val editionMode: LiveData<Boolean> get() = _editionMode
    val selectedContact: LiveData<Contact?> get() = _selectedContact

    fun startEdition(contact: Contact? = null) {
        _editionMode.value = true
        _selectedContact.value = contact
    }

    fun stopEdition() {
        _editionMode.value = false
        _selectedContact.value = null
    }

    fun enroll() = viewModelScope.launch {
        // Clear existing contacts
        repository.deleteContacts()

        // Get new UUID from server
        userEnrollmentToken = repository.enroll()
        if (userEnrollmentToken == null) {
            return@launch
        }

        // Fetch contacts from server
        repository.fetchContacts(userEnrollmentToken!!)
    }

    fun refresh() = viewModelScope.launch {
        repository.refreshContacts(userEnrollmentToken)
    }

    fun create(contact: Contact) = viewModelScope.launch {
        repository.saveContact(contact, userEnrollmentToken)
    }

    fun update(contact: Contact) = viewModelScope.launch {
        repository.saveContact(contact, userEnrollmentToken)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        repository.deleteContact(contact, userEnrollmentToken)
    }

    companion object {

        /**
         * Factory for the [ContactsViewModel].
         *
         * @author Emilie Bressoud
         * @author Loïc Herman
         * @author Sacha Butty
         */
        val Factory = viewModelFactory {
            initializer { ContactsViewModel(requireNotNull(this[APPLICATION_KEY]) as ContactsApplication) }
        }
    }
}