package com.irancell.nwg.ios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class DataBaseAttributesSubmit(@PrimaryKey(autoGenerate = true)
                                    val attrId: Int = 0,
                                    val siteId: Int,
                                    val parentGroupId: String = "-1",
                                    val groupId: String,
                                    val identifierName: String?,
                                    val persianName: String?,
                                    val englishName: String?,
                                    val typeId: Int,
                                    val typeName: String,
                                    val index: Int,
                                    val childForm: String?,
                                    val active_form: Boolean = false,
                                    val json: String)



