package com.dock2dock.android.networking

import com.dock2dock.android.networking.clients.IdentityApiClient
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.android.networking.models.Authentication
import com.dock2dock.android.networking.utilities.Constants.IDENTITYAPI_BASE_URL
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.getOrThrow
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiService {
    inline fun <reified T> getRetrofitClient(
        tokenManager: TokenManager,
        configuration: Dock2DockConfiguration,
        baseUrl: String
    ): T {
        var httpClient = unSafeOkHttpClient()
            .addInterceptor(AuthenticationInterceptor(tokenManager, configuration))
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(T::class.java)
    }

    fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> true } // change here
            }

            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }

    class AuthenticationInterceptor(
        private val tokenManager: TokenManager,
        private val configuration: Dock2DockConfiguration
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            val token = runBlocking {
                tokenManager.getToken()
            }
            return runBlocking {

                var newToken = token

                if (newToken.isNullOrEmpty()) {
                    var authenticationResponse = getNewAccessToken(configuration)
                    val expiryDate = Calendar.getInstance()

                    expiryDate.add(Calendar.SECOND, authenticationResponse.expiresInt)

                    tokenManager.saveToken(
                        authenticationResponse.accessToken,
                        expiryDate.timeInMillis
                    )
                    newToken = tokenManager.getToken()
                }
                request = request.newBuilder().header("Authorization", "Bearer $newToken").build()
                chain.proceed(request)
            }
        }
    }

//
//    class AuthAuthenticator(): Authenticator {
//
//        override fun authenticate(route: Route?, response: Response): Request? {
//            val token = runBlocking {
//                tokenManager.getToken()
//            }
//            return runBlocking {
//
//                var newToken = token
//
//                if (newToken.isNullOrEmpty()) {
//                    var authenticationResponse = getNewAccessToken(configuration)
//                    val expiryDate = Calendar.getInstance()
//                    expiryDate.add(Calendar.MINUTE, authenticationResponse.expiresInt)
//
//                    tokenManager.saveToken(
//                        authenticationResponse.accessToken,
//                        expiryDate.timeInMillis
//                    )
//                    newToken = tokenManager.getToken()
//                }
//                response.request.newBuilder()
//                    .header("Authorization", "Bearer $newToken")
//                    .build()
//
//            }
//        }

    private suspend fun getNewAccessToken(configuration: Dock2DockConfiguration): Authentication {
        var httpClient = unSafeOkHttpClient().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(IDENTITYAPI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(httpClient)
            .build()
        val identityClient = retrofit.create(IdentityApiClient::class.java)

        try {
            return identityClient.getToken(
                configuration.clientId,
                configuration.clientSecretKey
            ).getOrThrow()
        } catch (e: Exception) {
            throw IOException(e.message)
        }
    }
}
