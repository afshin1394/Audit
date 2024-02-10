package com.irancell.nwg.ios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.model.form.FormDomain
import com.irancell.nwg.ios.data.model.form.Name
import com.irancell.nwg.ios.data.model.form.Type
import java.util.UUID

@Entity
data class DatabaseAttributeProblematic constructor(
    @PrimaryKey(autoGenerate = true)
    val attrId: Int = 0,
    val siteId: Int,
    val parentGroupId: String = "-1",
    val groupId: String,
    val persianName: String?,
    val englishName: String?,
    val typeId: Int,
    val typeName: String,
    val index: Int,
    val childForm: String?,
    val active_form: Boolean = false,
    val json: String
) {
    override fun toString(): String {
        return "DatabaseAttributeProblematic(attrId=$attrId, siteId=$siteId, parentGroupId='$parentGroupId', groupId='$groupId', persianName=$persianName, englishName=$englishName,  typeId=$typeId, typeName='$typeName', index=$index, childForm=$childForm, active_form=$active_form, json='$json')"
    }
}
fun List<DatabaseAttributeProblematic>.asDatabaseAttributeSubmit() : List<DataBaseAttributesSubmit>
{
    return map {
        DataBaseAttributesSubmit( it.attrId,it.siteId,it.parentGroupId,it.groupId,"Problematic",it.persianName,it.englishName,it.typeId,it.typeName,it.index,it.childForm,it.active_form,it.json)
    }
}

fun List<DatabaseAttributeProblematic>.asDatabaseAttributes() : List<DatabaseAttributes>
{
    return map {
        DatabaseAttributes( it.attrId,it.siteId,it.parentGroupId,it.groupId,"Problematic",it.persianName,it.englishName,it.typeId,it.typeName,it.index,it.childForm,it.active_form,it.json)
    }
}

