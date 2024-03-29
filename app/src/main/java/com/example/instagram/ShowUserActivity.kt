package com.example.instagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.UserAdapter
import com.example.instagram.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUserActivity : AppCompatActivity() {

    var id: String = ""
    var title: String = ""
    var userAdapter: UserAdapter? = null
    var userList: List<User>? = null
    var idList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        val intent = intent
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { // for back button
            finish()
        }

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(this, userList as ArrayList<User>, false)
        recyclerView.adapter = userAdapter


        idList = ArrayList() //array list dynamic size ka array hota hai

        when (title) {

            "likes" -> getLikes()
            "following" -> getFollowing()
            "followers" -> getFollowers()
            "views" -> getViews()


        }


    }

    private fun getViews() {


        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(id!!)
            .child(intent.getStringExtra("storyid").toString())
            .child("views")



        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    (idList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children) {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun getFollowers() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Followers")



        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    (idList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children) {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun getFollowing() {

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Following")



        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    (idList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children) {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun getLikes() {

        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id!!)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (idList as ArrayList<String>).clear()

                    for (snapshot in snapshot.children) {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }

            }

        })

    }

    private fun showUser() {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)

                    for (id in idList!!) {
                        if (user!!.getuid() == id) {
                            (userList as ArrayList<User>).add(user!!)

                        }
                    }


                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}