package com.example.stackoverflowuser.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by tho nguyen on 2019-05-11.
 */

class NetworkProvider
/**
 * Constructs the different services we use
 */
private constructor() {

    companion object {

        /**
         * The volatile static singleton of [NetworkProvider]
         */
        @Volatile
        private var networkProvider: NetworkProvider? = null

        /**
         * Gets the singleton of the NetworkProvider, to avoid re-constructing retrofit etc...
         * @return The instance of [NetworkProvider]
         */
        val instance: NetworkProvider
            get() {
                synchronized(NetworkProvider::class.java) {
                    if (networkProvider == null) {
                        networkProvider = NetworkProvider()
                    }
                }
                return networkProvider!!
            }
    }

    val defaultOkHttpClient by lazy {
        provideDefaultOkHttpClient()
    }

    private fun provideDefaultOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val timeOut = 120
        builder.connectTimeout(timeOut.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(timeOut.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(timeOut.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
        builder.addInterceptor(interceptor)
        builder.addInterceptor { chain ->
            val request =
                chain.request().newBuilder().addHeader("Content-Type", "application/json").build()
            chain.proceed(request)
        }

        return builder.build()
    }

    inline fun <reified T> provideApi(
        baseUrl: String,
        okHttpClient: OkHttpClient = defaultOkHttpClient
    ): T {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .build()
        return retrofit.create(T::class.java)
    }
}
