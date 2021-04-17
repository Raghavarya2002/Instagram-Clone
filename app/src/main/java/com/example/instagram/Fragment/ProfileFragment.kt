package com.example.instagram.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.MyimagesAdapter
import com.example.instagram.Model.Post
import com.example.instagram.Model.User
import com.example.instagram.R
import com.example.instagram.accountSettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myImagesAdapter: MyimagesAdapter? = null


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


        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdapter = context?.let { MyimagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter





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

            }

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


}










