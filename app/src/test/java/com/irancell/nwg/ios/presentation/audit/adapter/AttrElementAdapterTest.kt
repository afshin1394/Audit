package com.irancell.nwg.ios.presentation.audit.adapter

import androidx.navigation.navOptions
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.data.remote.response.server.Type
import com.irancell.nwg.ios.presentation.audit.adapter.AttrElementAdapter.ViewHolderChoiceSelect
import com.irancell.nwg.ios.presentation.audit.adapter.AttrElementAdapter.ViewHolderPicker
import com.irancell.nwg.ios.util.DataState
import com.irancell.nwg.ios.util.Picker
import com.irancell.nwg.ios.util.SingleChoice
import com.irancell.nwg.ios.util.findIndex
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.util.UUID

data class OptionModel(val extraOptions: Option, var dataOption: Option?)
class AttrElementAdapterTest {
    lateinit var attrElement: AttrElement
    lateinit var extra: Extra
    lateinit var data: Data
    lateinit var optionModels: ArrayList<OptionModel>

    @Before
    fun setUp() {


        val option1 = Option("1", "0.0", 0.0)
        val option2 = Option("2", "1.0", 0.0)
        val option3 = Option("3", "2.0", 0.0)
        val option4 = Option("4", "3.0", 0.0)
        val option5 = Option("5", "4.0", 0.0)
        val option6 = Option("6", "5.0", 0.0)


        val options = arrayListOf(option1, option2, option3, option4, option5, option6)
        val optionsData = arrayListOf(Option("1", "0.0", 1.0), Option("5", "4.0", 0.0))
        extra = Extra(null, options)
        data = Data(details = null)
//        data.options = optionsData
        optionModels = arrayListOf<OptionModel>()
//        extra.options?.forEach {
//            if (data.options?.contains(it) == true)
//            optionModels.add(OptionModel(it,Option(it.id,it.name,1.0)))
//            else
//                optionModels.add(OptionModel(it,null))
//
//        }

        attrElement = AttrElement(
            UUID.randomUUID().toString(),
            Name("تست", "test"),
            Type(Picker, "Picker"),
            "",
            false,
            1,
            false,
            null,
            null,
            extra,
            data
        )
    }

    /**
     *  [ViewHolderPicker.checkSelectPicker].
     */
    @Test
    fun checkSelectPicker() {
        val extraOptions = extra.options
        if (attrElement.data?.options == null) {
            attrElement.data?.options = arrayListOf()
        }
        attrElement.data?.options?.clear()
        extraOptions?.let {
            for (option in extraOptions) {
                if (option.name == "4.0") {
                    val tempOpt = option
                    tempOpt.value = 1.0
                    attrElement.data?.options?.add(tempOpt)
                }
            }
        }


        println(attrElement.data?.options)
        assertTrue(
            attrElement.data?.options!!.contains(
                Option(
                    "5",
                    "4.0",
                    1.0
                )
            ) && attrElement.data?.options!!.size == 1
        )
        println(attrElement.data?.options)
    }

    /**
     *  [ViewHolderPicker.checkSelectPicker].
     */
    @Test
    fun checkSingleSelect() {
        val index = findOptionIndexBaseOnExtra()
        if (data.options == null) {
            data.options = arrayListOf()
        }
        if (optionModels[index].dataOption == null) {
            data.options?.clear()
            optionModels[index].extraOptions.value = 1.0
            data.options?.add(optionModels[index].extraOptions)
            optionModels.get(index).dataOption = optionModels[index].extraOptions

        } else {
            data.options?.clear()
            optionModels.forEach { it.dataOption = null }
        }

        println(optionModels)
        assertTrue(data.options!!.all { it.value == 1.0 } || data.options!!.size == 0)
    }


    /**
     *  [ViewHolderChoiceSelect.checkMultiSelect].
     */
    @Test
    fun checkMultiSelect() {

        val index = findOptionIndexBaseOnExtra()
        if (optionModels[index].dataOption != null) {
            data.options?.remove(optionModels[index].dataOption)
            optionModels[index].dataOption = null
        } else {
            if (data.options == null) {
                data.options = arrayListOf()
            }
            optionModels[index].extraOptions.value = 1.0
            data.options?.add(optionModels[index].extraOptions)
            optionModels[index].dataOption = optionModels[index].extraOptions
        }
        println(optionModels)
        data.options?.all { it.value == 1.0 }?.let {
            assertTrue(
                it
            )
        }
    }

    @Test
    fun checkIsChecked() {
        extra.options?.forEach {
            if (data.options?.contains(it) == true)
                it.value = 1.0
        }

        println(data.options)
    }

    @Test
    fun createOptionModelList() {
        var optionModels = arrayListOf<OptionModel>()
        extra.options?.forEach {
            optionModels.add(OptionModel(it, null))
        }
        println(optionModels)
    }

    fun findOptionIndexBaseOnExtra(): Int {
        extra.options?.shuffle()
        val selectedOption = extra.options?.get(0)!!
        println(selectedOption)
        val option: OptionModel? = optionModels.find { it.extraOptions.id == selectedOption.id }
        return option?.let {
            optionModels.indexOf(it)
        } ?: -1
    }

    @Test
    fun removeAllData() {
        optionModels.forEach {
            it.dataOption = null
        }
        println(optionModels)
    }

    @Test
    fun addData() {
        val index = findOptionIndexBaseOnExtra()
        optionModels.get(index = index).dataOption = Option("3", "2.0", 1.0)
        assertTrue(optionModels[index].dataOption != null)
        println(optionModels)
    }

    @Test
    fun removeData() {
        val index = findOptionIndexBaseOnExtra()
        optionModels.get(index = index).dataOption = null
        assertFalse(optionModels[index].dataOption != null)
        println(optionModels)
    }

    @Test
    fun testPickerInChoiceSelectFormat() {
        val selectedOption = Option("1", "0.0", 0.0)
        if (selectedOption.value == 1.0) {
            selectedOption.value = 0.0
            attrElement.data?.options?.remove(selectedOption)
        } else {
            if (attrElement.data?.options == null) {
                attrElement.data?.options = arrayListOf()
                attrElement.data?.options!!.add(selectedOption)
            } else {
                if (attrElement.data?.options?.contains(selectedOption) == true) {
                    attrElement.data?.options?.remove(selectedOption)
                    attrElement.data?.options?.add(selectedOption)
                } else {
                    attrElement.data?.options?.add(selectedOption)
                }
            }
            selectedOption.value = 1.0
        }
        attrElement.data?.options?.forEach {
            if (it.id != selectedOption.id) {
                it.value = 0.0
                attrElement.data?.options?.remove(it)
            }

        }
        println(attrElement.data?.options)

    }


}