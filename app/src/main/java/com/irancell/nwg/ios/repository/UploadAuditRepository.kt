package com.irancell.nwg.ios.repository

import AsyncResult
import android.content.Context
import android.graphics.drawable.LevelListDrawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.work.*

import com.google.gson.Gson
import com.irancell.nwg.ios.data.local.*
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.request.UploadAuditRequest
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.FormValue
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import com.irancell.nwg.ios.data.remote.response.audit.Group
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.database.dao.AuditFormDao
import com.irancell.nwg.ios.database.dao.ProblematicFormDao
import com.irancell.nwg.ios.database.dao.SubmitAttributeDao
import com.irancell.nwg.ios.network.patch.SubmitAuditService
import com.irancell.nwg.ios.network.patch.SubmitProblematicService
import com.irancell.nwg.ios.network.patch.UploadAuditService
import com.irancell.nwg.ios.service.FileUploadWorker
import com.irancell.nwg.ios.util.apiCall

import com.irancell.nwg.ios.util.constants.MAIN
import com.irancell.nwg.ios.util.constants.PROBLEMATIC
import com.irancell.nwg.ios.util.constants.Problematic
import com.irancell.nwg.ios.util.constants.auditZipGroupFolder
import com.irancell.nwg.ios.util.dbTransaction
import com.irancell.nwg.ios.util.zipDirectoryWithSubfolders
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UploadAuditRepository @Inject constructor(
    private val uploadAuditService: UploadAuditService,
    private val auditFormDao: AuditFormDao,
    private val submitAuditService: SubmitAuditService,
    private val submitProblematicService: SubmitProblematicService,
    private val submitAuditDao: SubmitAttributeDao,
    private val problematicFormDao: ProblematicFormDao,
    private val gson: Gson,
    private val isOnline: Boolean,
    @ApplicationContext private val appContext: Context

) {


    fun uploadAudit(
        fileDirPath: String,
        siteDomain: SiteDomain,
        type: Int
    ): LiveData<WorkInfo> {


        val uploadWork = OneTimeWorkRequestBuilder<FileUploadWorker>().setInputData(
            workDataOf(
                FileUploadWorker.FileDirPath to fileDirPath,
                FileUploadWorker.SiteName to siteDomain.site_name,
                FileUploadWorker.RegionName to siteDomain.region_name,
                FileUploadWorker.AuditId to siteDomain.audit_id,
                FileUploadWorker.Type to type
            )
        )
            .setBackoffCriteria(
                 BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setId(UUID.randomUUID())
            .build()
        val workManager: WorkManager = WorkManager.getInstance(appContext)
        workManager.enqueueUniqueWork("Upload-Files", ExistingWorkPolicy.REPLACE, uploadWork)
        return workManager.getWorkInfoByIdLiveData(uploadWork.id)


    }

    data class LevelAttribute(val level: Int, val attributes: ArrayList<DatabaseAttributes>)


    fun getParentGroupIDs(siteId: Int): List<String> {
        return auditFormDao.getParentGroupIds(siteId)
    }

    private fun getFirstLevelAttr(
        parentId: String,
        listOfAttributes: ArrayList<DatabaseAttributes>
    ): LevelAttribute {
        val levelAttribute = LevelAttribute(1, arrayListOf())

        for (listOfAttribute in listOfAttributes) {
            if (listOfAttribute.parentGroupId == "-1") {
                levelAttribute.attributes.add(listOfAttribute)
            }

        }
        return levelAttribute
    }

    fun getSecondLevelAttributes(
        firstLevelAttributeInit: LevelAttribute,
        listOfAttributes: ArrayList<DatabaseAttributes>
    ): LevelAttribute {
        val levelAttribute = LevelAttribute(2, arrayListOf())
        for (attribute in firstLevelAttributeInit.attributes) {
            for (listOfAttribute in listOfAttributes) {
                if (listOfAttribute.parentGroupId == attribute.groupId) {
                    levelAttribute.attributes.add(listOfAttribute)
                }

            }
        }

        return levelAttribute
    }


    fun getThirdLevelAttributes(
        secondLevelAttributeInit: LevelAttribute,
        listOfAttributes: ArrayList<DatabaseAttributes>
    ): LevelAttribute {
        val levelAttribute = LevelAttribute(3, arrayListOf())
        for (attribute in secondLevelAttributeInit.attributes) {
            for (listOfAttribute in listOfAttributes) {
                if (listOfAttribute.parentGroupId == attribute.groupId) {
                    levelAttribute.attributes.add(listOfAttribute)
                }

            }
        }

        return levelAttribute
    }


    private fun getVerityLevels(levelListAttributes: ArrayList<LevelAttribute>): Int {
        var v = 0
        var level = -1
        for (levelListAttribute in levelListAttributes) {
            if (levelListAttribute.level != level) {
                level = levelListAttribute.level
                v++
            }
        }
        return v
    }

    fun getLevels(levelListAttributes: ArrayList<LevelAttribute>): List<Int> {
        val levels = arrayListOf<Int>()
        for (levelListAttribute in levelListAttributes) {
            if (!levels.contains(levelListAttribute.level)) {
                levels.add(levelListAttribute.level)
            }
        }
        levels.sortDescending()
        return levels
    }


//    fun getAttributesOfLevel(level : Int,levelListAttributes: ArrayList<LevelAttribute>) : List<DatabaseAttributes>{
//        val levelList : ArrayList<DatabaseAttributes> = arrayListOf()
//        for (levelListAttribute in levelListAttributes) {
//            if (levelListAttribute.level == level)
//                levelList.add(levelListAttribute.attributes)
//
//        }
//        return levelList
//    }

    fun findParent(siteId: Int, databaseAttributes: DatabaseAttributes): DatabaseAttributes {
        return submitAuditDao.getAttributeByParentId(siteId, databaseAttributes.parentGroupId)
    }


    fun getChildPositionInParentAndAddForAllLevel(
        siteId: Int,
        attributeOFLevel: List<DatabaseAttributes>,
        attrParent: DatabaseAttributes
    ) {
        for (databaseAttributes in attributeOFLevel) {
            val tree = getChildPositionInParent(siteId, databaseAttributes, attrParent)
            tree?.let { it ->
//                updateParentAttr(siteId, attrParent, databaseAttributes, it)
            }
        }
    }


    data class Tree(var i: Int, var j: Int, var z: Int) {
        override fun toString(): String {
            return "Tree(i=$i, j=$j, z=$z)"
        }
    }

    fun getChildPositionInParent(
        siteId: Int,
        attrChild: DatabaseAttributes,
        attrParent: DatabaseAttributes
    ): Group? {


        val groupParent = gson.fromJson(attrParent.json, Group::class.java)

        for (i in 0 until groupParent.subGroups.size) {
            groupParent.subGroups[i].element?.let { attrElements ->
                for (j in 0 until attrElements.size) {
                    attrElements[j].group?.let { innerGroups ->
                        for (z in 0 until innerGroups.size) {
                            if (attrChild.groupId == innerGroups[z].id) {
                                innerGroups[z] = gson.fromJson(attrChild.json, Group::class.java)
                                return groupParent
                            }
                        }
                    }
                }
            }

        }
        return null
    }

    fun updateParentAttr(
        siteId: Int,
        attrParent: DatabaseAttributes,
        attrChild: DatabaseAttributes,
        tree: Tree
    ) {
        val groupParent = gson.fromJson(attrParent.json, Group::class.java)
        val groupChild = gson.fromJson(attrChild.json, Group::class.java)

        groupParent.subGroups[tree.i].element?.get(tree.j)?.group?.get(tree.z)?.subGroups =
            groupChild.subGroups

        Log.i("Group", "updateParentAttr: " + gson.toJson(groupParent))
    }

    fun getAllSiteGroups(siteId: Int, formType: Int): Flow<AsyncResult<SubmitAuditRequest>> {

        return when (formType) {
            MAIN -> {
                getFromAudit(siteId)
            }

            PROBLEMATIC -> {
                getFromProblematic(siteId)

            }
            else -> {
                getFromAudit(siteId)
            }
        }

    }

    private fun getFromProblematic(siteId: Int): Flow<AsyncResult<SubmitAuditRequest>> {
        return dbTransaction({
            submitAuditDao.deleteAll()
            problematicFormDao.getAllAttributesBaseOnSite(siteId)
        }, { databaseAttributes ->

            submitAuditDao.insertAllAttribute(databaseAttributes.asDatabaseAttributeSubmit())
            val firstLevelAttribute =
                getFirstLevelAttr("-1", ArrayList(databaseAttributes.asDatabaseAttributes()))
            val secondLevelAttribute =
                getSecondLevelAttributes(
                    firstLevelAttribute,
                    ArrayList(databaseAttributes.asDatabaseAttributes())
                )
            val thirdLevelAttribute =
                getThirdLevelAttributes(
                    secondLevelAttribute,
                    ArrayList(databaseAttributes.asDatabaseAttributes())
                )
            for (attribute in thirdLevelAttribute.attributes) {

                val parentAttr = findParent(siteId, attribute)

                val tree = getChildPositionInParent(siteId, attribute, parentAttr)
                submitAuditDao.updateAttribute(siteId, parentAttr.groupId, gson.toJson(tree))
            }
            val attributes = submitAuditDao.getAllAttributesBaseOnSite(siteId)
            val secondLevelAttributeNew =
                getSecondLevelAttributes(firstLevelAttribute, ArrayList(attributes))

            for (attribute in secondLevelAttributeNew.attributes) {
                val parentAttr = findParent(siteId, attribute)

                val tree = getChildPositionInParent(siteId, attribute, parentAttr)
                submitAuditDao.updateAttribute(siteId, parentAttr.groupId, gson.toJson(tree))
            }


            val databaseAttributesMain: List<DatabaseAttributes> =
                submitAuditDao.getSiteMainForms(siteId)

            val groups = databaseAttributesMain.asRemoteModel()
            val formValues = arrayListOf<FormValue>()
            groups.forEach {
                formValues.add(FormValue(it.id, it))
            }
            SubmitAuditRequest(formValues)
        })
    }


    private fun getFromAudit(siteId: Int): Flow<AsyncResult<SubmitAuditRequest>> {
        return dbTransaction({
            submitAuditDao.deleteAll()
            auditFormDao.getAllAttributesBaseOnSite(siteId)
        }, { databaseAttributes ->
            submitAuditDao.insertAllAttribute(databaseAttributes.asDatabaseAttributeSubmit())
            val firstLevelAttribute = getFirstLevelAttr("-1", ArrayList(databaseAttributes))
            val secondLevelAttribute =
                getSecondLevelAttributes(firstLevelAttribute, ArrayList(databaseAttributes))
            val thirdLevelAttribute =
                getThirdLevelAttributes(secondLevelAttribute, ArrayList(databaseAttributes))
            for (attribute in thirdLevelAttribute.attributes) {

                val parentAttr = findParent(siteId, attribute)

                val tree = getChildPositionInParent(siteId, attribute, parentAttr)
                submitAuditDao.updateAttribute(siteId, parentAttr.groupId, gson.toJson(tree))
            }
            val attributes = submitAuditDao.getAllAttributesBaseOnSite(siteId)
            val secondLevelAttributeNew =
                getSecondLevelAttributes(firstLevelAttribute, ArrayList(attributes))

            for (attribute in secondLevelAttributeNew.attributes) {
                val parentAttr = findParent(siteId, attribute)

                val tree = getChildPositionInParent(siteId, attribute, parentAttr)
                submitAuditDao.updateAttribute(siteId, parentAttr.groupId, gson.toJson(tree))
            }


            val databaseAttributesMain: List<DatabaseAttributes> =
                submitAuditDao.getSiteMainForms(siteId)

            val groups = databaseAttributesMain.asRemoteModel()
            val formValues = arrayListOf<FormValue>()
            groups.forEach {
                formValues.add(FormValue(it.id, it))
            }
            SubmitAuditRequest(formValues)
        })
    }


    fun submitAudit(audit_id: Int, body: SubmitAuditRequest, type: Int): Flow<AsyncResult<Unit>> {
        return when (type) {
            MAIN -> {
                apiCall(isOnline, {}, { submitAuditService.patchSubmitAudit(audit_id, body) }, {})
            }
            PROBLEMATIC -> {
                apiCall(
                    isOnline,
                    {},
                    { submitProblematicService.patchSubmitProblematicAudit(audit_id, body) },
                    {})
            }
            else -> {
                apiCall(isOnline, {}, { submitAuditService.patchSubmitAudit(audit_id, body) }, {})
            }
        }
    }


}