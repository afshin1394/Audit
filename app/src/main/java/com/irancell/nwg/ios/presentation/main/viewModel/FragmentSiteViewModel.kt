package com.irancell.nwg.ios.presentation.main.viewModel

import AsyncResult
import android.location.Location
import androidx.lifecycle.*
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.request.UploadAuditRequestFTP
import com.irancell.nwg.ios.repository.SiteRepository
import com.irancell.nwg.ios.util.FtpListener
import com.irancell.nwg.ios.util.FtpUploadManager
import com.irancell.nwg.ios.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class FragmentSiteViewModel @Inject constructor(
    private val siteRepository: SiteRepository,
    private val sharedPref: SharedPref,
    val location : LiveData<Location>
) : ViewModel() {
    val filterState = MutableLiveData<Int>()
    val language = MutableLiveData(sharedPref.getString(SELECTED_LANGUAGE))
    val progress = MutableLiveData<Int>()
    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    fun getSitesByProjectId(projectId: Int): LiveData<AsyncResult<List<SiteDomain>>>  {
           return siteRepository.getSitesByProjectId(projectId).asLiveData()
    }

   suspend fun getSitesByProjectIdCoroutine(projectId: Int): List<SiteDomain>  {
      return withContext(Dispatchers.IO) {
          siteRepository.getSitesByProjectIdCoroutine(projectId)
      }
   }
    fun uploadAudit(uploadAuditRequestFTP: UploadAuditRequestFTP) {
        viewModelScope.launch(Dispatchers.IO) {
            FtpUploadManager(
                uploadAuditRequestFTP.serverUrl,
                uploadAuditRequestFTP.serverUserName,
                uploadAuditRequestFTP.serverPassword,
                uploadAuditRequestFTP.folder.path,
                uploadAuditRequestFTP.regionName,
                uploadAuditRequestFTP.siteCode,
                object : FtpListener {
                    override fun onProgress(progress: Int) {
                        viewModelScope.launch(Dispatchers.Main) {
                            this@FragmentSiteViewModel.progress.value = progress
                        }
                    }

                    override fun onSuccess() {
                        viewModelScope.launch(Dispatchers.Main) {
                            delay(1000)
                            this@FragmentSiteViewModel.success.value = true
                        }

                    }

                    override fun onError() {
                        viewModelScope.launch(Dispatchers.Main) {
                            delay(1000)
                            this@FragmentSiteViewModel.error.value = true
                        }
                    }

                })
        }
    }





}