package com.irancell.nwg.ios.di

import android.content.Context
import android.util.Log
import androidx.databinding.ktx.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.irancell.nwg.ios.network.*
import com.irancell.nwg.ios.network.get.*
import com.irancell.nwg.ios.network.patch.SubmitAuditService
import com.irancell.nwg.ios.network.patch.SubmitProblematicService
import com.irancell.nwg.ios.network.patch.UploadAuditService
import com.irancell.nwg.ios.network.post.LoginService
import com.irancell.nwg.ios.network.post.LogoutService
import com.irancell.nwg.ios.util.MySSlSocketFactory
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.SESSION_TOKEN
import com.irancell.nwg.ios.util.isOnline
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.thdev.network.flowcalladapterfactory.FlowCallAdapterFactory
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun getSecureRandom(): SecureRandom {
        return SecureRandom()
    }

    @Provides
    @Singleton
    @Named("trustAll")
    fun provideSSLFactory(secureRandom: SecureRandom): SSLSocketFactory {
        return try {
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, MySSlSocketFactory.trustAllCerts, secureRandom)
            // Create an ssl socket factory with our all-trusting manager
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

//    @Provides
//    @Singleton
//    @Named("trustIOS")
//    fun provideSSLFactory(@ApplicationContext context: Context,secureRandom: SecureRandom): SSLContext{
//        var caFileInputStream = context.resources.openRawResource(R.raw.io_sroot_ca)
//
//        // We're going to put our certificates in a Keystore
//        val keyStore = KeyStore.getInstance("PKCS12")
//        keyStore.load(caFileInputStream,"".toCharArray())
//
//        // Create a KeyManagerFactory with our specific algorithm our our public keys
//        // Most of the cases is gonna be "X509"
//        val keyManagerFactory = KeyManagerFactory.getInstance("X509")
//        keyManagerFactory.init(keyStore, "".toCharArray())
//
//        // Create a SSL context with the key managers of the KeyManagerFactory
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(keyManagerFactory.keyManagers, null, secureRandom)
//        return sslContext
//    }

    @Singleton
    @Provides
    @Named("provideOkHttpClient")
    fun provideOkHttpClient(@Named("trustAll") sslSocketFactory: SSLSocketFactory) =
        if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(false)
                .callTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(Interceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build()
                    chain.proceed(newRequest)
                })
                .sslSocketFactory(
                    sslSocketFactory,
                    MySSlSocketFactory.trustAllCerts[0] as X509TrustManager
                )
                .build()
        } else {
            OkHttpClient
                .Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .addInterceptor(Interceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build()
                    chain.proceed(newRequest)
                })
                .sslSocketFactory(
                    sslSocketFactory,
                    MySSlSocketFactory.trustAllCerts[0] as X509TrustManager
                )
                .build()
        }

    @Singleton
    @Provides
    @Named("provideOkHttpClientWithHeader")
    fun provideOkHttpClientWithHeader(
        sharedPref: SharedPref,
        @Named("trustAll") sslSocketFactory: SSLSocketFactory
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false)
            .callTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token ${sharedPref.getString(SESSION_TOKEN)}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(newRequest)
            })
            .sslSocketFactory(
                sslSocketFactory,
                MySSlSocketFactory.trustAllCerts[0] as X509TrustManager
            )
            .build()
    }

     private fun customInterceptor(sharedPref: SharedPref? = null, hasHeader: Boolean): Interceptor {
        return Interceptor { chain ->
            val newRequest: Request = if (hasHeader)
                chain.request().newBuilder()
                    .addHeader("Authorization", "Token ${sharedPref?.getString(SESSION_TOKEN)}")
                    .addHeader("Content-Type", "application/json")
                    .build()
            else
                chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()

            val t1 = System.nanoTime()




            val response = chain.proceed(newRequest)
            response.body?.let {
                val responseBodyString: String = it.toString()

                val t2 = System.nanoTime()

            }

            response
        }
    }

    private fun bodyToString(request: Request): String? {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }


    @Singleton
    @Provides
    @Named("provideRetrofit")
    fun provideRetrofit(@Named("provideOkHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

//    @Singleton
//    @Provides
//    @Named("provideRetrofitRx")
//    fun provideRetrofitRx(@Named("provideOkHttpClient") okHttpClient: OkHttpClient): Retrofit =
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build();


    @Singleton
    @Provides
    @Named("provideRetrofitWithHeader")
    fun provideRetrofitWithHeader(@Named("provideOkHttpClientWithHeader") okHttpClientWithHeader: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(okHttpClientWithHeader)
            .build()


    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideNetworkAccessibility(@ApplicationContext context: Context): Boolean =
        isOnline(context)

    @Provides
    @Singleton
    fun provideLoginService(@Named("provideRetrofit") retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideLogoutService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): LogoutService =
        retrofit.create(LogoutService::class.java)

    @Provides
    @Singleton
    fun provideProjectService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): ProjectService =
        retrofit.create(ProjectService::class.java)

    @Provides
    @Singleton
    fun provideSiteService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): SiteService =
        retrofit.create(SiteService::class.java)

    @Provides
    @Singleton
    fun provideAuditFormService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): AuditFormService =
        retrofit.create(AuditFormService::class.java)


    @Provides
    @Singleton
    fun provideSubmitAuditService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): SubmitAuditService =
        retrofit.create(SubmitAuditService::class.java)

    @Provides
    @Singleton
    fun provideGenerateAuditService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): GenerateAuditService =
        retrofit.create(GenerateAuditService::class.java)

    @Provides
    @Singleton
    fun provideUploadAuditService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): UploadAuditService =
        retrofit.create(UploadAuditService::class.java)


    @Provides
    @Singleton
    fun provideGetAuditService(@Named("provideRetrofitWithHeader") retrofit: Retrofit): SubmittedAuditService =
        retrofit.create(SubmittedAuditService::class.java)

    @Provides
    @Singleton
    fun provideGetProblematicService(@Named("provideRetrofitWithHeader") retrofit: Retrofit) : ProblematicFormService =
        retrofit.create(ProblematicFormService::class.java)

    @Provides
    @Singleton
    fun provideSubmitProblematicService(@Named("provideRetrofitWithHeader") retrofit: Retrofit) : SubmitProblematicService =
        retrofit.create(SubmitProblematicService::class.java)


}