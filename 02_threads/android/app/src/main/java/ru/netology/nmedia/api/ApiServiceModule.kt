package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiServiceModule {
    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return retrofit(okHttpClient)
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkhttp(logging: HttpLoggingInterceptor, auth: AppAuth): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                auth.authStateFlow.value.token?.let { token ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", token)
                        .build()
                    return@addInterceptor chain.proceed(newRequest)
                }
                chain.proceed(chain.request())
            }
            .build()
    }
}