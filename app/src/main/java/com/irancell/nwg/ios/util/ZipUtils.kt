package com.irancell.nwg.ios.util

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


fun zipAll(directory: String, zipFile: String) {
    val sourceFile = File(directory)
    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
        it.use {
            zipFiles(it, sourceFile, "")
        }
    }
}

private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

    val data = ByteArray(2048)
    try {
    for (f in sourceFile.listFiles()) {

        if (f.isDirectory) {
            val entry = ZipEntry(f.name + File.separator)
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()

            Log.i("zip", "Adding Directory: " + f.name)
            zipOut.putNextEntry(entry)

            //Call recursively to add files within this directory
            zipFiles(zipOut, f, f.name)
        } else {

            if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                FileInputStream(f).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val path = parentDirPath + File.separator + f.name
                        Log.i("zip", "Adding file: $path")
                        val entry = ZipEntry(path)
                        entry.time = f.lastModified()
                        entry.isDirectory
                        entry.size = f.length()
                        zipOut.putNextEntry(entry)
                        while (true) {
                            val readBytes = origin.read(data)
                            if (readBytes == -1) {
                                break
                            }
                            zipOut.write(data, 0, readBytes)
                        }
                    }
                }
            } else {
                zipOut.closeEntry()
                zipOut.close()
            }
        }
    }
    }catch (e :java.lang.Exception){
        Log.i("messageee", "zipFiles: "+e.message)
    }
}

fun zipFile(directory: String, zipFile: String)
{
    val dir = File(directory)
    // Create a ZipOutputStream to write the zip file to
    val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
    // Iterate over the files in the directory
    for (file in dir.listFiles()) {
        // Create a ZipEntry for the file
        val zipEntry = ZipEntry(file.name)
        // Add the file to the zip file
        zipOutputStream.putNextEntry(zipEntry)
        // Copy the file contents to the zip file
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            zipOutputStream.write(buffer, 0, bytesRead)
        }
        // Close the input stream
        inputStream.close()
        // Close the ZipEntry
        zipOutputStream.closeEntry()
    }
    // Close the ZipOutputStream
    zipOutputStream.close()
}
fun zipDirectory(directory: File, zipFile: File) {
//    zipDirectory(directory,zipFile)
    val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
    val zipEntry = ZipEntry(directory.name)
    zipOutputStream.putNextEntry(zipEntry)
    val files = directory.listFiles()
    for (file in files) {
        if (file.isDirectory) {
            zipDirectory(file, zipFile)
        } else {
            zipFile(file, zipOutputStream)
        }
    }
    zipOutputStream.close()

}
private fun zipFile(file: File, zipOutputStream: ZipOutputStream) {
    val inputStream = FileInputStream(file)
    val entry = ZipEntry(file.name)
    zipOutputStream.putNextEntry(entry)
    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        zipOutputStream.write(buffer, 0, bytesRead)
    }
    inputStream.close()
    zipOutputStream.closeEntry()
}


fun getAllFiles(dir: File, fileList: ArrayList<File>) {
    try {
        val files = dir.listFiles()
        for (file in files) {
            fileList.add(file)
            if (file.isDirectory) {
                System.out.println("directory:" + file.canonicalPath)
                getAllFiles(file, fileList)
            } else {
                System.out.println("     file:" + file.canonicalPath)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun writeZipFile(directoryToZip: File, fileList: List<File>) {
    try {
        val fos = FileOutputStream(directoryToZip.name)
        val zos = ZipOutputStream(fos)
        for (file in fileList) {
            if (!file.isDirectory) { // we only zip files, not directories
                addToZip(directoryToZip, file, zos)
            }
        }
        zos.close()
        fos.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

@Throws(FileNotFoundException::class, IOException::class)
fun addToZip(directoryToZip: File, file: File, zos: ZipOutputStream) {
    val fis = FileInputStream(file)

    // we want the zipEntry's path to be a relative path that is relative
    // to the directory being zipped, so chop off the rest of the path
    val zipFilePath = file.canonicalPath.substring(
        directoryToZip.canonicalPath.length + 1,
        file.canonicalPath.length
    )
    println("Writing '$zipFilePath' to zip file")
    val zipEntry = ZipEntry(zipFilePath)
    zos.putNextEntry(zipEntry)
    val bytes = ByteArray(1024)
    var length: Int
    while (fis.read(bytes).also { length = it } >= 0) {
        zos.write(bytes, 0, length)
    }
    zos.closeEntry()
    fis.close()
}



 fun zipDirectoryWithSubfolders(sourceDir : String,destination : String) {
    val sourceDirFile = File(sourceDir)
    val destinationDir = File(destination)// Replace with the actual source directory path
    val destinationZipFile = File(destination) // Replace with the desired destination zip file path

    try {
        val zipOutputStream = ZipOutputStream(FileOutputStream(destinationZipFile))
        zipDirectory(sourceDirFile, destinationDir.name.dropLast(4), zipOutputStream)
        zipOutputStream.close()
    } catch (e: Exception) {
        Log.i("DirZip", "zip not satisfied: zip error"+e.message)
        e.printStackTrace()
    }
}

private fun zipDirectory(directory: File, parentPath: String, zipOutputStream: ZipOutputStream) {
    val files = directory.listFiles()

    for (file in files) {
        if (file.isDirectory) {
            val entryPath = parentPath + File.separator + file.name + File.separator
            zipOutputStream.putNextEntry(ZipEntry(entryPath))
            zipDirectory(file, entryPath, zipOutputStream)
        } else {
            val entryPath = parentPath + File.separator + file.name
            zipOutputStream.putNextEntry(ZipEntry(entryPath))

            val fileInputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                zipOutputStream.write(buffer, 0, length)
            }

            fileInputStream.close()
            zipOutputStream.closeEntry()
        }
    }
}
