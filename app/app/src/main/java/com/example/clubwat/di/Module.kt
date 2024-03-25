package com.example.clubwat.di

import com.example.clubwat.repository.ApproveClubRepository
import com.example.clubwat.repository.ApproveClubRepositoryImpl
import com.example.clubwat.repository.CreateClubRepository
import com.example.clubwat.repository.CreateClubRepositoryImpl
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
    }

    @Singleton
    @Provides
    fun provideCreateClubRepository(): CreateClubRepository {
        return CreateClubRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideApproveClubRepository(): ApproveClubRepository {
        return ApproveClubRepositoryImpl()
    } // return our specific impl of this repo

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

}