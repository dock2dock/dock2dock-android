package io.dock2dock.android

import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import io.dock2dock.android.configuration.Dock2DockConfiguration
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    inline fun <reified T> getRetrofitClient(): T {
        val config = Dock2DockConfiguration.instance()
        return Retrofit.Builder()
            .baseUrl(config.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClientFactory(config.apiKey))
            .build()
            .create(T::class.java)
    }

    fun okHttpClientFactory(apiKey: String): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                chain.request().newBuilder()
                    .addHeader("x-api-key", apiKey)
                    .build()
                    .let(chain::proceed)
            }
            .build()
    }
}