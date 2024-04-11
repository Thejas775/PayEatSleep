package com.thejas.findit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var imageleft : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        imageleft = findViewById(R.id.imageLeft)
        imageleft.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Find views by ID
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val nameEditText = findViewById<EditText>(R.id.username)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // Set up the sign-up button click listener
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()

            // Check which radio button is selected
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            val userType = selectedRadioButton.text.toString().toLowerCase()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && userType.isNotEmpty()) {
                // Perform sign-up with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Get the user's UID
                            val uid = auth.currentUser?.uid

                            // Store user data in Firebase Realtime Database
                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "type" to userType
                            )

                            uid?.let {
                                database.reference.child("users").child(it).setValue(userMap)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            // Navigate to different activities based on user type
                                            if (userType == "students") {
                                                val intent = Intent(this, StudentActivity::class.java)
                                                startActivity(intent)
                                            } else if (userType == "owners") {
                                                val intent = Intent(this, OwnerActivity::class.java)
                                                startActivity(intent)
                                            }

                                            // Finish the current activity
                                            finish()
                                        } else {
                                            // Handle database error
                                            Toast.makeText(this, "Failed to store user data in the database", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        } else {
                            // Handle sign-up error
                            Toast.makeText(this, "Failed to sign up: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                // Inform the user to fill out all fields
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}
