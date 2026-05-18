package com.example.front.data.di

import com.example.front.data.repository.ApplicationRepositoryImpl
import com.example.front.data.repository.AuthRepositoryImpl
import com.example.front.data.repository.UserRepositoryImpl
import com.example.front.domain.repository.ApplicationRepository
import com.example.front.domain.repository.AuthRepository
import com.example.front.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindApplicationRepository(impl: ApplicationRepositoryImpl): ApplicationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
