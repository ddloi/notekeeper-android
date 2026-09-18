package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {
    private lateinit var Email_signup : EditText;
    private lateinit var  Password_signup : EditText;
    private lateinit var Btn_signup : Button;
    private lateinit var mbacktologin : TextView;
    //Firebase
    private lateinit var firebaseAuth : FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        Email_signup = findViewById<EditText>(R.id.Email_signup)
        Password_signup = findViewById<EditText>(R.id.Password_signup)
        Btn_signup = findViewById<Button>(R.id.Signup_button)
        mbacktologin = findViewById<TextView>(R.id.Back_to_login)
        firebaseAuth = FirebaseAuth.getInstance();

        //quay lại login
        mbacktologin.setOnClickListener {
            var intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
        }
        // đăng nhập
        Btn_signup.setOnClickListener {
            val email = Email_signup.text.toString().trim()
            val password = Password_signup.text.toString().trim()

            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(applicationContext,"Please fill in the complete information",Toast.LENGTH_SHORT).show();
            }
            else if(password.length < 7)
            {
                Toast.makeText(applicationContext,"Enter a password with more than 7 characters",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Đăng ký người dùng vào firebase
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show()
                            sendEmailVerification()
                        }
                        else
                        {
                            Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

        }

    //gửi email xác minh
    private fun sendEmailVerification()
    {
        val user = firebaseAuth.currentUser
        //Check user để gửi email
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "A verification email has been sent, Please Login again", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else
                {
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    }
