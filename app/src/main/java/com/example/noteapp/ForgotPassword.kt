package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth



class ForgotPassword : AppCompatActivity() {
    private lateinit var mforgotpassword : EditText;
    private lateinit var mrecoverpassword : Button;
    private lateinit var mbacktologin : TextView;

    //Firebase
    private lateinit var firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        mforgotpassword = findViewById<EditText>(R.id.forgot_Pass)
        mrecoverpassword = findViewById<Button>(R.id.Recover_button)
        mbacktologin = findViewById<TextView>(R.id.Back_to_login)
        firebaseAuth = FirebaseAuth.getInstance();

        //Quay lại Login
        mbacktologin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //Gửi về mail để lấy lại mk
        mrecoverpassword.setOnClickListener {
            val mail = mforgotpassword.text.toString().trim();
            if(mail.isEmpty())
            {
                Toast.makeText(applicationContext,"Enter your mail first",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Send về mail
                firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener{ task ->
                    if(task.isSuccessful())
                    {
                        Toast.makeText(applicationContext,"Mail sent , you can recover your password using mail",Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"Email is wrong or Account is not exist , can't send email",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}