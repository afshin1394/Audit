package com.irancell.nwg.ios.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import java.io.*
import java.nio.charset.StandardCharsets


fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun ByteArray.toBase64(): String =
    String(this, StandardCharsets.UTF_8)


fun ByteArray.bytetoUTF8(): String =
    String(this, StandardCharsets.UTF_8)


fun String.utf8toByte(): ByteArray =
    this.toByteArray(StandardCharsets.UTF_8)

fun String.toByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)

fun ByteArray.toBitmap(): Bitmap {
    val options = BitmapFactory.Options()

    return BitmapFactory.decodeByteArray(this, 0, this.size, options)
}


fun String.fromBase64(): ByteArray =
    this.toByteArray(StandardCharsets.UTF_8)


const val TOP = 1
const val BOTTOM = 2
fun Bitmap.drawTextToBitmap(
    screenScale: Float,
    frameColor: Int,
    textColor: Int,
    textSize: Float = 4f,
    typeface: Typeface,
    text: String, position: Int
): Bitmap {

    val canvas = Canvas(this)

    // new antialised Paint - empty constructor does also work
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.isSubpixelText = false
    paint.color = textColor
    paint.textSize = textSize * screenScale
    paint.typeface = Typeface.create(typeface, Typeface.NORMAL)
    // draw text to the Canvas center
    //draw the text

    //x and y defines the position of the text, starting in the top left corner
    var frame = Paint(Paint.ANTI_ALIAS_FLAG)
    frame.color = frameColor
    var rectf = RectF()

    when (position) {
        TOP -> {
            rectf.set(0f, 60f, this.width.toFloat(), 140f)
            canvas.drawRect(rectf, frame)
            canvas.drawText(text, 80f, 120f, paint)
        }
        BOTTOM -> {
            rectf.set(0f,this.height.toFloat() - 180, this.width.toFloat(), this.height.toFloat() - 100)
            canvas.drawRect(rectf, frame)
            canvas.drawText(text, 80f, this.height.toFloat() - 120f, paint)
        }

    }
    return this
}


fun getRealPathFromURI(contentResolver: ContentResolver, contentUri: Uri?): Uri? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor = contentResolver.query(contentUri!!, proj, null, null, null)!!;
    val column_index: Int = cursor
        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    var string = cursor.getString(column_index)
    cursor.close()
    return Uri.fromFile(File(string))
}

fun saveBitmapToLocation(bitmap: Bitmap , context: Context, file: File) {
    file.delete();
    if (file.exists()) {
        file.getCanonicalFile().delete();

    }
    val os = BufferedOutputStream(
        FileOutputStream(file)
    )
    bitmap.compress(CompressFormat.JPEG, 100, os)
    os.flush()
    os.close()
}

fun getBitmapFromUri(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
        }
    } catch (e: Exception) {
        null
    }
}

@SuppressLint("UseCompatLoadingForDrawables")
fun createDrawableFromUri(context: Context, uri : Uri) : Drawable{
    return try {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        Drawable.createFromStream(inputStream, uri.toString())!!
    } catch (e: FileNotFoundException) {
        context.resources.getDrawable(com.irancell.nwg.ios.R.drawable.black_background)
    }
}

