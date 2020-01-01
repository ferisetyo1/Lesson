package feri.com.lesson.util

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar

object SBCustom {
    fun getSnackbar(v: View, s: String): Snackbar {
        var snackbar = Snackbar.make(v, s, Snackbar.LENGTH_LONG)
        val vSB = snackbar.view
        val params = vSB.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        vSB.layoutParams = params
        return snackbar
    }
}