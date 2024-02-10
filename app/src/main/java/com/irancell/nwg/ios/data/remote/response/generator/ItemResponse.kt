package com.irancell.nwg.ios.data.remote.response.generator

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.DatabaseGenerateAttributes
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.data.remote.response.server.Type
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ItemResponse(
    @Expose
    @SerializedName("uuid")
    override var id: String,
    @Expose
    @SerializedName("name")
    var name: Name,
    @Expose
    @SerializedName("type")
    var type: Type,
    @Expose
    @SerializedName("index")
    var index: Int,
    @Expose
    @SerializedName("mandatory")
    var mandatory : Boolean,
//    @Expose
//    @SerializedName("identifier_name")  var identifierName: String = "",
    @Expose
    @SerializedName("active_form")
    var active_form : Boolean,
    @Expose
    @SerializedName("generator_type")
    var generator_type : GeneratorType?,
    @Expose
    @SerializedName("child_form")
    var child_form : String?,
    @Expose
    @SerializedName("extra")
    var extra : Extra?,
    @Expose
    @SerializedName("data")
    var data : Data? = null,
    @Expose
    @SerializedName("items")
    var items : ArrayList<ItemResponse>?,

    ) : BaseItem(), Parcelable

fun List<ItemResponse>.asDatabaseModel() : List<DatabaseGenerateAttributes>{
    return map{
        DatabaseGenerateAttributes(it.id,it.name.english, Gson().toJson(it))
    }

}




/**MAPPERS*/

fun List<ItemResponse>.asSubGroups() : List<SubGroup>{
    return map {
        SubGroup(it.id,it.name,"",it.type,it.index,it.active_form,it.generator_type,it.child_form,it.extra,it.data,ArrayList(it.items?.asAttrElements()))
    }
}

fun List<ItemResponse>.asAttrElements() : List<AttrElement>{
    return map{
        AttrElement(UUID.randomUUID().toString(),it.name,it.type,"",it.mandatory,it.index,it.active_form,it.generator_type,it.child_form,it.extra,it.data)
    }
}
fun ItemResponse.asGroup() : Group{
    return Group(this.id,this.name,"",this.type,this.index,this.active_form,this.generator_type,this.child_form,this.extra,this.data,ArrayList(this.items?.asSubGroups()))
}
fun ItemResponse.asSubGroup() : SubGroup{
    return SubGroup(this.id,this.name,"",this.type,this.index,this.active_form,this.generator_type,this.child_form,this.extra,this.data,ArrayList(this.items?.asAttrElements()))
}
fun ItemResponse.asAttrElement() : AttrElement{
       return AttrElement(this.id,this.name,this.type,"",this.mandatory,this.index,this.active_form,this.generator_type,this.child_form,this.extra,this.data)
}