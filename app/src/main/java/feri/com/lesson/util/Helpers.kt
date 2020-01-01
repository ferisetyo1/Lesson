package feri.com.lesson.util

import java.util.concurrent.TimeUnit

object Helpers {

    fun dateCheckisTrueDate(string: String): Boolean {
        if (string.length == 10) {
            var tanggal = string.substring(0, 2).toIntOrNull()
            var bulan = string.substring(3, 5).toIntOrNull()
            var tahun = string.substring(6, 10).toIntOrNull()
            if (tanggal.toString().equals("null") ||
                bulan.toString().equals("null") ||
                tahun.toString().equals("null") ||
                tanggal!! > 31 ||
                bulan!! >12
            ) {
                return false
            } else {
                return true
            }
        } else {
            return false
        }
    }

    public fun longtoDate(millis: Long):String{
        return String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

}