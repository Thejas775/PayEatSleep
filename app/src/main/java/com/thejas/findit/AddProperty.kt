package com.thejas.findit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddProperty : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var addimages : Button
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)
        auth = FirebaseAuth.getInstance()
        addimages = findViewById(R.id.addimages)
        databaseReference = FirebaseDatabase.getInstance().reference.child("properties")
        val addButton: Button = findViewById(R.id.add)
        val nameEditText: TextInputEditText = findViewById(R.id.name)
        val addressEditText: TextInputEditText = findViewById(R.id.address)
        val contactPersonEditText: TextInputEditText = findViewById(R.id.contact_person)
        val contactNumberEditText: TextInputEditText = findViewById(R.id.contact_number)
        val priceEditText: TextInputEditText = findViewById(R.id.price)
        val radioGroup: RadioGroup = findViewById(R.id.radio_group)
        addButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val contactPerson = contactPersonEditText.text.toString().trim()
            val contactNumber = contactNumberEditText.text.toString().trim()
            val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton: RadioButton = findViewById(selectedId)
            val type = radioButton.text.toString()
            if (name.isEmpty() || address.isEmpty() || contactPerson.isEmpty() || contactNumber.isEmpty() || price == 0.0 || type.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ownerId = auth.currentUser?.uid ?: return@setOnClickListener

            // Create a property object
            val property = Property(name, address, contactPerson, contactNumber, price, type, ownerId)

            // Push the property to the database
            val propertyKey = databaseReference.push().key ?: return@setOnClickListener
            databaseReference.child(propertyKey).setValue(property)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Finish the activity
                    } else {
                        Toast.makeText(this, "Failed to add property: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }



    addimages.setOnClickListener {
            Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show()
        }

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    // Inflate the menu for the activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_student_activity, menu)
        return true
    }

    // Handle menu item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                auth.signOut()

                // Redirect the user to the login screen
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                // Clear the back stack to prevent the user from going back to the previous screen
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


}
