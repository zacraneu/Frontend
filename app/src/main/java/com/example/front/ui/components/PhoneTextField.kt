package com.example.front.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.front.R
import com.example.front.utils.PhoneFormatter

@Composable
fun PhoneTextField(
    phoneDigits: String,
    onPhoneDigitsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = phoneDigits,
        onValueChange = { input -> onPhoneDigitsChange(PhoneFormatter.normalizeDigits(input)) },
        label = { Text(stringResource(R.string.phone)) },
        placeholder = { Text(stringResource(R.string.phone_placeholder)) },
        modifier = modifier,
        singleLine = true,
        enabled = enabled,
        visualTransformation = PhoneVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        )
    )
}
