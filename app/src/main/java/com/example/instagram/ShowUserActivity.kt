package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.UserAdapter
import com.example.instagram.Model.User

class ShowUserActivity : AppCompatActivity() {

    var id : String = ""
    var title : String = ""
    var userAdapter: UserAdapter? = null
    var userList : List<User> ? = null
    var idList : List<String> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        val intent = intent
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

        val toolbar :Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { // for back button
            finish()
        }

        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter  = UserAdapter(this , userList as ArrayList<User> , false)
        recyclerView.adapter = userAdapter


        idList = ArrayList()

        when(title){

//            "likes" -> getLikes()
//            "following" -> getFollowing()
//            "followers" -> getFollowers()


            // we'll continue tomorrow , its 11:25 pm , okay , 14:46
        }




    }
}