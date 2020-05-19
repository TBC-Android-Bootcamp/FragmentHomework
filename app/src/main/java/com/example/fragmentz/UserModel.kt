package com.example.fragmentz

import com.google.gson.annotations.SerializedName

data class UserModel (
    @SerializedName("email")
    val email:String,
    @SerializedName("password")
    val password:String
)