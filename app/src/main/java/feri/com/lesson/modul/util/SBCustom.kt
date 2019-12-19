package feri.com.lesson.modul.util

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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