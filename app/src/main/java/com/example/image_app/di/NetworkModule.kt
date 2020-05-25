package com.example.image_app.di

import com.example.image_app.BuildConfig
import com.example.image_app.api.ApiService
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val MODULE_NAME = "Network Module"
val networkModule =Kodein.Module(MODULE_NAME,false){
  bind<OkHttpClient>() with singleton { getOkhttpClient() }
  bind<Retrofit>() with singleton { getRetrofit(instance()) }
    bind<ApiService>() with singleton { getApiService(instance()) }
}

fun getOkhttpClient(): OkHttpClient {
    val httpBuilder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpBuilder.interceptors().add(httpLoggingInterceptor)
    return httpBuilder.build()
}

fun getRetrofit(okHttpClient: OkHttpClient): Retrofit =Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(
        RxJava2CallAdapterFactory.create())
    .client(okHttpClient)
    .build()

private fun getApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
