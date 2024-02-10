//package com.irancell.nwg.ios.util
//
//import android.net.Uri
//import io.reactivex.Single
//import org.bytedeco.javacpp.opencv_core
//import org.bytedeco.javacpp.opencv_imgcodecs
//import org.bytedeco.javacpp.opencv_imgcodecs.imwrite
//import org.bytedeco.javacpp.opencv_stitching
//import java.io.File
//import java.lang.Exception
//
//class StitcherInput(val uris: List<Uri>, val stitchMode: Int)
//
//sealed class StitcherOutput {
//    class Success(val file: File) : StitcherOutput()
//    class Failure(val e: Exception) : StitcherOutput()
//}
//
//class ImageStitcher(private val fileUtil: FileUtil) {
//
//    fun stitchImages(input: StitcherInput): Single<StitcherOutput> {
//        return Single.fromCallable {
//            val files = fileUtil.urisToFiles(input.uris)
//            val vector = filesToMatVector(files)
//            stitch(vector, input.stitchMode)
//        }
//    }
//
//    private fun stitch(vector: opencv_core.MatVector, stitchMode: Int): StitcherOutput {
//        val result = opencv_core.Mat()
//        val stitcher = opencv_stitching.Stitcher.create(stitchMode)
//        val status = stitcher.stitch(vector, result)
//
//        fileUtil.cleanUpWorkingDirectory()
//        return if (status == opencv_stitching.Stitcher.OK) {
//            val resultFile = fileUtil.createResultFile()
//            imwrite(resultFile.absolutePath, result)
//            StitcherOutput.Success(resultFile)
//        } else {
//            val e = RuntimeException("Can't stitch images: " + getStatusDescription(status))
//            StitcherOutput.Failure(e)
//        }
//    }
//
//    @Suppress("SpellCheckingInspection")
//    private fun getStatusDescription(status: Int): String {
//        return when (status) {
//            opencv_stitching.Stitcher.ERR_NEED_MORE_IMGS -> "ERR_NEED_MORE_IMGS"
//            opencv_stitching.Stitcher.ERR_HOMOGRAPHY_EST_FAIL -> "ERR_HOMOGRAPHY_EST_FAIL"
//            opencv_stitching.Stitcher.ERR_CAMERA_PARAMS_ADJUST_FAIL -> "ERR_CAMERA_PARAMS_ADJUST_FAIL"
//            else -> "UNKNOWN"
//        }
//    }
//
//    private fun filesToMatVector(files: List<File>): opencv_core.MatVector {
//        val images = opencv_core.MatVector(files.size.toLong())
//        for (i in files.indices) {
//            images.put(i.toLong(), opencv_imgcodecs.imread(files[i].absolutePath))
//        }
//        return images
//    }
//}