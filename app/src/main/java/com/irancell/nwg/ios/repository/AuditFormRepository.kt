package com.irancell.nwg.ios.repository

import AsyncResult
import android.util.Log
import com.google.gson.Gson

import com.irancell.nwg.ios.data.local.DatabaseAttributes
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.data.model.MandatoryField
import com.irancell.nwg.ios.data.model.TabModel
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.server.Type
import com.irancell.nwg.ios.database.dao.AuditFormDao
import com.irancell.nwg.ios.database.dao.SiteDao
import com.irancell.nwg.ios.network.get.AuditFormService
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.INNER
import com.irancell.nwg.ios.util.constants.MAIN
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import javax.inject.Inject

class AuditFormRepository @Inject constructor(
    private val gson: Gson,
    private val auditFormDao: AuditFormDao,
    private val auditFormService: AuditFormService,
    private val siteDao: SiteDao,
) {

    fun updateAuditForm(): Flow<AsyncResult<Unit>> {

        return apiCall(
            true,
            preProcess = {},
            networkApi = { auditFormService.getAuditForm() },
            postProcess = { auditFormRes ->
                val auditForm = auditFormRes.results[0].group
                auditFormDao.deleteAll()
                val sites: List<DatabaseSite> = siteDao.getAllSites()
                sites.forEach { databaseSite ->
                    auditForm.forEach { group ->
                        val json: String = gson.toJson(group)
                        auditFormDao.insertAttribute(
                            DatabaseAttributes(
                                siteId = databaseSite.siteId,
                                parentGroupId = "-1",
                                identifierName = group.identifier_name,
                                groupId = group.id,
                                persianName = group.name.persian,
                                englishName = group.name.english,
                                typeId = MAIN,
                                active_form = group.active_form,
                                index = group.index,
                                typeName = group.type.name,
                                json = json,
                                childForm = group.child_form
                            )
                        )
                    }
                }
            })
    }

    suspend fun insertAndUpdateAttribute(
        siteId: Int?,
        outerGroup: Group,
        innerGroups: ArrayList<Group>
    ) {
        withContext(Dispatchers.IO) {
            val databaseAttributesList = arrayListOf<DatabaseAttributes>()
            for (innerGroup in innerGroups) {
                val attrId = auditFormDao.getNextAutoId()
                val databaseAttributes = DatabaseAttributes(
                    attrId,
                    siteId!!,
                    outerGroup.id,
                    innerGroup.id,
                    innerGroup.name.persian,
                    innerGroup.identifier_name,
                    innerGroup.name.english,
                    INNER,
                    innerGroup.type.name,
                    innerGroup.index,
                    innerGroup.child_form,
                    innerGroup.active_form,
                    gson.toJson(innerGroup, Group::class.java),
                )
                databaseAttributesList.add(databaseAttributes)
            }

            auditFormDao.insertAllAndUpdateAttribute(
                siteId!!, outerGroup.id.toString(), gson.toJson(
                    outerGroup,
                    Group::class.java
                ), databaseAttributesList
            )

        }
    }

    suspend fun updateGroup(siteId: Int?, groupId: String, subGroup: ArrayList<SubGroup>) {
        withContext(Dispatchers.IO) {
            val json: String = auditFormDao.getAttribute(siteId!!, groupId)
            val group: Group = gson.fromJson(json, Group::class.java)
            group.subGroups = subGroup

            auditFormDao.updateAttribute(siteId, groupId, gson.toJson(group))

        }
    }


    fun getAttributeGroups(site: Int?, type: Int): Flow<List<TabModel>> =
        auditFormDao.getSiteGroups(site!!, type)


    fun getSiteAttributesByGroupId(site: Int?, groupId: String): Flow<Group> =
        auditFormDao.getSiteAttributesByGroupId(site!!, groupId).map {
            gson.fromJson(it.json, Group::class.java)
        }


    suspend fun deleteImage(
        siteId: Int,
        groupId: UUID,
        subGroup: SubGroup,
        attrElement: AttrElement
    ) {
        withContext(Dispatchers.IO) {
            val json: String = auditFormDao.getAttribute(siteId, groupId.toString())
            val group: Group = gson.fromJson(json, Group::class.java)
            val subGroupIndex = findIndex(group.subGroups, subGroup.id)
            val elementIndex =
                findIndex(group.subGroups.get(subGroupIndex).element!!, attrElement.id)

            val element = group.subGroups.get(subGroupIndex).element!![elementIndex]
            if (element.data != null)
                element.data = null

            auditFormDao.updateAttribute(siteId, groupId.toString(), gson.toJson(group))
        }
    }

    suspend fun removeAttributesWithChildren(siteId: Int, groupId: UUID, type: Int) {
        withContext(Dispatchers.IO) {
            auditFormDao.removeAttributesWithChildren(siteId, groupId.toString(), type)
        }
    }

    suspend fun removeAttributesWithChildrenAndUpdateAttr(
        siteId: Int,
        groupId: String,
        type: Int,
        group: Group
    ) {
        withContext(Dispatchers.IO) {
            val json: String = gson.toJson(group)
            auditFormDao.removeAttributesWithChildrenAndUpdateAttr(
                siteId,
                groupId,
                group.id,
                type,
                json
            )
        }
    }

    suspend fun removeAttributes(siteId: Int, groupId: ArrayList<Int>, type: Int) {
        withContext(Dispatchers.IO) {
            auditFormDao.removeAttributes(siteId, groupId, type)
        }
    }


    fun getAllAttributesBaseOnSite(siteId: Int): Flow<AsyncResult<List<MandatoryField>>> {
        val itemResponse: ArrayList<ItemResponse> = arrayListOf()
        val hashSet = HashSet<MandatoryField>()

        return dbTransaction({
            auditFormDao.getAllAttributesBaseOnSite(siteId)
        }, {


            it.forEach {
                itemResponse.add(gson.fromJson(it.json, ItemResponse::class.java))
            }
            checkListLevelItem(hashSet, itemResponse)
            hashSet.toList()
        }
        )
    }

    fun checkListLevelItem(hashSet: HashSet<MandatoryField>, responses: ArrayList<ItemResponse>) {
        responses.forEach { dynamicItem ->
            if (dynamicItem.type.id == Section && dynamicItem.mandatory)
                Log.i("dynamicType", "checkListLevelItem: name"+dynamicItem.name+"  mandatory:"+dynamicItem.mandatory)
            checkConditions(dynamicItem, hashSet)
            dynamicItem.items?.let { responses ->
                checkListLevelItem(hashSet, responses)
            }
        }
    }


    fun checkConditions(item: ItemResponse, hashSet: HashSet<MandatoryField>): MandatoryField? {
        if (item.mandatory) {
            println("* Item -> " + item.name.english + "''' data-> " + item.data + "*")

            when (item.type.id) {
                Image -> {
                    if (item.data?.Image == null || item.data?.Image?.length == 0) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))

                        println(" Image " + item.name.english)
                    }
                }
                Gallery -> {
                    if (item.data?.Image == null) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Gallery " + item.name.english)

                    }
                }
                Comment -> {
                    if ((item.data?.value == null) || (item.data?.value?.isEmpty() == true)) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Comment " + item.name.english)

                    }
                }
                MultiChoice -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" MultiChoice " + item.name.english)

                    }
                }
                SingleChoice -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" SingleChoice " + item.name.english)

                    }
                }
                Generator -> {
                    if (item.data?.value == null ||  item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Generator " + item.name.english)

                    }
                }
                Barcode -> {
                    if (item.data?.value == null || item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Barcode " + item.name.english)

                    }
                }
                SwitchGenerator -> {
                    println(" SwitchGenerator  data" + item.data)

                    if (item.data?.value == null || item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" SwitchGenerator " + item.name.english)

                    }
                }
                Picker -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Picker " + item.name.english)
                    }
                }
                Location -> {
                    if (item.data?.details == null) {
                        hashSet.add(MandatoryField(item.id, item.name, item.type))
                        println(" Location " + item.name.english)
                    }
                }
                else->{
                    hashSet.add(MandatoryField(item.id,item.name,item.type))
                }
            }
        }
        return null
    }


}


