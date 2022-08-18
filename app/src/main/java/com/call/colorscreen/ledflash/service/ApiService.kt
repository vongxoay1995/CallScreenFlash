package com.call.colorscreen.ledflash.service

import com.call.colorscreen.ledflash.model.Data
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/getTheme")
    fun getThemes(): Call<Data>
}