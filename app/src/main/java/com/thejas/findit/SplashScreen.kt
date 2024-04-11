    package com.thejas.findit

    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.ValueEventListener
    import android.util.Log

    class SplashScreen : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth
        private lateinit var databaseReference: DatabaseReference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash_screen)

            // Initialize Firebase Auth and Database reference
            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance().reference

            // Handler to delay the start of the next activity
            Handler(Looper.getMainLooper()).postDelayed({
                val currentUser = auth.currentUser

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val userRef = databaseReference.child("users").child(uid)

                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userType = snapshot.child("type").value as? String
                            if (userType != null) {
                                try {
                                    if (userType == "student") {
                                        val intent = Intent(this@SplashScreen, StudentActivity::class.java)
                                        startActivity(intent)
                                    } else if (userType == "owner") {
                                        val intent = Intent(this@SplashScreen, OwnerActivity::class.java)
                                        startActivity(intent)
                                    }
                                } catch (e: Exception) {
                                    Log.e("SplashScreen", "Error navigating to activity: ${e.message}")
                                } finally {
                                    finish()
                                }
                            } else {
                                navigateToWelcomeActivity()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("SplashScreen", "Error fetching user data: ${error.message}")
                            navigateToWelcomeActivity()
                        }
                    })
                } else {
                    navigateToWelcomeActivity()
                }
            }, 2000)




        }

        private fun navigateToWelcomeActivity() {
            val intent = Intent(this@SplashScreen, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
