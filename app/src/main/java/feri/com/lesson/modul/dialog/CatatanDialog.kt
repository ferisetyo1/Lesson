package feri.com.lesson.modul.dialog

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import feri.com.lesson.R
import feri.com.lesson.model.CatatanModel
import feri.com.lesson.util.Helpers
import kotlinx.android.synthetic.main.modal_catatan.*

/**
 * A simple [Fragment] subclass.
 */
class CatatanDialog(model: CatatanModel) : DialogFragment() {

    val model = model

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_catatan, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (model.extraUrl.isNullOrEmpty()) {
            lyt_extra.visibility = View.GONE
        } else if (model.extraUrl!!.contains(".jpg", true)) {
            ExtraVideo.visibility = View.GONE
            Glide.with(this)
                .asDrawable()
                .load(model.extraUrl)
                .transition(withCrossFade())
                .fitCenter()
                .into(ExtraFoto)
        } else {
            ExtraFoto.visibility = View.GONE
            label_extra.text = "Video Kegiatan"
            ExtraVideo.setVideoURI(Uri.parse(model.extraUrl))
            var mediaController = MediaController(ExtraVideo.context)
            ExtraVideo.setOnPreparedListener {
                it.setOnVideoSizeChangedListener { mediaPlayer, i, i2 ->
                    ExtraVideo.setMediaController(mediaController)
                    mediaController.setAnchorView(ExtraVideo)
                    (mediaController.parent as ViewGroup).removeView(mediaController)
                    videoViewWrapper.addView(mediaController)
                    mediaController.visibility = View.VISIBLE
                    mediaController.requestFocus()
                }
                ExtraVideo.start()
            }
        }
        judul.text = model.judul
        waktu.text = Helpers.longtoTimeFormat(model.waktu!!)
        tipe_kegiatan.text = resources
            .getStringArray(R.array.tipe_kegiatan)
            .get(model.tipe!!)
        deskripsi.text = model.deskripsi
        ic_tutup.setOnClickListener { dialog?.dismiss() }
        btn_tutup.setOnClickListener { dialog?.dismiss() }
    }

}
