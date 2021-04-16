package com.example.instagram.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Model.Commnet
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private var mContext: Context,
                     private var mCommnet:MutableList<Commnet>?
):RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    private var firebaseUser:FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout , parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mCommnet!!.size

    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mCommnet!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile , holder.userNameTV , comment.getPublisher())

    }


    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){

        var imageProfile : CircleImageView
        var userNameTV : TextView
        var commentTV : TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTV  = itemView.findViewById(R.id.user_name_comment)
            commentTV  = itemView.findViewById(R.id.comment_comment)
        }


    }


    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String) {
            val userREF = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(publisher)
        userREF.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile).into(imageProfile)
                    userNameTV.text = user!!.getUsername()
                }
            }

        })
    }


}