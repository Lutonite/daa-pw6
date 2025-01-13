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

/**
 * Main [AndroidViewModel] for contacts in the application. Instantiated using the [Factory].
 * It handles storing the contact edition state as well as connecting the repository to the UI.
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    // Fetch the repository lazily instantiated by the application
    private val repository = application.repository
    // Get shared preferences to persist the UUID enrollment token
    private val sharedPreferences = application.getSharedPreferences(
        CONTACTS_PREFS_CONTEXT_KEY,
        Context.MODE_PRIVATE
    )

    // Mapping between the UUID and the shared preferences, if the user is logged out then this
    // value is null, requiring an enrollment. The repository will reject calls to the API if a
    // token is not set for a call that requires one.
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