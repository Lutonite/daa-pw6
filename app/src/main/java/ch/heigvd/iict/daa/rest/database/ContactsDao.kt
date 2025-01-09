package ch.heigvd.iict.daa.rest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.daa.rest.models.Contact

@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: Contact): Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    fun softDelete(contact: Contact) {
        contact.state = Contact.State.DELETED
        update(contact)
    }

    @Query("SELECT * FROM Contact WHERE state != :state")
    fun getAllContactsLiveData(state: Contact.State = Contact.State.DELETED): LiveData<List<Contact>>

    @Query("SELECT * FROM Contact WHERE state != :state")
    fun getAllUnsyncedContacts(state: Contact.State = Contact.State.SYNCED): List<Contact>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id : Long) : Contact?

    @Query("SELECT COUNT(*) FROM Contact")
    fun getCount() : Int

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

}