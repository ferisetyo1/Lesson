package feri.com.lesson.modul.util

object KeyGen {

    fun create(prefix: String,separator:String, length: Int): String {
        val STRING_CHARACTERS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return "${prefix}${separator}" + (1..length).map { STRING_CHARACTERS.random() }.joinToString("")
    }

}