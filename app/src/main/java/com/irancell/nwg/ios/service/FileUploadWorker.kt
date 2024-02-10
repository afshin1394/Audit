package com.irancell.nwg.ios.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.*
import androidx.work.impl.utils.futures.SettableFuture
import com.google.common.util.concurrent.ListenableFuture
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.remote.request.UploadAuditRequest
import com.irancell.nwg.ios.network.patch.UploadAuditService
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.util.FLOW_PIPE
import com.irancell.nwg.ios.util.constants.Problematic
import com.irancell.nwg.ios.util.constants.auditZipGroupFolder
import com.irancell.nwg.ios.util.zipDirectoryWithSubfolders
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.annotations.NonNull
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val uploadAuditService: UploadAuditService
) : CoroutineWorker(appContext, params) {
    private var runAttemptCounts = 0

    companion object {
        const val FileDirPath: String = "FileDirPath"
        const val RegionName: String = "RegionName"
        const val SiteName: String = "SiteName"
        const val AuditId: String = "AuditId"
        const val Type: String = "Type"
        private const val NOTIFICATION_CHANNEL_ID = "11"
        private const val TAG = "FileUploadWorker"
    }


    override suspend fun doWork(): Result {

        runAttemptCounts++
        Log.i(TAG, "doWork: runAttemptCounts"+ runAttemptCounts)

        setForeground(getForegroundInfo())
        val fileDirPath = inputData.getString(FileDirPath)!!
        val regionName = inputData.getString(RegionName)!!
        val siteName = inputData.getString(SiteName)!!
        val auditId = inputData.getInt(AuditId, -1)
        val type = inputData.getInt(Type, -1)

        val originPath =
            "$fileDirPath/AuditImages/$regionName/$siteName/"
        auditZipGroupFolder(fileDirPath, regionName, siteName)
        val destinationPath =
            "$fileDirPath/AuditImagesZip/$regionName/$siteName/"
        val file = File(destinationPath + "audit-" + auditId + ".zip")
        val name = "audit-$auditId"
        zipDirectoryWithSubfolders(
            originPath,
            destinationPath + "audit-" + auditId + ".zip"
        )
        val requestFile: RequestBody = file.asRequestBody("application/zip".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val uploadAuditRequest = UploadAuditRequest(auditId, name, body, 1, 1)

        val call: retrofit2.Call<Unit>
        val t1 = System.currentTimeMillis()
        if (type == Problematic) {
            call = uploadAuditService.patchUploadProblematicAudit(
                uploadAuditRequest.audit_id,
                uploadAuditRequest.name,
                uploadAuditRequest.file,
                uploadAuditRequest.number_of_part,
                uploadAuditRequest.section
            )
        } else {
            call = uploadAuditService.patchUploadAudit(
                uploadAuditRequest.audit_id,
                uploadAuditRequest.name,
                uploadAuditRequest.file,
                uploadAuditRequest.number_of_part,
                uploadAuditRequest.section
            )
        }
        val response = call.execute()
        val t2 = System.currentTimeMillis()

        Log.i(FLOW_PIPE, "apiCall: " + TimeUnit.MILLISECONDS.toSeconds(t2 - t1))
        Log.i(FLOW_PIPE, "apiCall: " + call.request().url)
        Log.i(FLOW_PIPE, "apiCall: " + response.code())
        return if (runAttemptCounts  < 3) {
            if (response.isSuccessful) {
                Log.i(FLOW_PIPE, "apiCall: success")

                Result.success()
            } else {
                Log.i(FLOW_PIPE, "apiCall: retry")

                Result.retry()
            }
        } else {
            Log.i(FLOW_PIPE, "apiCall: fail")

            Result.failure()
        }


    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_CHANNEL_ID.toInt(), createNotification())
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Upload",
                NotificationManager.IMPORTANCE_HIGH,
            )

            val notificationManager: NotificationManager? =
                getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                )

            notificationManager?.createNotificationChannel(
                notificationChannel
            )
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val mainActivityIntent = Intent(
            applicationContext,
            MainActivity::class.java
        )

        var pendingIntentFlag by Delegates.notNull<Int>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE
        } else {
            pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT
        }

        val mainActivityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            mainActivityIntent,
            pendingIntentFlag
        )


        return NotificationCompat.Builder(
            applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher_yellow)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText("Uploading files...")
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
            .build()
    }
}