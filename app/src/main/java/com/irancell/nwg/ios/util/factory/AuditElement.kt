package com.irancell.nwg.ios.util.factory

import com.google.gson.Gson
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.generator.asAttrElement
import com.irancell.nwg.ios.data.remote.response.generator.asAttrElements
import com.irancell.nwg.ios.repository.GenerateAttributeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuditElement(
    private val gson: Gson,
    private val generateAttributeRepository: GenerateAttributeRepository
) : AuditObject<AttrElement> {
    override suspend fun create(attrElement: AttrElement): Flow<ArrayList<AttrElement>> {
        return withContext(Dispatchers.Default) {
            generateAttributeRepository.getByChildForm(attrElement.child_form!!).map {
                return@map ArrayList(gson.fromJson(it, ItemResponse::class.java).items?.asAttrElements())
            }
        }
    }

}