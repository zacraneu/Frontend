package com.example.front.data.repository

import com.example.front.data.remote.ApiService
import com.example.front.domain.model.User
import com.example.front.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override suspend fun getProfile(): User = apiService.getProfile()
}
