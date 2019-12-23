package pet.loyal.provider.view.dialog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_dialog_enlarge_photo.*
import pet.loyal.provider.R
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.showToast
import pet.loyal.provider.view.home.HomeScreen
import java.io.File
import java.io.FileOutputStream

class ImageEnlargeFragment : DialogFragment(), FragmentActivityControlListener {


    private val folder = "Loyal"

    lateinit var os: FileOutputStream
    lateinit var imgview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_dialog_enlarge_photo, container, false)
        imgview = view.findViewById(R.id.img_download_image)
        imgview.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1919
                )
            } else {
                downloadImage()
            }
        }

        view.findViewById<ConstraintLayout>(R.id.layoutMain).setOnClickListener {
            dismiss()
        }

        view.findViewById<ImageView>(R.id.img_enlarge_photo).setOnClickListener {}

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.bg_color_download_photo, null)))
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private fun downloadImage() {
        val drawable: BitmapDrawable? = img_enlarge_photo.drawable as BitmapDrawable
        if (drawable != null) {
            val bitmap: Bitmap? = drawable.bitmap
            if (bitmap != null) {
                val filename = getFilename()
                os = FileOutputStream(filename)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

            }
        }
    }

    //     load the image once the image is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as HomeScreen
        activity.setPermissionGrantedListener(this)
        loadImage()
    }

    private fun loadImage() {
        if (arguments != null) {
            if (arguments!!.containsKey(Constants.extra_image_id)) {
                Picasso.get()
                    .load(arguments!!.getString(Constants.extra_image_id))
                    .placeholder(R.drawable.ic_add_photo)
                    .into(img_enlarge_photo)
            }
        }
    }

    private fun getFilename(): String {
        val filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
        val file = File(filepath, folder)

        if (!file.exists()) {
            file.mkdirs()
        }

        val tempFileName = "img" + System.currentTimeMillis()

        return file.absolutePath + "/" + tempFileName + ".png"
    }

    private fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1919 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                downloadImage()
//            } else {
//                showToast(context!!, getString(R.string.error_permission_denied))
//            }
//
//        }
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1919 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage()
            } else {
                showToast(context!!, getString(R.string.error_permission_denied))
            }
        }
    }

    override fun onPermissionGranted(granted: Boolean , requestCode: Int) {
        if (granted) {
            downloadImage()
        } else {
            showToast(context!!, getString(R.string.error_permission_denied))
        }
    }

    override fun onActivityResultListener(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}