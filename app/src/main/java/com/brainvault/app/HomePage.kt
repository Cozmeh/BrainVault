package com.brainvault.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(this, currentUser.displayName.toString(), Toast.LENGTH_SHORT).show()
        }
        currentUser?.let { user ->
            val userDocRef = firestore.collection("users").document(user.displayName.toString())

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Document already exists, do something
                        Log.d("HomePage", "User document exists")
                        Toast.makeText(this, "User document exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // Document doesn't exist
                        Log.d("HomePage", "User document doesn't exist")
                        Toast.makeText(this, "User document doesn't exists", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomePage", "Error checking user document", e)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
