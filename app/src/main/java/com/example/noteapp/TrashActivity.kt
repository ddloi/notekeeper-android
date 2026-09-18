package com.example.noteapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrashActivity : AppCompatActivity() {

    private lateinit var mrecyclerview: RecyclerView
    private lateinit var emptyTrash: LinearLayout
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var trashAdapter: FirestoreRecyclerAdapter<firebasemodel, TrashViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Thùng rác"

        mrecyclerview = findViewById(R.id.trashRecyclerView)
        emptyTrash    = findViewById(R.id.emptyTrash)

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseFirestore = FirebaseFirestore.getInstance()

        val query: Query = firebaseFirestore
            .collection("notes")
            .document(firebaseUser.uid)
            .collection("trash")
            .orderBy("deletedDate", Query.Direction.DESCENDING)

        val trashOptions = FirestoreRecyclerOptions.Builder<firebasemodel>()
            .setQuery(query, firebasemodel::class.java)
            .build()

        trashAdapter = object : FirestoreRecyclerAdapter<firebasemodel, TrashViewHolder>(trashOptions) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_layout, parent, false)
                return TrashViewHolder(view)
            }

            override fun onBindViewHolder(holder: TrashViewHolder, position: Int, model: firebasemodel) {
                val docId = snapshots.getSnapshot(position).id

                holder.notetitle.text   = model.title
                holder.notecontent.text = model.content
                holder.notedate.text    = "Đã xóa: ${snapshots.getSnapshot(position).getString("deletedDate") ?: ""}"

                try {
                    holder.mnote.setBackgroundColor(
                        Color.parseColor(model.color?.takeIf { it.isNotEmpty() } ?: "#FFFFFF")
                    )
                } catch (e: Exception) {
                    holder.mnote.setBackgroundColor(Color.WHITE)
                }

                when (model.priority) {
                    2 -> { holder.prioritybadge.text = "● Cao";        holder.prioritybadge.setTextColor(Color.parseColor("#F44336")) }
                    1 -> { holder.prioritybadge.text = "● Trung bình"; holder.prioritybadge.setTextColor(Color.parseColor("#FF9800")) }
                    else -> { holder.prioritybadge.text = "● Thấp";    holder.prioritybadge.setTextColor(Color.parseColor("#4CAF50")) }
                }

                // Click icon menu → Khôi phục hoặc Xóa vĩnh viễn
                holder.mmenu.setOnClickListener {
                    AlertDialog.Builder(holder.itemView.context)
                        .setTitle("\"${model.title}\"")
                        .setItems(arrayOf("♻️ Khôi phục", "🗑️ Xóa vĩnh viễn")) { _, which ->
                            when (which) {
                                0 -> restoreNote(docId, model)
                                1 -> confirmPermanentDelete(docId, model.title ?: "")
                            }
                        }
                        .show()
                }

                // Click card cũng mở dialog
                holder.mnote.setOnClickListener {
                    holder.mmenu.performClick()
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0) {
                    emptyTrash.visibility    = View.VISIBLE
                    mrecyclerview.visibility = View.GONE
                } else {
                    emptyTrash.visibility    = View.GONE
                    mrecyclerview.visibility = View.VISIBLE
                }
            }
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy   = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mrecyclerview.layoutManager = layoutManager
        mrecyclerview.adapter       = trashAdapter
        mrecyclerview.itemAnimator  = null
    }

    private fun restoreNote(docId: String, model: firebasemodel) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val restoreDate  = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val noteData = hashMapOf(
            "title"    to (model.title ?: ""),
            "content"  to (model.content ?: ""),
            "date"     to restoreDate,
            "color"    to (model.color ?: "#FFFFFF"),
            "priority" to model.priority
        )

        firebaseFirestore
            .collection("notes")
            .document(firebaseUser.uid)
            .collection("mynotes")
            .document(docId)
            .set(noteData)
            .addOnSuccessListener {
                // Xóa khỏi trash
                firebaseFirestore
                    .collection("notes")
                    .document(firebaseUser.uid)
                    .collection("trash")
                    .document(docId)
                    .delete()
                Toast.makeText(this, "Đã khôi phục ghi chú ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Khôi phục thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmPermanentDelete(docId: String, title: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        AlertDialog.Builder(this)
            .setTitle("Xóa vĩnh viễn?")
            .setMessage("\"$title\" sẽ bị xóa vĩnh viễn và không thể khôi phục.")
            .setPositiveButton("Xóa vĩnh viễn") { _, _ ->
                firebaseFirestore
                    .collection("notes")
                    .document(firebaseUser.uid)
                    .collection("trash")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Đã xóa vĩnh viễn", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        trashAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        trashAdapter.stopListening()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class TrashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notetitle: TextView     = itemView.findViewById(R.id.notetitle)
        val notecontent: TextView   = itemView.findViewById(R.id.notecontent)
        val notedate: TextView      = itemView.findViewById(R.id.notedate)
        val prioritybadge: TextView = itemView.findViewById(R.id.prioritybadge)
        val mnote: LinearLayout     = itemView.findViewById(R.id.note)
        val mmenu: ImageView        = itemView.findViewById(R.id.menupopbutton)
    }
}