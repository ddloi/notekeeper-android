package com.example.noteapp

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class createnote : AppCompatActivity() {

    private lateinit var titlecreatenote: EditText
    private lateinit var contentcreatenote: EditText
    private lateinit var savenote: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var toolbar: Toolbar
    private lateinit var documentReference: DocumentReference

    private var selectedColor: String = "#FFFFFF"
    private var selectedPriority: Int = 0
    private val colorList = listOf("#FFFFFF", "#FFD1DC", "#FFD700", "#B5EAD7", "#AEC6CF", "#E6CCFF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createnote)

        titlecreatenote = findViewById(R.id.titlecreatenote)
        contentcreatenote = findViewById(R.id.contentcreatenote)
        savenote = findViewById(R.id.save)
        toolbar = findViewById(R.id.toolbarcreatenote)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tạo ghi chú"

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()

        setupColorPicker()
        setupPrioritySpinner()

        savenote.setOnClickListener { saveNote() }
    }

    private fun saveNote() {
        val title = titlecreatenote.text.toString().trim()
        val content = contentcreatenote.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(applicationContext, "Vui lòng điền đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show()
            return
        }

        if (firebaseUser != null) {
            documentReference = firebaseFirestore
                .collection("notes")
                .document(firebaseUser!!.uid)
                .collection("mynotes")
                .document()

            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val note = hashMapOf(
                "title" to title,
                "content" to content,
                "date" to currentDate,
                "color" to selectedColor,
                "priority" to selectedPriority
            )

            documentReference.set(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tạo ghi chú thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Tạo ghi chú thất bại", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return titlecreatenote.text.toString().trim().isNotEmpty() ||
                contentcreatenote.text.toString().trim().isNotEmpty()
    }

    private fun showUnsavedChangesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bỏ ghi chú?")
            .setMessage("Bạn có nội dung chưa lưu. Bạn có muốn thoát không?")
            .setPositiveButton("Thoát") { _, _ -> finish() }
            .setNegativeButton("Tiếp tục soạn", null)
            .show()
    }

    // Nút back vật lý
    override fun onBackPressed() {
        if (hasUnsavedChanges()) showUnsavedChangesDialog()
        else super.onBackPressed()
    }

    // Nút ← trên ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (hasUnsavedChanges()) showUnsavedChangesDialog()
            else finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupColorPicker() {
        val colorViewIds = listOf(
            R.id.color_white, R.id.color_pink, R.id.color_yellow,
            R.id.color_green, R.id.color_blue, R.id.color_purple
        )
        colorViewIds.forEachIndexed { index, viewId ->
            findViewById<View>(viewId)?.setOnClickListener {
                selectedColor = colorList[index]
                contentcreatenote.setBackgroundColor(Color.parseColor(colorList[index]))
                titlecreatenote.setBackgroundColor(Color.parseColor(colorList[index]))
            }
        }
    }

    private fun setupPrioritySpinner() {
        val spinner = findViewById<Spinner>(R.id.prioritySpinner) ?: return
        val options = arrayOf("Thấp", "Trung bình", "Cao")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}