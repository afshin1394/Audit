package com.irancell.nwg.ios.presentation.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irancell.nwg.ios.IMAGE_QUALITY
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.Setting
import com.irancell.nwg.ios.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class FragmentSettingMainViewModel @Inject constructor(
    private val sharedPref: SharedPref
) : ViewModel() {
//    var sharedPrefObservable = MutableLiveData(sharedPref)
    fun updateLanguage(s: String) {
        sharedPref.putString(SELECTED_LANGUAGE,s)
    }

    fun updateImageQuality(s: String) {
        sharedPref.putString(IMAGE_QUALITY,s)
    }

    fun checkSettings() : LiveData<List<Setting>>{
       val selectedLang  = sharedPref.getString(SELECTED_LANGUAGE) as String
       val selectQuality = sharedPref.getString(IMAGE_QUALITY) as String
       return  MutableLiveData(listOf(Setting(SELECTED_LANGUAGE,selectedLang),
           Setting(IMAGE_QUALITY,selectQuality)
       ))
    }
}