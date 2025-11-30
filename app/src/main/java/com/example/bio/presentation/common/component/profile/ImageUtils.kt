package com.example.bio.presentation.common.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import java.io.File
import java.io.InputStream
import kotlin.math.min

object ImageUtils {

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cropBitmap(
        originalBitmap: Bitmap,
        userScale: Float,
        userOffset: Offset,
        containerSizePx: Int
    ): Bitmap {
        // 1. ایجاد بوم نهایی (مربعی)
        val outputBitmap = Bitmap.createBitmap(containerSizePx, containerSizePx, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(outputBitmap)

        val matrix = Matrix()

        // 2. محاسبه مقیاس اولیه (Fit) - برای اینکه عکس کامل در کادر جا شود
        val bitmapWidth = originalBitmap.width.toFloat()
        val bitmapHeight = originalBitmap.height.toFloat()

        val scaleX = containerSizePx / bitmapWidth
        val scaleY = containerSizePx / bitmapHeight
        val initialScale = min(scaleX, scaleY)

        // 3. محاسبه موقعیت وسط (برای اینکه عکس در شروع کار، وسط باشد)
        val centeredX = (containerSizePx - (bitmapWidth * initialScale)) / 2f
        val centeredY = (containerSizePx - (bitmapHeight * initialScale)) / 2f

        // --- اعمال تغییرات به ترتیب صحیح ---

        // الف) ابتدا عکس را فیت و وسط‌چین می‌کنیم
        matrix.postScale(initialScale, initialScale)
        matrix.postTranslate(centeredX, centeredY)

        // ب) ✅ تغییر مهم: اول زوم کاربر را اعمال می‌کنیم (حول مرکز کادر)
        val pivotX = containerSizePx / 2f
        val pivotY = containerSizePx / 2f
        matrix.postScale(userScale, userScale, pivotX, pivotY)

        // ج) ✅ سپس جابجایی کاربر را اعمال می‌کنیم (آخرین مرحله)
        // این باعث می‌شود جابجایی دقیقاً به اندازه پیکسل‌هایی باشد که انگشت حرکت کرده
        matrix.postTranslate(userOffset.x, userOffset.y)

        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        // رسم عکس نهایی
        canvas.drawBitmap(originalBitmap, matrix, paint)

        return outputBitmap
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): File? {
        return try {
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}