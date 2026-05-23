package com.example.front.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authenticated
