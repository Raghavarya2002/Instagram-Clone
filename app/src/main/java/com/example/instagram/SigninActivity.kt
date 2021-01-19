package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Signup_link_btn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        login_btn.setOnClickListener {

            loginUser()


        }

    }

    private fun loginUser() {
        val  Email = email_login.text.toString().trim()
        val password = pass_login.text.toString().trim()

            if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){

            email_login.error = "Valid Email Required"
            email_login.requestFocus()

            }

            if (password.length < 6 ){
            pass_login.error = "At least 6 characters are required"
            pass_login.requestFocus()
            }

        when{
        TextUtils.isEmpty(Email) -> Toast.makeText(this,"Email is required .",Toast.LENGTH_LONG).show()
        TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is required .",Toast.LENGTH_LONG).show()

            else ->
            {
                val progressDialog = ProgressDialog(this@SigninActivity)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait,this may take a while..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(Email,password).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        progressDialog.dismiss()
                        val intent = Intent(this@SigninActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }

                    else{
                        val message = task.exception!!
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()



                    }


                }

            }

            }



    }

    override fun onStart(){
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null){

            val intent = Intent(this@SigninActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }




    }

}