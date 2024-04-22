import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brainvault.app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteAdapter(private val onDeleteClickListener: (String) -> Unit ,private val onUpdateClickListener: (String, String) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes: List<Pair<String, String>> = ArrayList()

    fun setItems(notes: List<Pair<String, String>>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.itemContent)
        val deleteButton: FloatingActionButton = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.first
        holder.contentTextView.text = note.second
        // Long click listener for update functionality
        holder.itemView.setOnLongClickListener {
            val title = note.first
            val content = note.second
            onUpdateClickListener(title, content)
            true // Return true to consume the long click event
        }

        // Delete button click listener
        holder.deleteButton.setOnClickListener {
            val context = holder.itemView.context
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes") { _, _ ->
                    onDeleteClickListener(note.first)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}



