package com.irancell.nwg.ios.data.model.form

import com.irancell.nwg.ios.data.remote.response.audit.BaseItem


data class FormDomain(

    override var id: String,

    var name: Name,

    var type: Type,

    var index: Int,

    var mandatory : Boolean,

    var identifierName: String = "",

    var active_form : Boolean,

    var generator_type : GeneratorType?,

    var child_form : String?,

    var extra : Extra?,

    var data : Data? = null,

    var items : ArrayList<FormDomain>,

    ) : BaseItem(){

    override fun toString(): String {
        return "FormDomain(id='$id', name=$name, type=$type, index=$index, mandatory=$mandatory, identifierName='$identifierName', active_form=$active_form, generator_type=$generator_type, child_form=$child_form, extra=$extra, data=$data, items=$items)"
    }
}

