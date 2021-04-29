package com.example.instagram.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.MyimagesAdapter
import com.example.instagram.Model.Post
import com.example.instagram.Model.User
import com.example.instagram.R
import com.example.instagram.ShowUserActivity
import com.example.instagram.accountSettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myImagesAdapter: MyimagesAdapter? = null
    var postListSaved: List<Post>? = null
    var myImagesAdapterSavedImg: MyimagesAdapter? = null
    var mySavesImg: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid) {
            view.edit_account_setting_btn.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()

        }

        //recyclerview for uploaded images
        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdapter = context?.let { MyimagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter


        //recycler view for saved images
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myImagesAdapterSavedImg =
            context?.let { MyimagesAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg


        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE


        var uploadedImagesBtn: ImageButton
        uploadedImagesBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedImagesBtn.setOnClickListener {

            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }

        var SavedImagesBtn: ImageButton
        SavedImagesBtn = view.findViewById(R.id.images_save_btn)
        SavedImagesBtn.setOnClickListener {

            recyclerViewUploadImages.visibility = View.GONE
            recyclerViewSavedImages.visibility = View.VISIBLE
        }


        view.total_followers.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }
        view.total_following.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }



        view.edit_account_setting_btn.setOnClickListener {
            val getButtonText = view.edit_account_setting_btn.text.toString()
            when {

                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        accountSettingActivity::class.java
                    )
                )
                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }

                    addNotification()


                }

                getButtonText == "Following" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).removeValue()
                    }


                }

            }


        }

        getFollowers()
        getFollowing()
        userinfo()
        myphotos()
        getTotalNumberofPosts()
        mySaves()

        return view
    }


    private fun checkFollowAndFollowingButtonStatus() {

        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if (followingRef != null) {

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()) {
                        view?.edit_account_setting_btn?.text = "Following"

                    } else {
                        view?.edit_account_setting_btn?.text = "Follow"


                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }


    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")



        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    view?.total_followers?.text = snapshot.childrenCount.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }


    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")



        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    view?.total_following?.text = snapshot.childrenCount.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }


    private fun myphotos() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    (postList as ArrayList<Post>).clear()
                    for (snapshot in snapshot.children) {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId)) {

                            (postList as ArrayList<Post>).add(post)
                        }
                        reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun userinfo() {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //  if (context!= null) return
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(view?.pro_img_profile_frag)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_profile_frag?.text = user.getfullname()
                    view?.bio_profile_frag?.text = user.getbio()
                }

            }//1

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("ProfileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("ProfileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("ProfileId", firebaseUser.uid)
        pref?.apply()
    }


    private fun getTotalNumberofPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var postCounter = 0
                    for (snapshot in snapshot.children) {

                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher() == profileId) {
                            postCounter++

                        }
                    }

                    total_posts.text = " " + postCounter
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun mySaves() {

        mySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance()
            .reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savedRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (snapshot in snapshot.children) {

                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }

            }


            override fun onCancelled(error: DatabaseError) {

            }


        })


    }

    private fun readSavedImagesData() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    (postListSaved as ArrayList<Post>).clear()


                    for (snapshot in snapshot.children) {

                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavesImg!!) {

                            if (post!!.getPostid() == key) {
                                (postListSaved as ArrayList<Post>).add(post)

                            }

                        }
                    }

                    myImagesAdapterSavedImg!!.notifyDataSetChanged()

                }
            }


        })

    }

    private fun addNotification() {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "starting following you "
        notiMap["postid"] = ""
        notiMap["isPost"] = false

        notiRef.push().setValue(notiMap)

    }

}













