import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brainvault.app.OnNoteItemClickListener
import com.brainvault.app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteAdapter(private val onDeleteClickListener: (String) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

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
        // Delete button click listener
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(note.first)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}



