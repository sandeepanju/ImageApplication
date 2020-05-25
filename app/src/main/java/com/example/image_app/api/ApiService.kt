package com.example.image_app.api


import com.example.image_app.ui.data.MImage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers(
        "Accept-Version: v1",
        "Authorization: Client-ID dVIdmG3Vo9jFATtiURrw_Gm4ZV4MXbL-cGo9NuM7B9E"
    )
     @GET("photos")
    fun getImageList( @Query("page") pageNumber: Int,
                      @Query("per_page") perPage: Int=10,
                      @Query("order_by") orderBy: String="latest"): Single<List<MImage>>
}
