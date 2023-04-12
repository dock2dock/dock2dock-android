package io.dock2dock.networking

import io.dock2dock.networking.configuration.Dock2DockConfiguration
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    inline fun <reified T> getRetrofitClient(
        configuration: Dock2DockConfiguration,
        baseUrl: String
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClientFactory(configuration.apiKey))
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


//    fun unSafeOkHttpClient(): OkHttpClient.Builder {
//        val okHttpClient = OkHttpClient.Builder()
//        try {
//            // Create a trust manager that does not validate certificate chains
//            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
//                override fun checkClientTrusted(
//                    chain: Array<out X509Certificate>?,
//                    authType: String?
//                ) {
//                }
//
//                override fun checkServerTrusted(
//                    chain: Array<out X509Certificate>?,
//                    authType: String?
//                ) {
//                }
//
//                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//            })
//
//            // Install the all-trusting trust manager
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, trustAllCerts, SecureRandom())
//
//            // Create an ssl socket factory with our all-trusting manager
//            val sslSocketFactory = sslContext.socketFactory
//            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
//                okHttpClient.sslSocketFactory(
//                    sslSocketFactory,
//                    trustAllCerts.first() as X509TrustManager
//                )
//                okHttpClient.hostnameVerifier { _, _ -> true } // change here
//            }
//
//            return okHttpClient
//        } catch (e: Exception) {
//            return okHttpClient
//        }
//    }

//    class AuthenticationInterceptor(
//        private val tokenManager: TokenManager,
//        private val configuration: Dock2DockConfiguration
//    ) : Interceptor {
//        override fun intercept(chain: Interceptor.Chain): Response {
//            var request = chain.request()
//
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
//
//                    expiryDate.add(Calendar.SECOND, authenticationResponse.expiresInt)
//
//                    tokenManager.saveToken(
//                        authenticationResponse.accessToken,
//                        expiryDate.timeInMillis
//                    )
//                    newToken = tokenManager.getToken()
//                }
//                request = request.newBuilder().header("Authorization", "Bearer $newToken").build()
//                chain.proceed(request)
//            }
//        }
//    }


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

//    private suspend fun getNewAccessToken(configuration: Dock2DockConfiguration): Authentication {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(IDENTITYAPI_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
//            .build()
//        val identityClient = retrofit.create(IdentityApiClient::class.java)
//
//        try {
//            return identityClient.getToken(
//                configuration.clientId,
//                configuration.clientSecretKey
//            ).getOrThrow()
//        } catch (e: Exception) {
//            throw IOException(e.message)
//        }
//    }
}
