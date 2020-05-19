package com.example.fragmentz

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

object HttpRequest {
    interface RequestCallBacks {
        fun onSuccess(successJsonString: String)
        fun onFailure(failureMessage: String)

    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl("https://reqres.in/api/")
        .build()

    private val service = retrofit.create(ApiService::class.java)


    private fun getCall (requestCallBacks: RequestCallBacks) = object:Callback<String>{
        override fun onFailure(call: Call<String>, t: Throwable) {
            requestCallBacks.onFailure(t.toString())
        }

        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                requestCallBacks.onSuccess(response.body().toString())
            } else{
                val errorJson = JSONObject(response.errorBody()?.string().toString())

                if(errorJson.has("error")) requestCallBacks.onFailure(errorJson.getString("error"))

            }


        }
    }
    fun registerUser(user: UserModel, requestCallBacks: RequestCallBacks) {
        val call = service.registerUserRequest(user.email, user.password)

        call.enqueue(getCall(requestCallBacks))
    }

    fun logInUser(user: UserModel, requestCallBacks: RequestCallBacks) {
        val call = service.logInUserRequest(user.email, user.password)
        call.enqueue(getCall(requestCallBacks))
    }



    fun getSingleUserRequest(userId: Int, requestCallBacks: RequestCallBacks) {
        val call = service.getSingleUserRequest(userId)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) requestCallBacks.onSuccess(response.body().toString())
                else requestCallBacks.onFailure("User not found")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                requestCallBacks.onFailure(t.toString())
            }

        })

    }

    interface ApiService {
        @GET("users/{id}")
        fun getSingleUserRequest(@Path("id") userId: Int): Call<String>

        @FormUrlEncoded
        @POST("register")
        fun registerUserRequest(
            @Field("email") email: String,
            @Field("password") password: String): Call<String>

        @FormUrlEncoded
        @POST("login")
        fun logInUserRequest(
            @Field("email") email: String,
            @Field("password") password: String): Call<String>


    }
}