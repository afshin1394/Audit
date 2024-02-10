package com.irancell.nwg.ios.repository

import AsyncResult
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.irancell.nwg.ios.data.local.DatabaseAttributeProblematic
import com.irancell.nwg.ios.data.local.DatabaseAttributes
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.data.model.MandatoryField
import com.irancell.nwg.ios.data.model.TabModel
import com.irancell.nwg.ios.data.model.form.FormDomain
import com.irancell.nwg.ios.data.model.form.Name
import com.irancell.nwg.ios.data.remote.response.AuditFormResponse
import com.irancell.nwg.ios.data.remote.response.audit.Group
import com.irancell.nwg.ios.data.remote.response.audit.SubGroup
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.database.dao.ProblematicFormDao
import com.irancell.nwg.ios.database.dao.SiteDao
import com.irancell.nwg.ios.network.get.ProblematicFormService
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.MAIN
import com.irancell.nwg.ios.util.constants.PROBLEMATIC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProblematicFormRepository @Inject constructor(
    private val problematicFormService: ProblematicFormService,
    private val problematicFormDao: ProblematicFormDao,
    private val isOnline: Boolean,
    private val siteDao: SiteDao,
    private val gson: Gson
) {

    fun fetchProblematicForm(): Flow<AsyncResult<AuditFormResponse>> {
        return apiCall(isOnline, {}, { problematicFormService.getProblematicForm() }, {
            problematicFormDao.deleteAll()
            val sites: List<DatabaseSite> = siteDao.getAllSites()
            val problematicGroups = it.results[0].group

            sites.forEach { databaseSite ->
                problematicGroups.forEach { problematicGroup ->
                    val json: String = gson.toJson(problematicGroup)
                    problematicFormDao.insertProblematicAttribute(
                        DatabaseAttributeProblematic(
                            siteId = databaseSite.siteId,
                            parentGroupId = "-1",
                            groupId = problematicGroup.id,
                            persianName = problematicGroup.name.persian,
                            englishName = problematicGroup.name.english,
                            typeId = PROBLEMATIC,
                            active_form = problematicGroup.active_form,
                            index = problematicGroup.index,
                            typeName = problematicGroup.type.name,
                            json = json,
                            childForm = problematicGroup.child_form
                        )
                    )
                }
            }
            it
        })
    }

    fun getAttributeGroups(site: Int?, type: Int): Flow<List<TabModel>> =
        problematicFormDao.getSiteGroups(site!!, type)


    fun getSiteAttributesByGroupId(site: Int?, groupId: String): Flow<Group> =
        problematicFormDao.getSiteAttributesByGroupId(site!!, groupId).map {
            gson.fromJson(it.json, Group::class.java)
        }

    suspend fun updateGroup(siteId: Int?, groupId: String, subGroup: ArrayList<SubGroup>) {
        withContext(Dispatchers.IO) {
            val json: String = problematicFormDao.getAttribute(siteId!!, groupId)
            val group: Group = gson.fromJson(json, Group::class.java)
            group.subGroups = subGroup

            problematicFormDao.updateAttribute(siteId, groupId, gson.toJson(group))

        }
    }

    fun getAllAttributesBaseOnSite(siteId: Int): Flow<AsyncResult<List<MandatoryField>>> {
        val itemResponse: ArrayList<ItemResponse> = arrayListOf()
        val hashSet = HashSet<MandatoryField>()

        return dbTransaction({
            problematicFormDao.getAllAttributesBaseOnSite(siteId)
        }, {
            it.forEach {
                itemResponse.add(gson.fromJson(it.json, ItemResponse::class.java))
            }
            checkListLevelItem(hashSet,itemResponse)
            hashSet.toList()
        }
        )
    }
    fun checkListLevelItem(hashSet: HashSet<MandatoryField>, responses: ArrayList<ItemResponse>) {
        responses.forEach {dynamicItem->
            checkConditions(dynamicItem,hashSet)
            dynamicItem.items?.let { responses ->
                checkListLevelItem(hashSet,responses)
            }

        }
    }


    fun checkConditions(item: ItemResponse,hashSet: HashSet<MandatoryField>): MandatoryField? {
        if (item.mandatory) {
            println("* Item -> " + item.name.english + "''' data-> " + item.data + "*")

            when (item.type.id) {
                Image -> {
                    if (item.data?.Image == null || item.data?.Image?.length == 0) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))

                        println(" Image " + item.name.english)
                    }
                }
                Gallery -> {
                    if (item.data?.Image == null) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" Gallery " + item.name.english)

                    }
                }
                Comment -> {
                    if ((item.data?.value == null) || (item.data?.value?.isEmpty() == true)) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" Comment " + item.name.english)

                    }
                }
                MultiChoice -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" MultiChoice " + item.name.english)

                    }
                }
                SingleChoice -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" SingleChoice " + item.name.english)

                    }
                }
                Generator -> {
                    if (item.data?.value == null || item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" Generator " + item.name.english)
                    }
                }
                Barcode -> {
                    if (item.data?.value == null || item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" Barcode " + item.name.english)

                    }
                }
                SwitchGenerator -> {
                    println(" SwitchGenerator  data" + item.data   )

                    if (item.data?.value == null || item.data?.value == "0") {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" SwitchGenerator " + item.name.english)

                    }
                }
                Picker -> {
                    if (item.data?.options == null || item.data?.options?.size == 0) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
                        println(" Picker " + item.name.english)
                    }
                }
                Location -> {
                    if (item.data?.details == null) {
                        hashSet.add(MandatoryField(item.id,item.name,item.type))
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

