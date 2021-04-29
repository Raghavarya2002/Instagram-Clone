package com.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.AddStoryActivity
import com.example.instagram.Model.Story
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*

//two layouts in one kotlin file , story item ,add story item

class StoryAdapterr(private val mContext : Context, private val mStory : List<Story>):
    RecyclerView.Adapter<StoryAdapterr.ViewHolder>()

{

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        //story item
        var story_image_seen: CircleImageView ? = null
        var story_image: CircleImageView ? = null
        var story_username : TextView ? = null

        //Add Story Item
        var story_plus_btn : ImageView ? = null
        var addStory_text :TextView ? = null


        init {
            //story item
            story_image = itemView.findViewById(R.id.story_image)
            story_image_seen = itemView.findViewById(R.id.story_image_seen)
            story_username = itemView.findViewById(R.id.story_username)

            //Add Story Item
            story_plus_btn = itemView.findViewById(R.id.story_add)
            addStory_text = itemView.findViewById(R.id.add_story_text)




        }

    }

    override fun getItemViewType(position: Int): Int {

        if (position == 0){
            return 0
        }
        return 1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 0){

            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item , parent ,false)
            return ViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.story_item , parent , false)
            return ViewHolder(view)
        }


    }

    override fun getItemCount(): Int {

        return mStory.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val story = mStory[position]
        userinfo(holder , story.getUserId() , position)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext , AddStoryActivity::class.java)
            intent.putExtra("userid" , story.getUserId())
            mContext.startActivity(intent)

        }

    }

    private fun userinfo(viewholder:ViewHolder , userId:String , position: Int) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //  if (context!= null) return
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(viewholder.story_image)

                    if (position != 0){
                        Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                            .into(viewholder.story_image_seen)
                        viewholder.story_username!!.text = user.getUsername()
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}