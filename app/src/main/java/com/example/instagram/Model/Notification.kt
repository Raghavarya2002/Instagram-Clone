package com.example.instagram.Model

class Notification {


    private var userid : String = ""
    private var text : String = ""
    private var postid : String = ""
    private var ispost = false

    constructor()

    constructor(userid: String, text: String, postid: String, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.ispost = ispost
    }

    fun getUserId() : String{

        return  userid

    }
     fun  getText() : String{
         return text
     }

    fun  getPostId() : String{
        return postid
    }

    fun  isIsPost() : Boolean{
        return ispost
    }

     fun setuserId(userid: String){
         this.userid = userid
     }
    fun setText(userid: String){
        this.text = text
    }
    fun setPostId(userid: String){
        this.postid = postid
    }
    fun setIsPost(userid: String){
        this.ispost = ispost
    }


}