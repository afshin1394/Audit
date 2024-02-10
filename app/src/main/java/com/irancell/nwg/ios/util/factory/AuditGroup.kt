package com.irancell.nwg.ios.util.factory

import com.google.gson.Gson
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.Group
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.generator.asGroup
import com.irancell.nwg.ios.repository.GenerateAttributeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuditGroup(private val gson: Gson,private val generateAttributeRepository: GenerateAttributeRepository) : AuditObject<Group> {
    override suspend fun create(attrElement: AttrElement): Flow<ArrayList<Group>> {
      return  withContext(Dispatchers.Default) {
             generateAttributeRepository.getByChildForm(attrElement.child_form!!).map{
                 return@map arrayListOf(gson.fromJson(it,ItemResponse::class.java).asGroup())
             }
        }
    }
}