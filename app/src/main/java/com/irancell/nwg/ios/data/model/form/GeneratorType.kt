package com.irancell.nwg.ios.data.model.form


data class GeneratorType(
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return "GeneratorType(id=$id, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneratorType

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        return result
    }

}
