package com.example.front.data.remote

import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.model.ApplicationMutationResponse
import com.example.front.domain.model.ApplicationsPage
import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.model.AdminApplicationsPage
import com.example.front.domain.model.AdminReviewRequest
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.model.RefreshResponse
import com.example.front.domain.model.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshResponse

    @POST("api/v1/auth/logout")
    suspend fun logout()

    @GET("api/v1/user/profile")
    suspend fun getProfile(): User

    @GET("api/v1/applications")
    suspend fun getApplications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): ApplicationsPage

    @GET("api/v1/applications/{applicationId}")
    suspend fun getApplication(@Path("applicationId") applicationId: String): ApplicationDetail

    @Multipart
    @POST("api/v1/applications")
    suspend fun createApplication(
        @PartMap fields: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @Part documents: List<MultipartBody.Part>
    ): ApplicationMutationResponse

    @Multipart
    @PUT("api/v1/applications/{applicationId}")
    suspend fun updateApplication(
        @Path("applicationId") applicationId: String,
        @PartMap fields: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @Part documents: List<MultipartBody.Part>
    ): ApplicationMutationResponse

    @GET("api/v1/applications/{applicationId}/documents/{documentId}")
    suspend fun downloadDocument(
        @Path("applicationId") applicationId: String,
        @Path("documentId") documentId: String
    ): ResponseBody

    @GET("api/v1/admin/applications")
    suspend fun getAdminApplications(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): AdminApplicationsPage

    @GET("api/v1/admin/applications/{applicationId}")
    suspend fun getAdminApplication(@Path("applicationId") applicationId: String): ApplicationDetail

    @GET("api/v1/admin/applications/{applicationId}/documents/{documentId}")
    suspend fun downloadAdminDocument(
        @Path("applicationId") applicationId: String,
        @Path("documentId") documentId: String
    ): ResponseBody

    @PATCH("api/v1/admin/applications/{applicationId}/approve")
    suspend fun approveApplication(@Path("applicationId") applicationId: String): AdminActionResponse

    @PATCH("api/v1/admin/applications/{applicationId}/reject")
    suspend fun rejectApplication(
        @Path("applicationId") applicationId: String,
        @Body request: AdminReviewRequest
    ): AdminActionResponse

    @PATCH("api/v1/admin/applications/{applicationId}/return")
    suspend fun returnApplication(
        @Path("applicationId") applicationId: String,
        @Body request: AdminReviewRequest
    ): AdminActionResponse
}
