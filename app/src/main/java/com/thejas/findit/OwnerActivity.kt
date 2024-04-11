package com.thejas.findit

import PropertyAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OwnerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var addpr : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var propertyList: MutableList<Property>
    private lateinit var adapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)
        auth = FirebaseAuth.getInstance()
        addpr = findViewById(R.id.addpr)
        addpr.setOnClickListener {
            val intent = Intent(this, AddProperty::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.owner_properties_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        propertyList = mutableListOf()
        adapter = PropertyAdapter(propertyList)
        recyclerView.adapter = adapter

        // Fetch and display properties
        fetchProperties()


        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun fetchProperties() {
        val uid = auth.currentUser?.uid
        Log.e("OwnerActivity", "Fetching properties for owner with ID: $uid")
        if (uid != null) {
            val database = FirebaseDatabase.getInstance()
            val propertiesRef = database.getReference("properties").orderByChild("ownerId").equalTo(uid)

            propertiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("OwnerActivity", "Fetched properties count: ${dataSnapshot.childrenCount}")
                    propertyList.clear()
                    for (propertySnapshot in dataSnapshot.children) {
                        val property = propertySnapshot.getValue(Property::class.java)
                        property?.let {
                            propertyList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@OwnerActivity,
                        "Error fetching properties: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Log.d("OwnerActivity", "User is not authenticated")
        }
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
            R.id.refresh -> {
                fetchProperties()
                Toast.makeText(this, "Items Updated", Toast.LENGTH_SHORT).show()
                return true
            }



            else -> return super.onOptionsItemSelected(item)
        }
    }
}
