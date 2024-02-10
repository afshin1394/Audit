package com.irancell.nwg.ios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.data.remote.response.server.Type

@Entity
data class DatabaseAttributes(
    @PrimaryKey(autoGenerate = true)
    val attrId: Int = 0,
    val siteId: Int,
    val parentGroupId: String = "-1",
    val groupId: String,
    val persianName: String?,
    val identifierName: String?,
    val englishName: String?,
    val typeId: Int,
    val typeName: String,
    val index: Int,
    val childForm: String?,
    val active_form: Boolean = false,
    val json: String){
    override fun toString(): String {
        return "DatabaseAttributes(attrId=$attrId, siteId=$siteId, parentGroupId='$parentGroupId', groupId='$groupId', persianName=$persianName, identifierName='$identifierName', englishName=$englishName, typeId=$typeId, typeName='$typeName', index=$index, childForm=$childForm, active_form=$active_form, json='$json')"
    }
}



fun List<DatabaseAttributes>.asRemoteModel() : List<Group>{
    return map {
        Group(
            id = it.groupId, type = Type(it.typeId,it.typeName), identifier_name = it.identifierName, active_form = it.active_form, index = it.index, name =  Name(it.persianName,it.englishName), subGroups = Gson().fromJson(it.json, Group::class.java).subGroups,
            child_form = it.childForm, extra = Extra(
                null,null)
        )
    }
}


fun List<DatabaseAttributes>.asDatabaseAttributeSubmit() : List<DataBaseAttributesSubmit>
{
    return map {
        DataBaseAttributesSubmit( it.attrId,it.siteId,it.parentGroupId,it.groupId,it.identifierName,it.persianName,it.englishName,it.typeId,it.typeName,it.index,it.childForm,it.active_form,it.json)
    }
}



