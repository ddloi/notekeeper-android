package com.example.noteapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class information_noteactivity : AppCompatActivity() {
    private lateinit var tvEmail: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvChangePass: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_noteactivity)

        // Hiện nút back trên ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Thông tin tài khoản"

        tvEmail = findViewById(R.id.tvEmail)
        tvDate = findViewById(R.id.tvDate)
        tvChangePass = findViewById(R.id.tvChangePass)
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser

        tvEmail.text = "Email: ${user?.email ?: "Không có"}"

        val creationTime = user?.metadata?.creationTimestamp
        if (creationTime != null) {
            val date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(creationTime))
            tvDate.text = "Ngày tạo: $date"
        } else {
            tvDate.text = "Ngày tạo: Không rõ"
        }

        tvChangePass.setOnClickListener {
            val email = user?.email
            if (email != null) {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        tvChangePass.text = "Email đổi mật khẩu đã được gửi!"
                    }
                    .addOnFailureListener {
                        tvChangePass.text = "Gửi thất bại, thử lại sau."
                    }
            }
        }
    }

    // Xử lý nút back trên ActionBar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}