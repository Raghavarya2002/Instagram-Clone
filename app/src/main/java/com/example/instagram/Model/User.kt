package com.example.instagram.Model


class User {

    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""

    constructor()


    constructor(username: String, fullname: String, bio: String, image: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid


    }

    fun getUsername(): String {

        return username
    }

    fun setUsername(username: String) {

        this.username = username
    }

    fun getfullname(): String {

        return fullname
    }

    fun setfullname(fullname: String) {

        this.fullname = fullname
    }

    fun getbio(): String {

        return bio
    }

    fun setbio(bio: String) {

        this.bio = bio
    }

    fun getimage(): String {

        return image
    }

    fun setimage(image: String) {

        this.image = image
    }

    fun getuid(): String {

        return uid
    }

    fun setuid(uid: String) {

        this.uid = uid
    }

}