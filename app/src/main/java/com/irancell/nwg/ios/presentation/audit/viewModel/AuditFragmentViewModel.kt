package com.irancell.nwg.ios.presentation.audit.viewModel

import android.location.Location
import androidx.lifecycle.*
import com.google.gson.Gson
import com.irancell.nwg.ios.IMAGE_QUALITY
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.repository.AuditFormRepository
import com.irancell.nwg.ios.repository.GenerateAttributeRepository
import com.irancell.nwg.ios.repository.ProblematicFormRepository
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.MAIN
import com.irancell.nwg.ios.util.constants.PROBLEMATIC
import com.irancell.nwg.ios.util.factory.AuditFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class AuditFragmentViewModel @Inject constructor(
    val location: LiveData<Location>,
    private val gson: Gson,
    private val auditFormRepository: AuditFormRepository,
    private val problematicFormRepository: ProblematicFormRepository,
    private val generateAttributeRepository: GenerateAttributeRepository,
    private val sharedPref: SharedPref
) :
    ViewModel() {


    val language = MutableLiveData(sharedPref.getString(SELECTED_LANGUAGE))
    lateinit var subGroup: SubGroup
    lateinit var element: AttrElement
    fun getAttributesByGroupId(siteId: Int?, groupId: String, formType: Int): LiveData<Group> {
        return when (formType) {

            MAIN -> {
                auditFormRepository.getSiteAttributesByGroupId(siteId, groupId)
                    .flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }
            PROBLEMATIC -> {
                problematicFormRepository.getSiteAttributesByGroupId(siteId, groupId)
                    .flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }
            else -> {

                auditFormRepository.getSiteAttributesByGroupId(siteId, groupId)
                    .flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }


        }


    }




    fun deleteImage(siteId: Int, groupId: UUID, subGroup: SubGroup, attrElement: AttrElement) {
        viewModelScope.launch(Dispatchers.IO) {

//           var attrElement = findCurrentStateElement()
            auditFormRepository.deleteImage(siteId, groupId, subGroup, attrElement)
        }
    }

    fun getTime(): String {
        val timeClient = NTPUDPClient()
        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm")
        val timeInfo = timeClient.getTime(InetAddress.getByName("0.asia.pool.ntp.org"))
        val returnTime = timeInfo.returnTime
        return returnTime.getDateFromMillis(sdf)
    }


    fun updateAttrs(siteId: Int, groupId: String, subGroups: ArrayList<SubGroup>?, formType: Int) {
        viewModelScope.launch {
            when (formType) {
                MAIN -> {
                    subGroups?.let { subGroups ->
                        if (subGroups.size > 0)
                            auditFormRepository.updateGroup(siteId, groupId, subGroups)
                    }

                }
                PROBLEMATIC -> {
                    subGroups?.let { subGroups ->
                        if (subGroups.size > 0)
                            problematicFormRepository.updateGroup(siteId, groupId, subGroups)
                    }
                }
            }
        }

    }

    fun generate(siteId: Int, element: AttrElement, subGroup: SubGroup, groupId: String) {
        viewModelScope.launch {
            AuditFactory(
                siteId,
                gson,
                auditFormRepository,
                generateAttributeRepository,
                sharedPref.getString(SELECTED_LANGUAGE, "en")!!
            )
                .build(element, subGroup, groupId)
        }

    }

    fun remove(siteId: Int, element: AttrElement, subGroup: SubGroup, groupId: String) {
        viewModelScope.launch {
            AuditFactory(
                siteId,
                gson,
                auditFormRepository,
                generateAttributeRepository,
                sharedPref.getString(SELECTED_LANGUAGE, "en")!!
            ).remove(element, subGroup, groupId)
        }
    }

    fun compressFile(originPath: String, destinationPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (sharedPref.getString(IMAGE_QUALITY)) {
                LOW -> {
                    compressFile(Compress(originPath, destinationPath, 720, 1280, 700000))
                }
                MEDIUM -> {
                    compressFile(Compress(originPath, destinationPath, 720, 1280, 700000))
                }
                HIGH -> {
                    compressFile(Compress(originPath, destinationPath, 720, 1280, 700000))
                }
            }
        }


    }

}