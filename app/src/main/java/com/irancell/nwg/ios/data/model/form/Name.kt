package com.irancell.nwg.ios.data.model.form


data class Name(
    var persian: String? = null,
    var english: String? = null
){
    override fun toString(): String {
        return "Name(persian=$persian, english=$english)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Name

        if (persian != other.persian) return false
        if (english != other.english) return false

        return true
    }

    override fun hashCode(): Int {
        var result = persian?.hashCode() ?: 0
        result = 31 * result + (english?.hashCode() ?: 0)
        return result
    }

}
