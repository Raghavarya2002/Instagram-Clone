package com.example.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.Model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class accountSettingActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@accountSettingActivity, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

        change_image_text_btn.setOnClickListener {

            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@accountSettingActivity)

        }

        save_info_profile_btn.setOnClickListener {

            if (checker == "clicked") {

                uploadImageAndUploadInfo()


            } else {
                updateUserInfoOnly()

            }

        }

        userinfo()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {
        when {
            full_name_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Full name is required", Toast.LENGTH_LONG).show()
            }
            username_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Bio is required", Toast.LENGTH_LONG).show()
            }
            else -> {
                val userRef = FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()

                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                userRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(
                    this,
                    "Account setting has been updated successfully.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@accountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }


    }


    private fun userinfo() {

        val userRef =
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //  if (context!= null) return
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.getUsername())
                    full_name_profile_frag.setText(user!!.getfullname())
                    bio_profile_frag.setText(user!!.getbio())
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun uploadImageAndUploadInfo() {


        when {
            imageUri == null -> {
                Toast.makeText(this, "Please Select an image first", Toast.LENGTH_LONG).show()
            }

            full_name_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Full name is required", Toast.LENGTH_LONG).show()
            }
            username_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.toString() == "" -> {
                Toast.makeText(this, "Bio is required", Toast.LENGTH_LONG).show()
            }

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Setting")
                progressDialog.setMessage("Please wait , we're updating your profile...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
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
                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()

                        userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = myUrl
                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(
                            this,
                            "Account setting has been updated successfully.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@accountSettingActivity, MainActivity::class.java)
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

