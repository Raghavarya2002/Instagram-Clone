package com.example.instagram.Model

class Post {
    private var postid: String = ""
    private var description: String = ""
    private var publisher: String = ""
    private var postimage: String = ""

    constructor()
    constructor(postid: String, description: String, publisher: String, postimage: String) {
        this.postid = postid
        this.description = description
        this.publisher = publisher
        this.postimage = postimage
    }

    fun getPostid():String{
        return postid
    }
    fun getPostimage():String{
        return postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getDescription():String{
        return description
    }
    fun setPostid(postid:String){
        this.postid = postid
    }
    fun setPostimage(postimage:String){
        this.postimage = postimage
    }
    fun setPublisher(publisher:String){
        this.publisher = publisher
    }
    fun setDescription(description:String){
        this.description = description
    }




}