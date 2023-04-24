package com.ekh.githubrepo.di

import android.content.Context
import com.ekh.githubrepo.R
import com.ekh.githubrepo.datasource.GithubApi
import com.ekh.githubrepo.util.Flipper
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun providerApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): GithubApi {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(converterFactory)
            .build()
            .create(GithubApi::class.java)
    }

    @Provides
    @Singleton
    fun providesAuthTokenInterceptor(
        @ApplicationContext context: Context
    ): Interceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        val token = context.getString(R.string.token)
        requestBuilder.header("Authorization", "Basic $token")
        chain.proceed(requestBuilder.build())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(FlipperOkhttpInterceptor(Flipper.networkFlipperPlugin))
        .addInterceptor(interceptor)
        .build()

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create(Gson())
}