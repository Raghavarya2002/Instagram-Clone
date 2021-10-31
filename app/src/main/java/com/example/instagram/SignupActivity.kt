package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        Signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }

        signup_btn.setOnClickListener {


            createAccount()


        }

    }

    private fun createAccount() {
        val fullname = fullname_signup.text.toString().toLowerCase()
        val username = username_signup.text.toString().toLowerCase()
        val Email = email_signup.text.toString().trim()
        val password = pass_signup.text.toString().trim()

        if (!EMAIL_ADDRESS.matcher(Email).matches()) {

            email_signup.error = "Valid Email Required"
            email_signup.requestFocus()

        }

        if (password.length < 6) {
            pass_signup.error = "At least 6 characters are required"
            pass_signup.requestFocus()
        }


        when {


            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "Full Name is required .",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "Username is required .",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(Email) -> Toast.makeText(
                this,
                "Email is required .",
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Password is required .",
                Toast.LENGTH_LONG
            ).show()


            else -> {

                val progressDialog = ProgressDialog(this@SignupActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait,this may take a while..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(Email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            saveUserInfo(fullname, username, Email, password, progressDialog)

                        } else {
                            val message = task.exception!!
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }


                    }

            }

        }

    }

    private fun saveUserInfo(
        fullname: String,
        username: String,
        email: String,
        password: String,
        progressDialog: ProgressDialog
    ) {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = "Go Float Yourself"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instagram-18dd2.appspot.com/o/profile.png?alt=media&token=f6b9f72a-f71d-4ab7-a95d-562d4fad5d83"

        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Account has been created successfully.",
                        Toast.LENGTH_LONG
                    ).show()


                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId)
                        .setValue(true)

                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                } else {

                    val message = task.exception!!
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }

    }
}