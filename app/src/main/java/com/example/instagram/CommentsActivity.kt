package com.example.instagram

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.CommentAdapter
import com.example.instagram.AppConstants.COMMENT
import com.example.instagram.AppConstants.COMMENTS
import com.example.instagram.AppConstants.IS_POST
import com.example.instagram.AppConstants.POSTS
import com.example.instagram.AppConstants.POST_ID
import com.example.instagram.AppConstants.POST_ID_LOCAL
import com.example.instagram.AppConstants.POST_IMAGE
import com.example.instagram.AppConstants.PUBLISHER
import com.example.instagram.AppConstants.PUBLISHER_ID
import com.example.instagram.AppConstants.TEXT
import com.example.instagram.AppConstants.USERS
import com.example.instagram.AppConstants.USER_ID
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
        postId = intent.getStringExtra(POST_ID_LOCAL)!!
        publisherId = intent.getStringExtra(PUBLISHER_ID)!!
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)

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
                ).show()
            } else {

                addComment()
            }
        })


    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child(COMMENTS)
            .child(postId!!)
        val commentsMap = HashMap<String, Any>()
        commentsMap[COMMENT] = add_comment!!.text.toString()
        commentsMap[PUBLISHER] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        addNotification()
        add_comment!!.text.clear()
    }


    private fun userinfo() {

        val userRef =
            FirebaseDatabase.getInstance().reference.child(USERS).child(firebaseUser!!.uid)
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

        val postRef = FirebaseDatabase.getInstance().reference.child(POSTS).child(postId!!)
            .child(POST_IMAGE)
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
            .child(COMMENTS)
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
            .child(AppConstants.NOTIFICATIONS)
            .child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap[USER_ID] = firebaseUser!!.uid
        notiMap[TEXT] = "commented:" + add_comment!!.text.toString()
        notiMap[POST_ID] = postId
        notiMap[IS_POST] = true


        notiRef.push().setValue(notiMap)

    }

}