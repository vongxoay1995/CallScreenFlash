package com.call.colorscreen.flashlight.service

import com.call.colorscreen.flashlight.model.Data
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/getTheme")
    fun getThemes(): Call<Data>
}