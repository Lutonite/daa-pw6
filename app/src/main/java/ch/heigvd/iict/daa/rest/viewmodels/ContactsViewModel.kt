package ch.heigvd.iict.daa.rest.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.daa.rest.ContactsApplication
import ch.heigvd.iict.daa.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository

    val allContacts = repository.allContacts

    fun enroll() {
        viewModelScope.launch {
            try {
                // Clear existing contacts
                repository.clearAllContacts()
                
                // Get new UUID from server
                val uuid = repository.enroll()
                Log.i("ContactsViewModel", "Enrolled with UUID: $uuid")
                
                // Save UUID in preferences
                getApplication<ContactsApplication>().saveUUID(uuid)
                
                // Fetch initial contacts
                repository.fetchContacts(uuid)
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Failed to enroll", e)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            //TODO
        }
    }

    fun create(contact: Contact) {
        viewModelScope.launch {
            try {
                repository.create(
                    contact,
                    getApplication<ContactsApplication>().getUUID()
                        ?: throw IllegalStateException("UUID not found")
                )
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Failed to create contact", e)
            }
        }
    }

    fun update(contact: Contact) {
        viewModelScope.launch {
            try {
                val uuid = getApplication<ContactsApplication>().getUUID()
                    ?: throw IllegalStateException("UUID not found")
                // Update locally first with UPDATED state
                repository.update(contact, uuid)
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Failed to update contact", e)
            }
        }
    }

    fun delete(contact: Contact) {
        viewModelScope.launch {
            try {
                val uuid = getApplication<ContactsApplication>().getUUID()
                    ?: throw IllegalStateException("UUID not found")
                // Update locally first with DELETED state
                //repository.delete(contact, uuid)
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Failed to delete contact", e)
            }
        }
    }
}

class ContactsViewModelFactory(private val application: ContactsApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}