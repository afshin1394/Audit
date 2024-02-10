package com.irancell.nwg.ios.util.factory

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.Data
import com.irancell.nwg.ios.data.remote.response.audit.Detail
import com.irancell.nwg.ios.data.remote.response.audit.SubGroup
import com.irancell.nwg.ios.repository.AuditFormRepository
import com.irancell.nwg.ios.repository.GenerateAttributeRepository
import com.irancell.nwg.ios.util.Generator
import com.irancell.nwg.ios.util.constants.*
import com.irancell.nwg.ios.util.findIndex
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.collections.ArrayList

class AuditFactory(
    val siteId: Int,
    val gson: Gson,
    val auditFormRepository: AuditFormRepository,
    val generateAttributeRepository: GenerateAttributeRepository,
    val local : String
) {


    suspend fun build(attrElement: AttrElement, subGroup: SubGroup, outerGroupId: String) {

        auditFormRepository.getSiteAttributesByGroupId(siteId, outerGroupId).collect { outerGroup ->
            val subGroupIndex = findIndex(outerGroup.subGroups, subGroup.id)
            val attrElementIndex = findIndex(subGroup.element!!, attrElement.id)

            when (attrElement.generator_type!!.id) {
                GenerateForm -> {
                    AuditGroup(gson, generateAttributeRepository).create(attrElement)
                        .collect { innerGroups ->
                            attrElement.group?.let {
                                for (innerGroup in innerGroups) {

                                    Log.i("track", "build: it:" + it)

                                    innerGroup.id =
                                        UUID.randomUUID().toString()

                                    innerGroup.name.persian += (it.size + 1).toString()
                                    innerGroup.name.english += (it.size + 1).toString()




                                    /**set generator type subGroup names**/
                                    if (innerGroup.subGroups.all { it.element!!.all { it.generator_type!!.id == GenerateForm } })
                                        innerGroup.subGroups.forEach {
                                            it.name = innerGroup.name
                                        }
                                    Log.i("track", "build: innerGroup.id" + innerGroup.id)

                                    it.add(innerGroup)
                                    attrElement.data?.let {data->
                                        data.value =
                                            (data.value.toString()
                                                .toInt() + 1).toString()
                                    }
                                    outerGroup.subGroups[subGroupIndex].element?.set(attrElementIndex,
                                        attrElement
                                    )
                                }
                                auditFormRepository.insertAndUpdateAttribute(
                                    siteId,
                                    outerGroup,
                                    innerGroups
                                )
                                return@collect
                            }
                            for (innerGroup in innerGroups) {
                                innerGroup.id = UUID.randomUUID().toString()
                                innerGroup.name.persian += "1"
                                innerGroup.name.english += "1"



                                /**set generator type subGroup names**/
                                if (innerGroup.subGroups.all { it.element!!.all { it.generator_type!!.id == GenerateForm } })
                                    innerGroup.subGroups.forEach {
                                        it.name = innerGroup.name
                                    }

                                attrElement.group = arrayListOf(innerGroup)
                                attrElement.data?.let {data->
                                    data.value =
                                        (data.value.toString().toInt() + 1).toString()
                                }


                                outerGroup.subGroups[subGroupIndex].element?.set(attrElementIndex,
                                    attrElement
                                )
                            }
                            auditFormRepository.insertAndUpdateAttribute(
                                siteId,
                                outerGroup,
                                innerGroups
                            )


                            return@collect
                        }

                }
                GenerateFormSection -> {
                    AuditSubGroup(gson, generateAttributeRepository).create(attrElement)
                        .collect { generatedSubGroups ->
                            var categoryId = System.currentTimeMillis()
                            for (generatedSubGroup in generatedSubGroups) {
                                generatedSubGroup.isGenerated = true
                                generatedSubGroup.parent_id = attrElement.child_form
                                generatedSubGroup.categoryId = categoryId
                                generatedSubGroup.id = UUID.randomUUID().toString()
                                if ( attrElement.data == null)
                                {
                                  val data =  Data(Detail(null,null,null) )
                                  data.value = "1"
                                  attrElement.data = data
                                }
                                attrElement.data?.let {data->

                                    data.value?.let {
                                        if (attrElement.type.id == Generator) {
                                            data.value =
                                                (data.value.toString()
                                                    .toInt() + 1).toString()
                                            data.value?.let { value ->
                                                generatedSubGroup.name.english += value
                                                generatedSubGroup.name.persian += value

                                            }
                                        } else {
                                            data.value = "1"
                                        }

                                    }
                                }

                                val generatedSubGroups =
                                    outerGroup.subGroups.filter { it.isGenerated && it.parent_id == attrElement.child_form } as ArrayList<SubGroup>

                                var addingPosition = -1
                                if (generatedSubGroups.size > 0) {
                                    addingPosition = findIndex(
                                        outerGroup.subGroups,
                                        generatedSubGroups[generatedSubGroups.lastIndex].id
                                    ) + 1
                                } else {
                                    addingPosition = subGroupIndex + 1
                                }
                                Log.i("addingPosition", "addingPosition: " + addingPosition)
                                outerGroup.subGroups.add(addingPosition, generatedSubGroup)
                                outerGroup.subGroups[subGroupIndex].element?.set(attrElementIndex,
                                    attrElement
                                )

                            }

                            auditFormRepository.updateGroup(
                                siteId,
                                outerGroup.id,
                                outerGroup.subGroups
                            )
                            return@collect
                        }

                }
                GenerateFormItem -> {
                    AuditElement(gson, generateAttributeRepository).create(attrElement).collect {
                        val categoryId = System.currentTimeMillis()
                        for (element in it)
                        {
                            val generatedElements =
                                subGroup.element!!.filter { it.isGenerated && it.parentId == attrElement.child_form } as ArrayList<AttrElement>

                            var addingPosition = -1
                            if (generatedElements.size > 0) {
                                addingPosition = findIndex(
                                    subGroup.element!!,
                                    generatedElements[generatedElements.lastIndex].id
                                ) + 1
                            } else {
                                addingPosition = attrElementIndex + 1
                            }
                            element.categoryId = categoryId
                            element.id = UUID.randomUUID().toString()
                            element.isGenerated = true
                            element.parentId = attrElement.child_form
                            subGroup.element!!.add(addingPosition, element)
                            outerGroup.subGroups[subGroupIndex] = subGroup
                        }
                        auditFormRepository.updateGroup(siteId, outerGroup.id, outerGroup.subGroups)
                        return@collect

                    }
                }

            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun remove(attrElement: AttrElement, subGroup: SubGroup, outerGroupId: String) {
        val outerGroup =
            auditFormRepository.getSiteAttributesByGroupId(siteId, outerGroupId).first()
        val elementIndex = findIndex(subGroup.element, attrElement.id)
        val subGroupIndex = findIndex(outerGroup.subGroups, subGroup.id)
//            .collect { outerGroup ->
        Log.i("outerEmition", "remove: " + outerGroup)
        when (attrElement.generator_type!!.id) {
            RemoveForm -> {
                attrElement.group?.let {
                    if (it.size > 0) {
                        var innerGroupId = it[it.size - 1].id
                        attrElement.data?.let { data ->
                            if (data.value.toString().toInt() > 0)
                                data.value =
                                    (data.value.toString()
                                        .toInt() - 1).toString()




                            it.removeAt(it.size - 1)

                            outerGroup.subGroups[subGroupIndex].element?.get(elementIndex)!!.group = it

                            outerGroup.subGroups[subGroupIndex].element?.get(elementIndex)!!.data?.value =
                                data.value
                            auditFormRepository.removeAttributesWithChildrenAndUpdateAttr(
                                siteId, innerGroupId,
                                INNER, outerGroup
                            )
                        }

                    }

                }

            }

            RemoveFormSection -> {
                val generatedSubGroups =
                    outerGroup.subGroups.filter { it.isGenerated && it.parent_id == attrElement.child_form } as ArrayList<SubGroup>
                if (generatedSubGroups.isNotEmpty()) {
                    attrElement.data?.value?.let {value->
                        if (attrElement.type.id  == Generator) {
                            if (value.toInt() > 0)
                                attrElement.data?.value =
                                    (value
                                        .toInt() - 1).toString()

                        } else {
                            attrElement.data?.value = "0"
                        }
                        outerGroup.subGroups[subGroupIndex].element?.set(elementIndex, attrElement)
                    }

                   val categoryId =  generatedSubGroups[generatedSubGroups.lastIndex].categoryId
                   for (generatedSubGroup in generatedSubGroups){
                       if (categoryId == generatedSubGroup.categoryId)
                           outerGroup.subGroups.removeAt(
                               findIndex(
                                   outerGroup.subGroups,
                                   generatedSubGroup.id
                               )
                           )
                   }

                    auditFormRepository.updateGroup(siteId, outerGroupId, outerGroup.subGroups)
                }

            }
            RemoveFormItem -> {

                var generatedElements =
                    subGroup.element!!.filter { it.isGenerated && it.parentId == attrElement.child_form  } as ArrayList<AttrElement>
                if (generatedElements.isNotEmpty()) {
                    var categoryId =  generatedElements[generatedElements.lastIndex].categoryId
                    for (generatedElement in generatedElements) {
                        if (generatedElement.categoryId == categoryId) {
                            subGroup.element?.removeAt(
                                findIndex(
                                    subGroup.element,
                                    generatedElements[generatedElements.lastIndex].id
                                )
                            )
                            generatedElements = subGroup.element!!.filter { it.isGenerated && it.parentId == attrElement.child_form  } as ArrayList<AttrElement>
                        }
                    }
                    outerGroup.subGroups[subGroupIndex] = subGroup
                    auditFormRepository.updateGroup(siteId, outerGroupId, outerGroup.subGroups)


                }
            }
        }
    }
}