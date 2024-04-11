package com.thejas.findit

import PropertyAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var propertyList: MutableList<Property>
    private lateinit var adapter: PropertyAdapter
    private lateinit var pg : Button
    private lateinit var mess : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        pg = findViewById(R.id.pg)
        mess = findViewById(R.id.mess)


        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.student_properties_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        propertyList = mutableListOf()
        adapter = PropertyAdapter(propertyList)
        recyclerView.adapter = adapter

        // Fetch properties and display them
        fetchProperties()

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set click listeners for the buttons
        pg.setOnClickListener {
            pg.background = ContextCompat.getDrawable(this, R.drawable.rounded_border)
            mess.background = ContextCompat.getDrawable(this, R.drawable.rounded_border2)
            filterProperties("PG")
        }

        mess.setOnClickListener {
            mess.background = ContextCompat.getDrawable(this, R.drawable.rounded_border)
            pg.background = ContextCompat.getDrawable(this, R.drawable.rounded_border2)
            filterProperties("Mess")
        }
    }

    private fun fetchProperties() {
        val database = FirebaseDatabase.getInstance()
        val propertiesRef = database.getReference("properties") // Adjust the reference as needed

        // Fetch properties
        propertiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear existing list and populate with new data
                propertyList.clear()
                for (propertySnapshot in dataSnapshot.children) {
                    val property = propertySnapshot.getValue(Property::class.java)
                    property?.let {
                        propertyList.add(it)
                    }
                }

                // Notify adapter of data change
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@StudentActivity, "Failed to fetch properties: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterProperties(type: String) {
        val filteredList = propertyList.filter { it.type == type }
        adapter.updateList(filteredList)
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
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}