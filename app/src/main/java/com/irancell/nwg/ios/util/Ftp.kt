package com.irancell.nwg.ios.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.collections.indices as indices1

interface FtpListener {
    fun onProgress(progress: Int)
    fun onSuccess()
    fun onError()
}

 class FtpUploadManager(
    val serverUrl: String,
    val user: String,
    val pass: String,
    val path: String,
    val regionName : String,
    val siteCode : String,
    ftpListener : FtpListener
) {
    private val wholeSize by lazy {
        dirSize(File(path))
    }

    var progress = 0

    init {
        upload(ftpListener)
    }

    private fun dirSize(dir: File): Long {

        if (dir.exists()) {
            var result: Long = 0
            val fileList = dir.listFiles()
            if (fileList != null) {
                for (i in fileList.indices1) {
                    // Recursive call if it's a directory
                    result += if (fileList[i].isDirectory) {
                        dirSize(fileList[i])
                    } else {
                        // Sum the file size in bytes
                        fileList[i].length()
                    }
                }
            }
            return result // return the file size
        }
        return 0
    }

    fun upload(ftpListener: FtpListener) {

        val ftpClient = FTPClient()
        try {
            Log.i("ssdx", "upload: " + dirSize(File(path)))
            ftpClient.connect("10.221.65.16")
            ftpClient.soTimeout = 10000
            ftpClient.keepAlive = true
            ftpClient.enterLocalPassiveMode();
            if (ftpClient.login("ftp.ios.com|afshin.sa", "1qaz!QAZ")) {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE)
                ftpClient.makeDirectory(regionName)
                ftpClient.changeWorkingDirectory(regionName)
                ftpClient.makeDirectory(siteCode)
                ftpClient.changeWorkingDirectory(siteCode)
                uploadDirectory(ftpClient, "", path, "",ftpListener)
                progress = 100
                ftpListener.onProgress(progress)
                ftpListener.onSuccess()
                Log.i("each", "upload: " + progress)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ftpListener.onError()

        }
    }


    fun uploadDirectory(
        ftpClient: FTPClient,
        remoteDirPath: String, localParentDir: String, remoteParentDir: String
    , ftpListener: FtpListener
    ) {


        var localParentDir = localParentDir
        val localDir = File(localParentDir)
        val subFiles = localDir.listFiles()
        if (subFiles != null && subFiles.isNotEmpty()) {
            for (i in subFiles.indices1) {
                val item = subFiles[i]
                var remoteFilePath = (remoteDirPath + "/" + remoteParentDir
                        + "/" + item.name)
                if (remoteParentDir == "") {
                    remoteFilePath = remoteDirPath + "/" + item.name
                }
                if (item.isFile) {
                    // upload the file
                    val localFilePath = item.absolutePath
                    println("About to upload the file: $localFilePath")
                    val uploaded: Boolean = uploadSingleFile(
                        ftpClient,
                        localFilePath, remoteFilePath
                    ,ftpListener
                    )

                    if (uploaded) {
                        println(
                            "UPLOADED a file to: "
                                    + remoteFilePath
                        )
                    } else {
                        println(
                            ("COULD NOT upload the file: "
                                    + localFilePath)
                        )
                    }

                } else {
                    // create directory on the server
                    Log.i("remote", "uploadDirectory: " + remoteFilePath)
                    val created = ftpClient.makeDirectory(remoteFilePath)
                    if (created) {
                        println(
                            ("CREATED the directory: "
                                    + remoteFilePath)
                        )
                    } else {
                        println(
                            ("COULD NOT create the directory: "
                                    + remoteFilePath)
                        )
                    }

                    // upload the sub directory
                    var parent = remoteParentDir + "/" + item.name
                    if ((remoteParentDir == "")) {
                        parent = item.name
                    }
                    localParentDir = item.absolutePath
                    uploadDirectory(
                        ftpClient,
                        remoteDirPath, localParentDir,
                        parent,
                        ftpListener

                    )
                }
            }
        } else {
            ftpListener.onError()
        }
    }

    fun uploadSingleFile(
        ftpClient: FTPClient,
        localFilePath: String?, remoteFilePath: String?,ftpListener: FtpListener
    ): Boolean {
        val localFile = File(localFilePath)
        val inputStream: InputStream = FileInputStream(localFile)
        return inputStream.use { inputStream ->
            progress += ((localFile.length().toFloat() / wholeSize.toFloat()) * 100).toInt()
            ftpListener.onProgress(progress)
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
            ftpClient.storeFile(remoteFilePath, inputStream)
        }
    }
}










