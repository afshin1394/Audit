package com.irancell.nwg.ios.util.factory

import android.util.Log
import com.google.gson.Gson
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.SubGroup
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.generator.asSubGroup
import com.irancell.nwg.ios.data.remote.response.generator.asSubGroups
import com.irancell.nwg.ios.repository.GenerateAttributeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class AuditSubGroup(private val gson: Gson, private val generateAttributeRepository: GenerateAttributeRepository) : AuditObject<SubGroup> {
    override suspend fun create(attrElement: AttrElement): Flow<ArrayList<SubGroup>> {
        return generateAttributeRepository.getByChildForm(attrElement.child_form!!).map {
            return@map arrayListOf( gson.fromJson(it, ItemResponse::class.java).asSubGroup())
        }
    }
}