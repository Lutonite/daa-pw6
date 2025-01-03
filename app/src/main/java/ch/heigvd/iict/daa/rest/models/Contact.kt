package ch.heigvd.iict.daa.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String,
    var firstname: String?,
    var birthday: Calendar?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?,
    // Synchronization properties
   var serverId: Long? = null,
   @Transient var state: State = State.SYNCED,
) {
    enum class State {
        SYNCED,  // The contact is in sync with the server
        CREATED, // The contact has been created locally
        UPDATED, // The contact has been updated locally
        DELETED, // The contact has been deleted locally
    }
    constructor(
        name: String,
        firstname: String?,
        birthday: Calendar?,
        email: String?,
        address: String?,
        zip: String?,
        city: String?,
        type: PhoneType?,
        phoneNumber: String?,
        serverId: Long?
    ) : this(null, name, firstname, birthday, email, address, zip, city, type, phoneNumber,null)

}