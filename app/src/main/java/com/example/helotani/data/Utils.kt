import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Buat file sementara untuk menyimpan gambar
fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir ?: context.cacheDir
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", filesDir)
}

// Ubah URI menjadi file
fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    }
    return myFile
}

// Simpan Bitmap ke file
fun bitmapToFile(bitmap: Bitmap, context: Context): File {
    val myFile = createCustomTempFile(context)
    FileOutputStream(myFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
    }
    return myFile
}
