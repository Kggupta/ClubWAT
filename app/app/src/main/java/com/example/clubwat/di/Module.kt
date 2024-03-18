package com.example.clubwat.di

import com.example.clubwat.repository.DiscussionRepository
import com.example.clubwat.repository.DiscussionRepositoryImpl
import com.example.clubwat.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDiscussionRepository(): DiscussionRepository {
        return DiscussionRepositoryImpl()
    } // return our specific impl of this repo

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

}