package com.example.instagram.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Model.Notification
import com.example.instagram.R
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(
    private val mContext : Context , private val mNotification : List<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.notificactions_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {

        return mNotification.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage : ImageView
        var profileImage: CircleImageView
        var userName: TextView
        var text: TextView

        init {
            postImage = itemView.findViewById(R.id.notification_post_image)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            userName = itemView.findViewById(R.id.user_name_comment)
            text = itemView.findViewById(R.id.comment_notification)
        }


    }



}