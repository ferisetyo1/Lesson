package feri.com.lesson.util

import com.google.firebase.database.FirebaseDatabase

class DBHelper {
    companion object {
        private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

        init {
            firebaseDatabase.setPersistenceEnabled(true)
        }

        fun getDatabase() = firebaseDatabase
    }
}