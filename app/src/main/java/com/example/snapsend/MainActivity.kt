package com.example.snapsend

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    lateinit var goButton: Button
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        goButton = findViewById(R.id.goButton)

        if (auth.currentUser != null) {
            logIn()
        }


    }

    fun goClicked(view: View) {
        // Check for login
        // else signup
        if (passwordEditText?.length()!! >= 6) {
            auth.signInWithEmailAndPassword(
                emailEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        logIn()
                    } else {
                        auth.createUserWithEmailAndPassword(
                            emailEditText?.text.toString(),
                            passwordEditText?.text.toString()
                        ).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(task.result!!.user!!.uid).child("email")
                                    .setValue(emailEditText?.text.toString())
                                logIn()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    baseContext,
                                    "Login Failed. Try Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                emailEditText?.text?.clear()
                                passwordEditText?.text?.clear()
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(
                this,
                "password length can not be less than 6 characters",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun logIn() {
        // Move to next Activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}