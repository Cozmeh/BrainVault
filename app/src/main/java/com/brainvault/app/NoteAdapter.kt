import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brainvault.app.OnNoteItemClickListener
import com.brainvault.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteAdapter(private val notes: List<Pair<String, String>>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.itemContent)
        //val deleteButton: Button = itemView.findViewById(R.id.delete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.first
        holder.contentTextView.text = note.second
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
private fun deleteNoteFromFirebase(noteId: String) {
    // Delete the document with the given noteId from Firebase Firestore
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        val userCollectionRef = firestore.collection(it.displayName.toString())
        userCollectionRef.document(noteId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Note deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting note", e)
            }
    }
}

