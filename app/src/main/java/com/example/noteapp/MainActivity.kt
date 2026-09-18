package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var Email_login : EditText
    private lateinit var Password_login : EditText
    private lateinit var btn_login : Button
    private lateinit var ForgotPassword_login : TextView
    private lateinit var btn_signup_login : Button

    //firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        Email_login = findViewById<EditText>(R.id.Email_login)
        Password_login = findViewById<EditText>(R.id.Password_login)
        btn_login = findViewById<Button>(R.id.Login_button)
        ForgotPassword_login = findViewById<TextView>(R.id.forgot_Pass)
        btn_signup_login = findViewById<Button>(R.id.Signup_login_button)
        firebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser = firebaseAuth.currentUser

        //vertification rồi thì chuyển sang note Activity luôn
        if (firebaseUser != null) {
            finish()
            startActivity(Intent(this, noteActivity::class.java))
        }

        ForgotPassword_login.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
        // SignUp
        btn_signup_login.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        // Login
        btn_login.setOnClickListener {
            val email = Email_login.text.toString().trim()
            val password = Password_login.text.toString().trim()

            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(applicationContext,"Please fill your information",Toast.LENGTH_SHORT).show()
            }
            else
            {
                //login to user
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        checkemailverfication();
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"Account is not exist",Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }
    //Check xác thực
    private fun checkemailverfication() {
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser?.isEmailVerified == true)
        {
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this,noteActivity::class.java))
        }
        else
        {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
            firebaseAuth.signOut()
        }
    }
}