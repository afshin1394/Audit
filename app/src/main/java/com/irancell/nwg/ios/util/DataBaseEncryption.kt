package com.irancell.nwg.ios.util

import android.R
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec


const val ALGORITHM_AES = "AES"
const val KEY_SIZE = 256

private fun generatePassphrase(): ByteArray {
    val keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES)
    keyGenerator.init(KEY_SIZE)
    return keyGenerator.generateKey().encoded
}
const val PASS_PHRASE = "PASS_PHRASE"
 fun savePassPhrase(sharedPref: SharedPref):ByteArray {
    val passphrase = generatePassphrase()
    sharedPref.putString(PASS_PHRASE, passphrase.toString(Charsets.ISO_8859_1))
     return passphrase
}

 fun getPassphrase(sharedPref: SharedPref): ByteArray? {
    val passphraseString = sharedPref.getString(PASS_PHRASE)
    return passphraseString?.toByteArray(Charsets.ISO_8859_1)
}
fun exportEncryptedDB(sharedPref: SharedPref,context: Context) {
    try {
        val appName = context.resources.getString(com.irancell.nwg.ios.R.string.app_name)
        val file = File(Environment.getExternalStorageDirectory().path +  "/" + appName + "/")
        Log.i("pathhhhh", "exportEncryptedDB: "+file.path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val sd: File = Environment.getExternalStorageDirectory()
        if (sd.canWrite()) {
            val data: File = Environment.getDataDirectory()
            val currentDBPath = "//data//" + context.packageName + "//databases//" + "database"
            val backupDBPath = "/$appName/database"
            val currentDB = File(data, currentDBPath)
            val backupDB = File(sd, backupDBPath)
            val src = FileInputStream(currentDB)
            val dst = FileOutputStream(backupDB)

            // KEY Length 16 byte
            val sks = SecretKeySpec(getPassphrase(sharedPref), "AES")
            // Create cipher
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, sks)
            val cos = CipherOutputStream(dst, cipher)
            val b = ByteArray(8)
            var i: Int = src.read(b)
            while (i != -1) {
                cos.write(b, 0, i)
                i = src.read(b)
            }
            src.close()
            dst.close()
            cos.flush()
            cos.close()
            Log.e(
                "EXPORT_DB", """
     Database has been exported to
     ${backupDB.toString()}
     """.trimIndent()
            )
        } else {
            Log.e("EXPORT_DB", "No storage permission.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("EXPORT_DB", "Error exporting database!")
    }
}


fun exportDatabaseFile(context: Context) {
    try {
        copyDataFromOneToAnother(context.getDatabasePath("database").path, Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + "database")
        copyDataFromOneToAnother(context.getDatabasePath("database" + "-shm").path, Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + "database" + "-shm")
        copyDataFromOneToAnother(context.getDatabasePath("database" + "-wal").path, Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + "database" + "-wal")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun copyDataFromOneToAnother(fromPath: String, toPath: String) {
    val inStream = File(fromPath).inputStream()
    val outStream = FileOutputStream(toPath)

    inStream.use { input ->
        outStream.use { output ->
            input.copyTo(output)
        }
    }
}