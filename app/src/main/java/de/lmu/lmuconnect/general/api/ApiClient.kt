package de.lmu.lmuconnect.general.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.lmu.lmuconnect.general.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private lateinit var apiService: ApiService

    var gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .setLenient()
        .create()

    fun getApiService(): ApiService {
        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }
}