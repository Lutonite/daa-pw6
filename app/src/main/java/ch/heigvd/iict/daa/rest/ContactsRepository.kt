package ch.heigvd.iict.daa.rest

import ch.heigvd.iict.daa.rest.database.ContactsDao

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

}