package com.irancell.nwg.ios.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.util.toast.showToast
import java.io.*


const val READ_BLOCK_SIZE = 100
fun write(context: Context, name: String, content: ByteArray) {
    // add-write text into file
    try {
        val fileout: FileOutputStream = context.openFileOutput(name, MODE_PRIVATE)
        val outputWriter = OutputStreamWriter(fileout)
        outputWriter.write(content.toBase64())
        outputWriter.close()


        showToast(context,R.string.file_saved)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun read(context: Context, name: String): Uri? {
    //reading text from file
    try {
        val fileIn: FileInputStream = context.openFileInput(name)
        val InputRead = InputStreamReader(fileIn)
        val inputBuffer = CharArray(READ_BLOCK_SIZE)
        var s: String? = ""
        var charRead: Int
        while (InputRead.read(inputBuffer).also { charRead = it } > 0) {
            // char to string conversion
            val readstring = String(inputBuffer, 0, charRead)
            s += readstring
        }
        InputRead.close()
        return Uri.fromFile(context.getFileStreamPath(name))

    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        return null
    }
}

fun saveFile(sourceUri: Uri, destinationUri: Uri) {
    val sourceFilename: String = sourceUri.path!!
    val destinationFilename = destinationUri.path!!

    var bis: BufferedInputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        bis = BufferedInputStream(FileInputStream(sourceFilename))
        bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
        val buf = ByteArray(1024)
        bis.read(buf)
        do {
            bos.write(buf)
        } while (bis.read(buf) !== -1)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            bis?.close()
            bos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("Range")
fun getRealPathFromURI(context: Context, contentUri: Uri): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Log.i("version", "getRealPathFromURI: " + Build.VERSION.SDK_INT)

        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor?.moveToFirst() == true) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor?.close()
        return Uri.parse(res)
    } else {
        var finalUri: Uri? = null
        var image_id: String? = null
        var cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            image_id = cursor.getString(0)
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1)
            cursor.close()
        }
        cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            finalUri =
                Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
            cursor.close()
        }
        return finalUri
    }
}


data class Compress(
    val inputImagePath: String,
    val outputImagePath: String,
    val desiredWidth: Int,
    val desiredHeight: Int,
    val targetSize: Int
)

fun compressFile(compress: Compress): File {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(compress.inputImagePath, options)

    val imageWidth = options.outWidth
    val imageHeight = options.outHeight

    // Desired width and height after compression (change as needed)
    val desiredWidth = compress.desiredWidth
    val desiredHeight = compress.desiredHeight

    // Calculate the sample size to scale down the image
    options.inSampleSize =
        calculateInSampleSize(imageWidth, imageHeight, desiredWidth, desiredHeight)
    options.inJustDecodeBounds = false

    val originalBitmap = BitmapFactory.decodeFile(compress.inputImagePath, options)
    val targetFileSize = compress.targetSize
    val originalFileSize = File(compress.inputImagePath).length()
    var quality = 100
    if (targetFileSize.toFloat() <= originalFileSize.toFloat())
        quality = (targetFileSize.toFloat() / originalFileSize.toFloat() * 100).toInt()


    val compressedBitmap =
        compressBitmap(originalBitmap, quality) // Adjust quality (0-100) as needed

    val outputFile = File(compress.outputImagePath)
    val outputStream = FileOutputStream(outputFile)
    compressedBitmap.compress(
        Bitmap.CompressFormat.JPEG,
        100,
        outputStream
    ) // 100 means no compression on the final image
    outputStream.flush()
    outputStream.close()

    return outputFile
}

private fun calculateInSampleSize(
    imageWidth: Int,
    imageHeight: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1

    if (imageHeight > reqHeight || imageWidth > reqWidth) {
        val halfHeight = imageHeight / 2
        val halfWidth = imageWidth / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
    val compressedBitmap = BitmapFactory.decodeByteArray(
        byteArrayOutputStream.toByteArray(),
        0,
        byteArrayOutputStream.size()
    )
    byteArrayOutputStream.flush()
    byteArrayOutputStream.close()
    return compressedBitmap
}








