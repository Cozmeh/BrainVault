package com.brainvault.app

import NoteAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class HomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        // firebase variables
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // recycler view init
        recyclerView = findViewById(R.id.recyclerViewNotes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter(::deleteNote)
        recyclerView.adapter = adapter

        // general variable
        val currentUser = auth.currentUser
        val floatingButton: FloatingActionButton = findViewById(R.id.add)


        // checking user
        if (currentUser != null) {
            Toast.makeText(this, "Welcome "+ currentUser.displayName.toString() + "!", Toast.LENGTH_SHORT).show()
        }
        // floating button
        floatingButton.setOnClickListener {
            openBottomNavBar()
        }
        // creates a default note for the new user's collection , if collection doesnt exists
        // if exists then displays the info of the user
        userInit(currentUser)
    }

    private fun userInit(currentUser: FirebaseUser?){
        val collectionRef = firestore.collection(currentUser?.displayName.toString())
        collectionRef.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Collection doesn't exist
                    Log.d("Firestore", "Collection 'test' doesn't exist")
                    //Toast.makeText(this, "NO exist", Toast.LENGTH_SHORT).show()
                    // if the collection does not exist , then it will create a collection
                    if (currentUser != null) {
                        firestore.collection(currentUser.displayName.toString()).document("Default").set(mapOf("content" to "Hi,User!"))
                            .addOnSuccessListener {
                                Log.d("Firestore", "Collection created successfully")
                                Toast.makeText(this, "A collection has been created successfully with the name " + currentUser.displayName.toString(), Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error creating collection", e)
                                Toast.makeText(this, "Failed to create collection with the name " + currentUser.displayName.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Collection exists
                    Log.d("Firestore", "Collection 'test' exists")
                    //Toast.makeText(this, "Yes exist", Toast.LENGTH_SHORT).show()
                    // gets the data to display in recycler view
                    getData(currentUser)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking collection 'test'", e)
                Toast.makeText(this, "An error has occured , please try again later!", Toast.LENGTH_SHORT).show()
            }
    }


    // gets data from the firebase in realtime
    private fun getData(currentUser : FirebaseUser?){
        if (currentUser != null) {
            val userCollectionRef = firestore.collection(currentUser.displayName.toString())

            // Create a snapshot listener to listen for real-time updates
            val listenerRegistration = userCollectionRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Listen failed", e)
                    return@addSnapshotListener
                }

                val notesList = mutableListOf<Pair<String, String>>()

                for (doc in snapshots!!) {
                    val noteTitle = doc.id
                    val noteContent = doc.getString("content") ?: ""
                    notesList.add(Pair(noteTitle, noteContent))
                }

//                val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNotes)
//                recyclerView.layoutManager = LinearLayoutManager(this)
//                recyclerView.adapter = NoteAdapter(notesList)
                  adapter.setItems(notesList)
            }
        } else {
            Log.e("Firestore", "Current user is null")
            Toast.makeText(this, "Current user is null", Toast.LENGTH_SHORT).show()
        }
    }

    // opening the bottom sheet
    private fun openBottomNavBar() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomNavView = layoutInflater.inflate(R.layout.bottom_nav_bar, null)
        bottomSheetDialog.setContentView(bottomNavView)

        val editTextTitle = bottomNavView.findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = bottomNavView.findViewById<EditText>(R.id.editTextContent)
        val buttonSave = bottomNavView.findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                saveNote(title, content)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog.show()
    }

    // saving data to firebase
    private fun saveNote(title: String, content: String) {
        val currentUser = auth.currentUser
        val data = hashMapOf("content" to content)
        currentUser?.let {
            firestore.collection(it.displayName.toString())
                .document(title)
                .set(data)
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot added with ID: $title")
                    Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding document", e)
                    Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // delete the particular note
    private fun deleteNote(title: String) {
        val currentUser = auth.currentUser
        currentUser?.let {
            firestore.collection(it.displayName.toString())
                .document(title)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Note deleted successfully")
                    Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting note", e)
                    Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
                }
        }
    }




}
