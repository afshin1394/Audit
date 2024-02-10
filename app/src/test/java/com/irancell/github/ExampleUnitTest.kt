package com.irancell.github

import android.util.Log
import com.google.gson.Gson
import com.irancell.nwg.ios.data.local.DatabaseAttributes
import com.irancell.nwg.ios.data.local.asRemoteModel
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.FormValue
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import com.irancell.nwg.ios.data.remote.response.audit.Extra
import com.irancell.nwg.ios.data.remote.response.audit.Option
import com.irancell.nwg.ios.database.dao.SubmitAttributeDao
import com.irancell.nwg.ios.repository.UploadAuditRepository
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Inject
    lateinit var submitAuditDao: SubmitAttributeDao

    @Test
    fun populateData() {
        var options: ArrayList<Option> = arrayListOf()
        var i = 0.0
        var counter = 0
        while (i < 15) {
            i += 0.5f
            counter++
            options.add(Option(UUID.randomUUID().toString(), (i).toString(), i.toDouble()))
        }
        var data = Extra(null,options)
        println(Gson().toJson(data))

    }

    @Test
    fun populateOptions(){
        val options: ArrayList<Option> = arrayListOf()
        options.add(Option(UUID.randomUUID().toString(),"LandLord Issue",0.0))
        options.add(Option(UUID.randomUUID().toString(),"Key Issue",0.0))
        options.add(Option(UUID.randomUUID().toString(),"Permission-MCI",0.0))
        options.add(Option(UUID.randomUUID().toString(),"Permission-Rightel",0.0))
        options.add(Option(UUID.randomUUID().toString(),"Permission-Organization",0.0))
        options.add(Option(UUID.randomUUID().toString(),"Access Issue",0.0))
        val extra = Extra(null,options)
        println(Gson().toJson(extra))

    }
//
//    @Test
//    fun getSortedLeveledList(
//        level: Int,
//        parentId: String,
//        listOfAttributes: ArrayList<DatabaseAttributes>,
//        leveledList: ArrayList<UploadAuditRepository.LevelAttribute>
//    ): ArrayList<UploadAuditRepository.LevelAttribute> {
//        for (listOfAttribute in listOfAttributes) {
//            if (listOfAttribute.parentGroupId == parentId)
//                leveledList.add(UploadAuditRepository.LevelAttribute(level, arrayListOf(listOfAttribute)))
//        }
//        if (listOfAttributes.size <= leveledList.size)
//            return leveledList
//        else {
//            val veraity = getVerityLevels(leveledList)
//            for (levelAttribute in leveledList) {
//                return getSortedLeveledList(
//                    veraity,
//                    levelAttribute.attributes[0].groupId,
//                    listOfAttributes,
//                    leveledList
//                )
//            }
//            return leveledList
//        }
//
//    }
//
//    @Test
//    private fun getVerityLevels(levelListAttributes: ArrayList<UploadAuditRepository.LevelAttribute>): Int {
//        var v = 0
//        var level = -1
//        for (levelListAttribute in levelListAttributes) {
//            if (levelListAttribute.level != level) {
//                level = levelListAttribute.level
//                v++
//            }
//        }
//        return v
//    }

//    @Test
//    fun getParentAttribute(
//        siteId: Int,
//        databaseAttributes: DatabaseAttributes
//    ): DatabaseAttributes {
//        var databaseAttributesx: DatabaseAttributes = databaseAttributes
//        while (databaseAttributes.parentGroupId != "-1") {
//            databaseAttributesx =
//                auditFormDao.getAttributeWithFeatures(siteId, databaseAttributes.parentGroupId)
//            getParentAttribute(siteId, databaseAttributesx)
//        }
//        return databaseAttributesx
//    }


}