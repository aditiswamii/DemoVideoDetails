package com.example.demovideodetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File

class MainActivity : AppCompatActivity() {
    private var btn: Button? = null
    private var videoView: VideoView? = null
    private val VIDEO_DIRECTORY = "/demonutsVideoooo"
    private val GALLERY = 1
    private val CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestMultiplePermissions()

        btn = findViewById(R.id.btn) as Button
        videoView = findViewById(R.id.vv) as VideoView

        btn!!.setOnClickListener(View.OnClickListener { showPictureDialog() })

    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select video from gallery", "Record video from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> chooseVideoFromGallary()
                1 -> takeVideoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun chooseVideoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takeVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    @SuppressLint("Range")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d("result", "" + resultCode)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("what", "cancle")
            return
        }
        if (requestCode == GALLERY) {
            Log.d("what", "gale")
            if (data != null) {
                val contentURI = data!!.data
                val selectedVideoPath = getPath(contentURI)
                if (selectedVideoPath != null) {
                    Log.d("path", selectedVideoPath)
                    val file = File(selectedVideoPath)
                    var length: Long = file.length()
                    length = length / 1024
                    Log.d("length", length.toString())
                }
                val returnCursor: Cursor? = contentResolver.query(contentURI!!, null, null, null, null)
                Log.e("col",""+returnCursor!!.columnCount)
                returnCursor.moveToFirst()
                for(i in 0..returnCursor!!.columnCount-1){
                    var ind : Int = returnCursor!!.getColumnIndex(returnCursor!!.getColumnName(i))
                    Log.e("ind",""+ind)
                    Log.e("col",""+returnCursor!!.getColumnName(i)+" "+returnCursor.getString(ind))
                }
//                Log.e("col",""+returnCursor!!.getColumnName(0))
//                Log.e("col",""+returnCursor!!.getColumnName(1))
//                Log.e("col",""+returnCursor!!.getColumnName(2))
//                Log.e("col",""+returnCursor!!.getColumnName(3))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))
//                Log.e("col",""+returnCursor!!.getColumnName(4))


                videoView!!.setVideoURI(contentURI)
                videoView!!.requestFocus()
               // videoView!!.start()

            }

        } else if (requestCode == CAMERA) {
            Log.d("what", "camera")
            val contentURI = data!!.data
            val recordedVideoPath = getPath(contentURI)
            if (recordedVideoPath != null) {
                Log.d("frrr", recordedVideoPath)
            }

            videoView!!.setVideoURI(contentURI)
            videoView!!.requestFocus()
            videoView!!.start()
        }
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor!!
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor!!.moveToFirst()
            Log.d("video",cursor!!.getString(column_index))
            return cursor!!.getString(column_index)
        } else
            return null
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(applicationContext, "All permissions are granted by user!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
            .onSameThread()
            .check()
    }
}