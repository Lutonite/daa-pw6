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

    @Query("SELECT * FROM Contact WHERE state IN (:states)")
    fun getContacts(
        vararg states: Contact.State = arrayOf(
            Contact.State.SYNCED,
            Contact.State.CREATED,
            Contact.State.UPDATED
        )
    ): List<Contact>

    @Query("SELECT * FROM Contact WHERE state IN (:states)")
    fun getContactsLiveData(
        vararg states: Contact.State = arrayOf(
            Contact.State.SYNCED,
            Contact.State.CREATED,
            Contact.State.UPDATED
        )
    ): LiveData<List<Contact>>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id: Long): Contact?

    @Query("SELECT COUNT(*) FROM Contact WHERE state IN (:states)")
    fun getCount(
        vararg states: Contact.State = arrayOf(
            Contact.State.SYNCED,
            Contact.State.CREATED,
            Contact.State.UPDATED
        )
    ): Int

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

}