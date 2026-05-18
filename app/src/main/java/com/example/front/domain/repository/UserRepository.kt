package com.example.front.domain.repository

import com.example.front.domain.model.User

interface UserRepository {
    suspend fun getProfile(): User
}
