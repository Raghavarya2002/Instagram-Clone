package com.example.instagram.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.NotificationAdapter
import com.example.instagram.Model.Notification
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class NotificationFragment : Fragment() {


    private var notificationList : List<Notification> ? = null
    private var notificationAdapter : NotificationAdapter? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification, container, false)

        var recyclerView : RecyclerView
        recyclerView =  view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        notificationList = ArrayList()


        notificationAdapter = NotificationAdapter(context!! , notificationList as ArrayList<Notification>)

        recyclerView.adapter = notificationAdapter

        readNotification()

        return  view

    }

    private fun     readNotification() {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){

                        (notificationList as ArrayList<Notification>).clear()

                        for (snapshot in snapshot.children){

                            val notification = snapshot.getValue(Notification::class.java)
                            (notificationList as ArrayList<Notification>).add(notification!!)

                        }

                        Collections.reverse(notificationList)
                        notificationAdapter!!.notifyDataSetChanged()

                    }
            }


        })
    }




}