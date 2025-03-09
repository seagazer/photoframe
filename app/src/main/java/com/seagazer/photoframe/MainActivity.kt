package com.seagazer.photoframe

import android.annotation.SuppressLint
import android.content.Intent
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.function.ToIntFunction

class MainActivity : AppCompatActivity() {
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.btn_selector).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                setType("image/*")
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 20)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            this.startActivityForResult(intent, 0x111)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.clipData?.getItemAt(0)?.uri?.let {
            Log.d("abc", "1------" + it)
            val cursor = contentResolver.query(it, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            Log.d("abc", "2------" + cursor)
            val index = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            Log.d("abc", "3------" + index)
            cursor?.moveToFirst()
            val path = cursor?.getString(index!!)
            Log.d("abc", "4------" + path)
            cursor?.close()
            val exif = ExifInterface(path!!)
            Log.d("abc", "------" + exif.getAttribute(ExifInterface.TAG_MAKE))
            Log.d("abc", "------" + exif.getAttribute(ExifInterface.TAG_MODEL))
            Log.d("abc", "iso------" + exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS))
            Log.d("abc", "快门------" + exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME))
            Log.d("abc", "光圈------" + exif.getAttribute(ExifInterface.TAG_F_NUMBER))
            Log.d("abc", "焦距------" + exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH))
            findViewById<TextView>(R.id.make).text = exif.getAttribute(ExifInterface.TAG_MAKE)
            findViewById<TextView>(R.id.model).text = exif.getAttribute(ExifInterface.TAG_MODEL)
            findViewById<TextView>(R.id.speed).text = "ISO " + exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
            val res = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.toFloat()!! * 100
            Log.d("abc", "快门2------$res")
            Log.d("abc", "快门3------$res")
            findViewById<TextView>(R.id.etime).text = Math.round(res).toString() + "/100s"
            findViewById<TextView>(R.id.fnumber).text = "f" + exif.getAttribute(ExifInterface.TAG_F_NUMBER)
            val fl = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val sts = fl!!.split("/")
            val flv = sts[0].toInt() / sts[1].toInt()
            Log.d("abc", "焦距2------$flv")

            findViewById<TextView>(R.id.flength).text = flv.toString() + "mm"

            findViewById<ImageView>(R.id.logo).setImageURI(it)
        }
    }
}