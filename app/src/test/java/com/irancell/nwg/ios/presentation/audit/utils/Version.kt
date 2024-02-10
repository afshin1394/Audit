package com.irancell.nwg.ios.presentation.audit.utils

import android.os.Build
import com.irancell.nwg.ios.BuildConfig
import org.junit.Before
import org.junit.Test
import java.util.*

class Version {
     var serverVersion : Int = 2
    var needForceUpdate = false

    var state = ""

    val force_update = "force_update"
    val recommendedUpdate = "recommendedUpdate"
    val faultyVersion = "faultyVersion"

    @Before
    fun setBefore(){
        serverVersion = Random().nextInt(5)
        needForceUpdate = Random().nextBoolean()
        println(serverVersion)
        println(needForceUpdate)
    }

    @Test
    fun `check for recommended update`(){
        if( BuildConfig.VERSION_CODE <= serverVersion ){
            if (needForceUpdate){
               state = force_update
            }else{
              state = recommendedUpdate
            }
        }else{
            state = faultyVersion
        }
        println(state)
    }
}