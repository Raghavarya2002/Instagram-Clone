package com.example.instagram

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.CommentAdapter
import com.example.instagram.Model.Commnet
import com.example.instagram.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*


class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Commnet>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)


        val intent = intent
        postId = intent.getStringExtra("postId")!!
        publisherId = intent.getStringExtra("publisherId")!!
        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter


        userinfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment!!.text.toString() == "") {
                Toast.makeText(
                    this@CommentsActivity,
                    "Please write comments first",
                    Toast.LENGTH_LONG
                )
            } else {

                addComment()
            }
        })


    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        addNotification()
        add_comment!!.text.clear()
    }


    private fun userinfo() {

        val userRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //  if (context!= null) return
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPostImage() {

        val postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!)
            .child("postimage")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //  if (context!= null) return
                if (snapshot.exists()) {

                    val image = snapshot.value.toString()
                    Picasso.get().load(image).placeholder(R.drawable.profile)
                        .into(post_image_comment)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun readComments() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    commentList!!.clear()
                    for (snapshot in snapshot.children) {
                        val comment = snapshot.getValue(Commnet::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()


                }
            }

        })
    }

    private fun addNotification() {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "commented:" + add_comment!!.text.toString()
        notiMap["postid"] = postId
        notiMap["isPost"] = true

        notiRef.push().setValue(notiMap)

    }

}