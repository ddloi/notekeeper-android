package com.example.noteapp

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class editnote : AppCompatActivity() {

    private lateinit var titleEdit: EditText
    private lateinit var contentEdit: EditText
    private lateinit var saveEdit: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var noteId: String

    private var selectedColor: String = "#FFFFFF"
    private var selectedPriority: Int = 0

    // Lưu giá trị gốc để so sánh khi back
    private var originalTitle: String = ""
    private var originalContent: String = ""
    private var originalColor: String = "#FFFFFF"
    private var originalPriority: Int = 0

    private val colorList = listOf("#FFFFFF", "#FFD1DC", "#FFD700", "#B5EAD7", "#AEC6CF", "#E6CCFF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editnote)

        titleEdit = findViewById(R.id.titleedit)
        contentEdit = findViewById(R.id.contentedit)
        saveEdit = findViewById(R.id.saveedit)
        toolbar = findViewById(R.id.toolbareditnote)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chỉnh sửa ghi chú"

        firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        noteId = intent.getStringExtra("noteId") ?: ""

        // Lưu giá trị gốc để detect thay đổi
        originalTitle   = intent.getStringExtra("noteTitle") ?: ""
        originalContent = intent.getStringExtra("noteContent") ?: ""
        originalColor   = intent.getStringExtra("noteColor") ?: "#FFFFFF"
        originalPriority = intent.getIntExtra("notePriority", 0)

        titleEdit.setText(originalTitle)
        contentEdit.setText(originalContent)
        selectedColor    = originalColor
        selectedPriority = originalPriority

        try {
            contentEdit.setBackgroundColor(Color.parseColor(selectedColor))
            titleEdit.setBackgroundColor(Color.parseColor(selectedColor))
        } catch (e: Exception) { }

        setupColorPicker()
        setupPrioritySpinner()

        saveEdit.setOnClickListener {
            val newTitle   = titleEdit.text.toString().trim()
            val newContent = contentEdit.text.toString().trim()

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            firebaseFirestore
                .collection("notes")
                .document(firebaseUser.uid)
                .collection("mynotes")
                .document(noteId)
                .update(
                    "title", newTitle,
                    "content", newContent,
                    "date", updatedDate,
                    "color", selectedColor,
                    "priority", selectedPriority
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã cập nhật ghi chú", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return titleEdit.text.toString().trim() != originalTitle ||
                contentEdit.text.toString().trim() != originalContent ||
                selectedColor != originalColor ||
                selectedPriority != originalPriority
    }

    private fun showUnsavedChangesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bỏ thay đổi?")
            .setMessage("Bạn có thay đổi chưa lưu. Bạn có muốn thoát không?")
            .setPositiveButton("Thoát") { _, _ -> finish() }
            .setNegativeButton("Tiếp tục sửa", null)
            .show()
    }

    override fun onBackPressed() {
        if (hasUnsavedChanges()) showUnsavedChangesDialog()
        else super.onBackPressed()
    }

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
            R.id.edit_color_white, R.id.edit_color_pink, R.id.edit_color_yellow,
            R.id.edit_color_green, R.id.edit_color_blue, R.id.edit_color_purple
        )
        colorViewIds.forEachIndexed { index, viewId ->
            findViewById<View>(viewId)?.setOnClickListener {
                selectedColor = colorList[index]
                contentEdit.setBackgroundColor(Color.parseColor(colorList[index]))
                titleEdit.setBackgroundColor(Color.parseColor(colorList[index]))
            }
        }
    }

    private fun setupPrioritySpinner() {
        val spinner = findViewById<Spinner>(R.id.prioritySpinnerEdit) ?: return
        val options = arrayOf("Thấp", "Trung bình", "Cao")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(selectedPriority)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}