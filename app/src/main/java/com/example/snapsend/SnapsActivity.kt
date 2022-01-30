package com.example.snapsend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SnapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var receivedSnapsEmails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView = findViewById(R.id.snapsListView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, receivedSnapsEmails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid.toString()).child("snaps").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                receivedSnapsEmails.add(snapshot.child("from").value as String)
                snaps.add(snapshot)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var index = 0
                for (snap: DataSnapshot in snaps) {
                    if (snap.key == snapshot.key) {
                        snaps.removeAt(index)
                        receivedSnapsEmails.removeAt(index)

                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps.get(i)

            var intent = Intent(this, ViewSnapActivity::class.java)

            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("snapKey", snapshot.key)

            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.createSnap) {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.logout) {
            auth.signOut()
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
    }
}