package com.example.snapsend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.Exception

class ViewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    var mStorageReference: StorageReference? = null
    var imageName: String? = null
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")

        imageName = intent.getStringExtra("imageName")

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images/$imageName")

        try {
            val localTempFile: File = File.createTempFile("snapImg", "jpg")
            mStorageReference!!.getFile(localTempFile).addOnSuccessListener( OnSuccessListener {
                val myBitmap: Bitmap = BitmapFactory.decodeFile(localTempFile.absolutePath)
                snapImageView?.setImageBitmap(myBitmap)
            } ).addOnFailureListener(OnFailureListener {
                Toast.makeText(this, "Snap Unavailable.", Toast.LENGTH_SHORT).show()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid.toString()).child("snaps").child(intent.getStringExtra("snapKey") as String).removeValue()
        FirebaseStorage.getInstance().getReference().child("images").child("$imageName").delete()

    }
}