package com.example.noteapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class noteActivity : AppCompatActivity() {
    private lateinit var mcreatenotefab: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mrecyclerview: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        mcreatenotefab = findViewById(R.id.createnotefab)
        mrecyclerview  = findViewById(R.id.recyclerView)
        emptyState     = findViewById(R.id.emptyState)

        firebaseAuth      = FirebaseAuth.getInstance()
        firebaseUser      = FirebaseAuth.getInstance().currentUser!!
        firebaseFirestore = FirebaseFirestore.getInstance()

        supportActionBar?.title = "All Notes"

        mcreatenotefab.setOnClickListener {
            startActivity(Intent(this, createnote::class.java))
        }

        setupAdapter("")
    }

    private fun setupAdapter(searchText: String) {
        val baseQuery: Query = firebaseFirestore
            .collection("notes")
            .document(firebaseUser.uid)
            .collection("mynotes")
            .orderBy("priority", Query.Direction.DESCENDING)
            .orderBy("title", Query.Direction.ASCENDING)

        val allusernotes = FirestoreRecyclerOptions.Builder<firebasemodel>()
            .setQuery(baseQuery, firebasemodel::class.java)
            .build()

        noteAdapter = object : FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_layout, parent, false)
                return NoteViewHolder(view)
            }

            override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: firebasemodel) {
                val docId = snapshots.getSnapshot(position).id

                // Lọc search phía client
                if (searchText.isNotEmpty() &&
                    model.title?.contains(searchText, ignoreCase = true) == false &&
                    model.content?.contains(searchText, ignoreCase = true) == false) {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    return
                } else {
                    holder.itemView.visibility = View.VISIBLE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                holder.notetitle.text   = model.title
                holder.notecontent.text = model.content
                holder.notedate.text    = model.date ?: ""

                // Màu nền
                try {
                    holder.mnote.setBackgroundColor(
                        Color.parseColor(model.color?.takeIf { it.isNotEmpty() } ?: "#FFFFFF")
                    )
                } catch (e: Exception) {
                    holder.mnote.setBackgroundColor(Color.WHITE)
                }

                // Badge ưu tiên
                when (model.priority) {
                    2 -> { holder.prioritybadge.text = "● Cao";        holder.prioritybadge.setTextColor(Color.parseColor("#F44336")) }
                    1 -> { holder.prioritybadge.text = "● Trung bình"; holder.prioritybadge.setTextColor(Color.parseColor("#FF9800")) }
                    else -> { holder.prioritybadge.text = "● Thấp";    holder.prioritybadge.setTextColor(Color.parseColor("#4CAF50")) }
                }

                // Click card → mở edit
                holder.mnote.setOnClickListener {
                    startActivity(Intent(holder.itemView.context, editnote::class.java).apply {
                        putExtra("noteId", docId)
                        putExtra("noteTitle", model.title)
                        putExtra("noteContent", model.content)
                        putExtra("noteColor", model.color)
                        putExtra("notePriority", model.priority)
                    })
                }

                // Click ⋮ → popup menu
                holder.mmenu.setOnClickListener { view ->
                    val popup = PopupMenu(holder.itemView.context, view)
                    popup.menuInflater.inflate(R.menu.note_item_menu, popup.menu)
                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {

                            R.id.menu_edit -> {
                                startActivity(Intent(holder.itemView.context, editnote::class.java).apply {
                                    putExtra("noteId", docId)
                                    putExtra("noteTitle", model.title)
                                    putExtra("noteContent", model.content)
                                    putExtra("noteColor", model.color)
                                    putExtra("notePriority", model.priority)
                                })
                                true
                            }

                            R.id.menu_share -> {
                                val priorityText = when (model.priority) {
                                    2 -> "🔴 Cao"; 1 -> "🟠 Trung bình"; else -> "🟢 Thấp"
                                }
                                val shareText = buildString {
                                    append("📝 ${model.title ?: ""}\n")
                                    append("─────────────────\n")
                                    append("${model.content ?: ""}\n")
                                    append("─────────────────\n")
                                    append("⚡ Ưu tiên: $priorityText\n")
                                    if (!model.date.isNullOrEmpty()) append("📅 ${model.date}")
                                }
                                holder.itemView.context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, model.title ?: "Ghi chú")
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }, "Chia sẻ ghi chú qua..."
                                    )
                                )
                                true
                            }

                            // #1 Xác nhận trước khi xóa
                            R.id.menu_delete -> {
                                AlertDialog.Builder(holder.itemView.context)
                                    .setTitle("Xóa ghi chú?")
                                    .setMessage("\"${model.title}\" sẽ được chuyển vào thùng rác.")
                                    .setPositiveButton("Xóa") { _, _ ->
                                        moveToTrash(docId, model)
                                    }
                                    .setNegativeButton("Hủy", null)
                                    .show()
                                true
                            }

                            else -> false
                        }
                    }
                    popup.show()
                }
            }

            // #3 Cập nhật empty state mỗi khi data thay đổi
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0) {
                    emptyState.visibility  = View.VISIBLE
                    mrecyclerview.visibility = View.GONE
                } else {
                    emptyState.visibility  = View.GONE
                    mrecyclerview.visibility = View.VISIBLE
                }
            }
        }

        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mrecyclerview.layoutManager = staggeredGridLayoutManager
        mrecyclerview.adapter       = noteAdapter
        mrecyclerview.itemAnimator  = null

        noteAdapter.startListening()
    }

    // #2 Chuyển note vào thùng rác thay vì xóa thẳng
    private fun moveToTrash(docId: String, model: firebasemodel) {
        val deletedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val trashData = hashMapOf(
            "title"       to (model.title ?: ""),
            "content"     to (model.content ?: ""),
            "date"        to (model.date ?: ""),
            "color"       to (model.color ?: "#FFFFFF"),
            "priority"    to model.priority,
            "deletedDate" to deletedDate
        )

        // Thêm vào collection "trash"
        firebaseFirestore
            .collection("notes")
            .document(firebaseUser.uid)
            .collection("trash")
            .document(docId)
            .set(trashData)
            .addOnSuccessListener {
                // Xóa khỏi mynotes
                firebaseFirestore
                    .collection("notes")
                    .document(firebaseUser.uid)
                    .collection("mynotes")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Đã chuyển vào thùng rác", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Tìm kiếm ghi chú..."

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.stopListening()
                setupAdapter(newText?.trim() ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.infor -> {
                startActivity(Intent(this, information_noteactivity::class.java))
                true
            }
            R.id.trash -> {
                startActivity(Intent(this, TrashActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notetitle: TextView    = itemView.findViewById(R.id.notetitle)
        val notecontent: TextView  = itemView.findViewById(R.id.notecontent)
        val notedate: TextView     = itemView.findViewById(R.id.notedate)
        val prioritybadge: TextView = itemView.findViewById(R.id.prioritybadge)
        val mnote: LinearLayout    = itemView.findViewById(R.id.note)
        val mmenu: ImageView       = itemView.findViewById(R.id.menupopbutton)
    }
}