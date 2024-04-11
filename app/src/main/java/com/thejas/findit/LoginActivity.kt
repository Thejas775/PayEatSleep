package com.thejas.findit

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var studentRadioButton: RadioButton
    private lateinit var imageleft : ImageView
    private lateinit var ownerRadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        imageleft = findViewById(R.id.imageLeft)
        imageleft.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Get references to UI components
        emailEditText = findViewById(R.id.logInMailID)
        passwordEditText = findViewById(R.id.userpassword)
        loginButton = findViewById(R.id.logInBtn)
        studentRadioButton = findViewById(R.id.radio_students)
        ownerRadioButton = findViewById(R.id.radio_owners)

        // Handle login button click event
        loginButton.setOnClickListener {
            // Get user input for email, password, and role
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val selectedRole = when {
                studentRadioButton.isChecked -> "student"
                ownerRadioButton.isChecked -> "owner"
                else -> null
            }

            // Validate inputs
            if (email.isEmpty() || password.isEmpty() || selectedRole == null) {
                Toast.makeText(this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticate the user with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Authentication succeeded, get the current user
                        val user = auth.currentUser
                        user?.let {
                            val uid = it.uid

                            // Retrieve the user type from the database
                            val userRef = databaseReference.child("users").child(uid)
                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    // Check user type
                                    val userType = snapshot.child("type").value as? String
                                    if (userType == selectedRole) {
                                        // Navigate to the correct activity based on user role
                                        if (selectedRole == "student") {
                                            val intent = Intent(this@LoginActivity, StudentActivity::class.java)
                                            startActivity(intent)
                                        } else if (selectedRole == "owner") {
                                            val intent = Intent(this@LoginActivity, OwnerActivity::class.java)
                                            startActivity(intent)
                                        }
                                        finish() // Finish LoginActivity
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "User role mismatch. Please verify your role selection.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Error fetching user data: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    } else {
                        // Authentication failed
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}