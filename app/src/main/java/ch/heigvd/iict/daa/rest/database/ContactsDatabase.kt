package ch.heigvd.iict.daa.rest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.heigvd.iict.daa.rest.database.converters.CalendarConverter
import ch.heigvd.iict.daa.rest.models.Contact

@Database(entities = [Contact::class], version = 2, exportSchema = true)
@TypeConverters(CalendarConverter::class)
abstract class ContactsDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao

    companion object {

        @Volatile
        private var INSTANCE: ContactsDatabase? = null

        fun getDatabase(context: Context): ContactsDatabase {

            return INSTANCE ?: synchronized(this) {
                val _instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java, "contacts.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = _instance
                _instance
            }
        }
    }
}