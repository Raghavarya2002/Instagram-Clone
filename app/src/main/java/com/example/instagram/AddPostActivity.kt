package com.example.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.AppConstants.DESCRIPTION
import com.example.instagram.AppConstants.POSTS
import com.example.instagram.AppConstants.POST_ID
import com.example.instagram.AppConstants.POST_IMAGE
import com.example.instagram.AppConstants.POST_PICTURES
import com.example.instagram.AppConstants.PUBLISHER
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.description_post
import kotlinx.android.synthetic.main.activity_add_post.image_post
import kotlinx.android.synthetic.main.activity_add_post.save_new_post_btn
import java.util.Locale

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        storagePostPicRef = FirebaseStorage.getInstance().reference.child(POST_PICTURES)
        save_new_post_btn.setOnClickListener { uploadImage() }

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        when {
            imageUri == null -> Toast.makeText(
                this,
                getString(R.string.please_select_an_image_first),
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(
                this,
                getString(R.string.please_write_description),
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle(getString(R.string.new_post))
                progressDialog.setMessage(getString(R.string.please_wait_we_re_adding_your_post))
                progressDialog.show()

                val fileRef =
                    storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(com.google.android.gms.tasks.Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()

                        }
                    }
                    return@Continuation fileRef.downloadUrl


                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child(POSTS)
                        val postId = ref.push().key
                        val postMap = HashMap<String, Any>()

                        postMap[POST_ID] = postId!!
                        postMap[DESCRIPTION] =
                            description_post.text.toString().toLowerCase(Locale.getDefault())
                        postMap[PUBLISHER] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap[POST_IMAGE] = myUrl
                        ref.child(postId).updateChildren(postMap)
                        Toast.makeText(
                            this,
                            getString(R.string.post_uploaded_successfully),
                            Toast.LENGTH_LONG
                        )
                            .show()

                        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }

                })
            }

        }


    }

}