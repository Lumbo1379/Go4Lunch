package com.example.go4lunch.utils

import android.content.Context
import android.location.Location
import androidx.annotation.Nullable
import com.example.go4lunch.models.Places
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference

class APICalls {

    interface ICallBacks {
        fun onResponse(@Nullable places: Places?)
        fun onFailure()
    }

    companion object {
        private lateinit var mRetrofit: Retrofit

        fun fetchPlaces(callbacks: ICallBacks, location: Location, radius: Int, type: String, keyword: String, apiKey: String) {
            val callbacksWeakReference = WeakReference(callbacks)
            val mapsService = mRetrofit.create(IMapsService::class.java)

            val call = mapsService.getPlaces(QueryParse.parseLocation(location), radius, type, keyword, apiKey)
            call.enqueue(object : Callback<Places> {
                override fun onResponse(call: Call<Places>, response: Response<Places>) {
                    if (callbacksWeakReference.get() != null) callbacksWeakReference.get()!!.onResponse(response.body())
                }
                override fun onFailure(call: Call<Places>, t: Throwable) {
                    if (callbacksWeakReference.get() != null) callbacksWeakReference.get()!!.onFailure();
                }
            })
        }

        fun init(context: Context) {
            val cacheSize = (5 * 1024 * 1024).toLong()
            val cache = Cache(context.cacheDir, cacheSize)

            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor { chain ->
                    var request = chain.request()

                    request = if (Connectivity.hasNetwork(context)!!)
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()

                    chain.proceed(request)
                }.build()

            mRetrofit = Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
    }
}