package com.example.front

import com.example.front.utils.ApiErrorMapper
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ApiErrorMapperTest {
    @Test
    fun mapsIOExceptionToOfflineMessage() {
        val message = ApiErrorMapper.message(IOException())
        assertTrue(message.contains("сет"))
    }
}
