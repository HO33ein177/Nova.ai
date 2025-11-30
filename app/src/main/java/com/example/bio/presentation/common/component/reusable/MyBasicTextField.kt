package com.example.bio.presentation.common.component.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection

@Composable
fun MyBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    trailingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    // ✅ تشخیص جهت متن بر اساس اولین کاراکتر
    val isLtr = if (value.isNotEmpty()) {
        val firstChar = value.first()
        // شرط: اگر کاراکتر اول عدد است یا حروف انگلیسی
        firstChar.isDigit() || (firstChar in 'a'..'z' || firstChar in 'A'..'Z')
    } else {
        false // پیش‌فرض (وقتی خالی است) راست‌چین باشد
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(it) } },
        // ✅ Placeholder همیشه راست‌چین باقی می‌ماند (چون فارسی است)
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            { Icon(imageVector = it, contentDescription = label ?: "Icon") }
        },
        // ✅✅✅ استایل هوشمند: اگر عدد/انگلیسی بود چپ، وگرنه راست
        textStyle = LocalTextStyle.current.copy(
            textDirection = if (isLtr) TextDirection.Ltr else TextDirection.ContentOrRtl,
            textAlign = if (isLtr) TextAlign.Left else TextAlign.Start
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.medium
    )
}