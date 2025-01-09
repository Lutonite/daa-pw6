package ch.heigvd.iict.daa.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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
    var state: State = State.CREATED,
) {
    enum class State {
        SYNCED,  // The contact is in sync with the server
        CREATED, // The contact has been created locally
        UPDATED, // The contact has been updated locally
        DELETED, // The contact has been deleted locally
    }

    val synced get() = state == State.SYNCED;
}

data class ContactDTO(
    var id: Long?,
    var name: String,
    var firstname: String?,
    var birthday: OffsetDateTime?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?,
)

fun Contact.toDTO(): ContactDTO = ContactDTO(
    id = serverId,
    name = name,
    firstname = firstname,
    birthday = birthday?.toInstant()?.atOffset(ZoneOffset.UTC),
    email = email,
    address = address,
    zip = zip,
    city = city,
    type = type,
    phoneNumber = phoneNumber
)

fun ContactDTO.toContact(
    localId: Long? = null,
    state: Contact.State = Contact.State.SYNCED
) = Contact(
    id = localId,
    serverId = id,
    state = state,
    name = name,
    firstname = firstname,
    birthday = birthday?.let {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(it.offset))
        calendar.time = Date.from(it.toInstant())
        calendar
    },
    email = email,
    address = address,
    zip = zip,
    city = city,
    type = type,
    phoneNumber = phoneNumber,
)
