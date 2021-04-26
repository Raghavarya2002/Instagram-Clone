package com.example.instagram.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Model.Story
import com.example.instagram.R
import de.hdodenhof.circleimageview.CircleImageView

//two layouts in one kotlin file , story item ,add story item

class StoryAdapter(private val mContext : Context , private val mStory : List<Story>):RecyclerView.Adapter<StoryAdapter.ViewHolder>()

{

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        //story item
        var story_image: CircleImageView ? = null
        var story_image_seen: CircleImageView ? = null
        var story_username : TextView ? = null

        //Add Story Item
        var story_plus_btn : CircleImageView ? = null
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

        return  if (viewType == 0){

            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item , parent ,false)
             ViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.story_item , parent , false)
             ViewHolder(view)
        }


    }

    override fun getItemCount(): Int {

        return mStory.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val story = mStory[position]

    }


}