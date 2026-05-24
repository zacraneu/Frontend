package com.example.front

import android.net.Uri
import com.example.front.domain.model.ApplicationFormState
import com.example.front.domain.model.DocumentUpload
import com.example.front.utils.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import java.io.File

class ApplicationFormStateTest {
    @Test
    fun validate_returnsNullForValidForm() {
        val tempFile = File.createTempFile("test", ".pdf")
        tempFile.writeBytes(ByteArray(1024))
        val state = ApplicationFormState(
            fullName = "Ivan Petrov",
            email = "ivan@example.com",
            phone = "+7-999-123-45-67",
            submissionReason = "Справка",
            documents = listOf(
                DocumentUpload(
                    uri = mock<Uri>(),
                    file = tempFile,
                    name = "passport.pdf"
                )
            )
        )

        assertNull(state.validate())
        tempFile.delete()
    }

    @Test
    fun validate_rejectsLongSubmissionReason() {
        val state = ApplicationFormState(
            fullName = "Ivan Petrov",
            email = "ivan@example.com",
            phone = "+7-999-123-45-67",
            submissionReason = "x".repeat(Constants.MAX_SUBMISSION_REASON_LENGTH + 1)
        )

        assertEquals(
            "Причина подачи не должна превышать ${Constants.MAX_SUBMISSION_REASON_LENGTH} символов",
            state.validate()
        )
    }

    @Test
    fun validate_requiresAtLeastOneDocument() {
        val state = ApplicationFormState(
            fullName = "Ivan Petrov",
            email = "ivan@example.com",
            phone = "+7-999-123-45-67",
            submissionReason = "Справка"
        )

        assertEquals("Загрузите минимум 1 документ", state.validate())
    }
}
