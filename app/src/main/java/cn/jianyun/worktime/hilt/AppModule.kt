package cn.jianyun.worktime.hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import cn.jianyun.worktime.api.BaseApi
import cn.jianyun.worktime.api.ConfigApi
import cn.jianyun.worktime.api.ShareApi
import cn.jianyun.worktime.api.TraceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("app")
    fun provideAppCache(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile("app") }
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        var logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .readTimeout(Duration.ofSeconds(50L))
            .connectTimeout(Duration.ofSeconds(50L))
            .build()
    }

    @Provides
    @Singleton
    fun provideFestivalApiService(okHttpClient: OkHttpClient): BaseApi {
        return Retrofit.Builder()
            .baseUrl("https://www.kotal.cn")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(BaseApi::class.java)
    }


    @Provides
    @Singleton
    fun provideTraceApi(okHttpClient: OkHttpClient): TraceApi {
        return Retrofit.Builder()
            .baseUrl("https://e.potal.cn/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TraceApi::class.java)
    }


    @Provides
    @Singleton
    fun provideShareApiService(okHttpClient: OkHttpClient): ShareApi {
        return Retrofit.Builder()
            .baseUrl("https://work.potal.cn")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ShareApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConfigApiService(okHttpClient: OkHttpClient): ConfigApi {
        return Retrofit.Builder()
            .baseUrl("https://api.potal.cn")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ConfigApi::class.java)
    }


}
