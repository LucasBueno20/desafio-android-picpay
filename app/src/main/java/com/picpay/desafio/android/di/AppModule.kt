package com.picpay.desafio.android.di

import android.app.Application
import androidx.room.Room
import com.picpay.desafio.android.data.local.UserDao
import com.picpay.desafio.android.data.local.UserDatabase
import com.picpay.desafio.android.data.remote.PicPayService
import com.picpay.desafio.android.data.repository.UserListRepositoryImpl
import com.picpay.desafio.android.domain.repository.UserListRepository
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.presentation.home.HomeFragmentViewModel
import com.picpay.desafio.android.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object AppModule {

    fun allModules() = listOf(
        viewModel,
        useCase,
        repository,
        netModule,
        dbModule
    )

    private val viewModel = module {

        viewModel { HomeFragmentViewModel(get()) }
    }

    private val useCase = module {
        single { GetUsersUseCase(get()) }
    }

    private val repository = module {
        single<UserListRepository> { UserListRepositoryImpl(get(), get()) }
    }

    private val netModule = module {

        val TIME_OUT = 30L

        fun provideHttpClient(): OkHttpClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            return OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()
        }


        fun provideRetrofit(client: OkHttpClient, url: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun provideService(retrofit: Retrofit): PicPayService {
            return retrofit.create(PicPayService::class.java)
        }

        single { provideHttpClient() }
        single { provideRetrofit(get(), Constants.BASE_URL) }
        single { provideService(get()) }
    }

    private val dbModule = module {
        fun provideDatabase(application: Application): UserDatabase {
            return Room.databaseBuilder(application, UserDatabase::class.java, "user_db")
                .fallbackToDestructiveMigration()
                .build()
        }

        fun provideDao(database: UserDatabase): UserDao {
            return database.dao
        }

        single { provideDatabase(androidApplication()) }
        single { provideDao(get()) }
    }
}