package com.irancell.nwg.ios.presentation.audit.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.databinding.*
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.*
import kotlinx.coroutines.*
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager


class AttrElementAdapter(
    val lifecycleCoroutineScope: LifecycleCoroutineScope,
    val attributeListener: AttributeListener,
    val local: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var panoramicElements: ArrayList<AttrElement>
    lateinit var elements: ArrayList<AttrElement>

    companion object {
        private const val TAG = "AttrElementAdapter"
    }

    private fun getItemAt(position: Int): AttrElement {
        return elements[position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Image -> {
                return ViewHolderImage(
                    lifecycleCoroutineScope,
                    AttrImageItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, clickListener = attributeListener
                )
            }
            Barcode -> {
                return ViewHolderBarcode(
                    lifecycleCoroutineScope,
                    AttrBarcodeItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, clickListener = attributeListener
                )
            }
            Comment -> {
                return ViewHolderComment(
                    AttrCommentItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }
            Generator -> {
                return ViewHolderGenerator(
                    AttrGeneratorItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }

            MultiChoice -> {
                return ViewHolderChoiceSelect(
                    lifecycleCoroutineScope,
                    AttrMultiselectItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener, MultiChoice
                )
            }
            SingleChoice -> {
                return ViewHolderChoiceSelect(
                    lifecycleCoroutineScope,
                    AttrMultiselectItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener, SingleChoice
                )
            }
            SwitchGenerator -> {
                return ViewHolderSwitchGenerator(
                    lifecycleCoroutineScope,
                    AttrSwitchItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }
            Picker -> {
                return ViewHolderPicker(
                    AttrPickerItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }
            Location -> {
                return ViewHolderLocation(
                    AttrLocationItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }
            Gallery -> {
                return ViewHolderPickImage(
                    lifecycleCoroutineScope,
                    AttrPickImageItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }

            else -> {
                return ViewHolderImage(
                    lifecycleCoroutineScope,
                    AttrImageItemviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), local, attributeListener
                )
            }
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Image -> {
                (holder as ViewHolderImage).bind(getItemAt(position))
            }
            Barcode -> {
                (holder as ViewHolderBarcode).bind(getItemAt(position))
            }
            Comment -> {
                (holder as ViewHolderComment).bind(getItemAt(position))
            }
            MultiChoice -> {
                (holder as ViewHolderChoiceSelect).bind(getItemAt(position))
            }
            SingleChoice -> {
                (holder as ViewHolderChoiceSelect).bind(getItemAt(position))
            }
            Generator -> {
                (holder as ViewHolderGenerator).bind(getItemAt(position))
            }
            SwitchGenerator -> {
                (holder as ViewHolderSwitchGenerator).bind(getItemAt(position))
            }
            Picker -> {
                (holder as ViewHolderPicker).bind(getItemAt(position))
            }
            Location -> {
                (holder as ViewHolderLocation).bind(getItemAt(position))
            }
            Gallery -> {
                (holder as ViewHolderPickImage).bind(getItemAt(position))
            }
        }
    }

    class ViewHolderPicker(
        private val binding: AttrPickerItemViewBinding,
        private val local: String,
        private val attributeListener: AttributeListener,
    ) :
        RecyclerView.ViewHolder(binding.root),
        PickerLayoutManager.onScrollStopListener {
        lateinit var attrElement: AttrElement
        var adapter: PickerAdapter
        val pickerLayoutManager =
            PickerLayoutManager(itemView.context, PickerLayoutManager.HORIZONTAL, false)

        init {
            pickerLayoutManager.isChangeAlpha = true
            pickerLayoutManager.scaleDownBy = 0.99f
            pickerLayoutManager.scaleDownDistance = 0.8f
            adapter = PickerAdapter()
            val snapHelper: SnapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(binding.rvPicker)
            binding.rvPicker.layoutManager = pickerLayoutManager
            binding.rvPicker.adapter = adapter
            pickerLayoutManager.setOnScrollStopListener(this)
        }

        fun bind(item: AttrElement) {

            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            attrElement = item
            item.extra?.let {
                it.options?.let {
                    adapter.submitList(it)
                }
            }

            item.data?.let {
                it.options?.let { options ->

                    options[0].let { opt ->

                        val index = findIndex(attrElement.extra?.options, opt.id)

                        if (index < ((attrElement.extra?.options?.size?.minus(1)) ?: 0))
                            pickerLayoutManager.scrollToPosition(index + 1)
                        else
                            pickerLayoutManager.scrollToPosition(index)
                        binding.tvPickerValue.text = opt.name.trim()
                    }
                }
            }

        }


        override fun selectedView(view: View?) {
            checkSelectPicker(view)

        }

        private fun checkSelectPicker(view: View?) {

            val textView = (view as ViewGroup).getChildAt(1) as TextView
            binding.tvPickerValue.text = textView.text.toString()


            val extraOptions = attrElement.extra?.options
            if (attrElement.data?.options == null) {
                attrElement.data?.options = arrayListOf()
            }
            attrElement.data?.options?.clear()
            extraOptions?.let {

                for (option in extraOptions) {
                    if (option.name == textView.text.trim()) {
                        option.value = 1.0
                        attrElement.data?.options?.add(option)
                        attrElement.extra?.options?.let { it1 ->
                            adapter.submitList(it1)
                        }

                    }
                }
            }
            attributeListener.onEvent(attrElement, null, adapterPosition, Pick)
        }

    }


    class ViewHolderChoiceSelect(
        lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: AttrMultiselectItemviewBinding,
        private val local: String,
        private val attributeListener: AttributeListener,
        private val type: Int
    ) :
        RecyclerView.ViewHolder(binding.root), OptionListener {
        lateinit var attrElement: AttrElement
        var adapter: OptionsAdapter

        init {
            binding.RVChoiceItems.layoutManager = GridLayoutManager(itemView.context, 3)
            adapter = OptionsAdapter(lifecycleCoroutineScope, this)
        }

        fun bind(item: AttrElement) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            attrElement = item
            item.extra?.let {
                it.options?.let { options ->
                    adapter.options = options
                    binding.RVChoiceItems.adapter = adapter
                }

            }


        }

        override fun onOptionClick(
            selectedOption: Option,
            position: Int,
            options: ArrayList<Option>
        ) {
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
            if (type == SingleChoice) {
                options.forEach {
                    if (it.id != selectedOption.id) {
                        it.value = 0.0
                        attrElement.data?.options?.remove(it)
                    }

                }
            }

            attributeListener.onEvent(attrElement, null, adapterPosition, ChoiceSelect)

        }

    }

    class ViewHolderImage constructor(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: AttrImageItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: AttrElement) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            if (item.data?.Image != null) {
                binding.imgPreview.visibility = View.VISIBLE
                binding.imgCamera.visibility = View.GONE
                item.data?.contentUri?.let { contentUri ->
                    Glide.with(binding.root.context)
                        .load(Uri.parse(contentUri))
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .apply(RequestOptions().error(R.drawable.image_not_found))
                        .into(binding.imgPreview)
                }

            } else {
                binding.imgPreview.visibility = View.GONE
                binding.imgCamera.visibility = View.VISIBLE
            }


            binding.imgCamera.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(attrElement = item, null, adapterPosition, TakeImage)
            }
            binding.imgPreview.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(attrElement = item, null, adapterPosition, PreviewDeleteImage)

            }

        }
    }


    class ViewHolderPickImage constructor(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: AttrPickImageItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AttrElement) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            if (item.data?.Image != null) {
                binding.imgPreview.visibility = View.VISIBLE
                binding.imgPickImage.visibility = View.GONE
                item.data?.contentUri?.let {
                    Glide.with(binding.root.context)
                        .load(Uri.parse(it))
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .thumbnail(Glide.with(binding.root.context).load(R.raw.loading))
                        .apply(RequestOptions().error(R.drawable.image_not_found))
                        .into(binding.imgPreview)
                }
            } else {
                binding.imgPreview.visibility = View.GONE
                binding.imgPickImage.visibility = View.VISIBLE
            }

            binding.imgPickImage.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(attrElement = item, null, adapterPosition, PickImage)
            }
            binding.imgPreview.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(
                    attrElement = item,
                    null,
                    adapterPosition,
                    PreviewDeleteImage
                )
            }


        }
    }

    class ViewHolderLocation constructor(
        private val binding: AttrLocationItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var element: AttrElement
        private val latTextWatcher: LatTextWatcher =
            LatTextWatcher()
        private val lonTextWatcher: LonTextWatcher =
            LonTextWatcher()

        init {
            binding.etvLatitude.addTextChangedListener(latTextWatcher)
            binding.etvLongitude.addTextChangedListener(lonTextWatcher)
        }

        fun bind(item: AttrElement) {
            element = item
            binding.element = item
            binding.local = local
            binding.executePendingBindings()
            latTextWatcher.updateItem(item)
            lonTextWatcher.updateItem(item)

            binding.imgGetLocation.setOnClickListener {
                clickListener.onEvent(attrElement = element, null, adapterPosition, GetLocation)
            }

        }

        class LatTextWatcher(
        ) : TextWatcher {


            var attrElement: AttrElement? = null
            fun updateItem(attrElement: AttrElement) {
                this.attrElement = attrElement
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                attrElement?.let {
                    if (it.data?.details != null)
                        it.data?.details?.latitude = editable.toString()
                    else
                        it.data = Data(
                            Detail(null, editable.toString(), null)
                        )
                }
            }
        }

        class LonTextWatcher(
        ) : TextWatcher {


            var attrElement: AttrElement? = null
            fun updateItem(attrElement: AttrElement) {
                this.attrElement = attrElement
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                attrElement?.let {
                    if (it.data != null)
                        it.data?.details?.longitude = editable.toString()
                    else
                        it.data = Data(

                            Detail(null, null, editable.toString())
                        )
                }
            }
        }
    }

    class ViewHolderBarcode constructor(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: AttrBarcodeItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AttrElement) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            binding.txtBarcode.visibility = View.GONE
            item.data.let {
                if (item.data != null) {
                    binding.txtBarcode.visibility = View.VISIBLE
                    item.data?.value?.let {
                        binding.txtBarcode.text = it
                    }
                } else {
                    binding.txtBarcode.visibility = View.GONE
                }
            }
            binding.barcode.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(attrElement = item, null, adapterPosition, ScanCode)
            }
            binding.txtBarcode.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onEvent(attrElement = item, null, adapterPosition, DeleteCode)
            }

        }
    }

    class ViewHolderComment constructor(
        private val binding: AttrCommentItemviewBinding,
        private val local: String,
        attributeListener: AttributeListener


    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val myTextWatcher: MyTextWatcher = MyTextWatcher(attributeListener)

        init {
            binding.etvComment.addTextChangedListener(myTextWatcher)
        }


        @SuppressLint("SuspiciousIndentation")
        fun bind(item: AttrElement) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            myTextWatcher.updateItem(item, adapterPosition)
        }

        class MyTextWatcher(
            private val attributeListener: AttributeListener
        ) : TextWatcher {


            var attrElement: AttrElement? = null
            var adapterPosition: Int = 0
            fun updateItem(attrElement: AttrElement, adapterPosition: Int) {
                Log.i(TAG, "updateItem: " + adapterPosition)
                this.attrElement = attrElement
                this.adapterPosition = adapterPosition
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                attrElement?.let {
                    if (it.data != null)
                        it.data?.value = editable.toString()
                    else {
                        val data = Data(
                            null,
                        )
                        data.value = editable.toString()
                        it.data = data
                    }

                    attributeListener.onEvent(it, null, adapterPosition, TextChange)
                }
            }
        }
    }

    class ViewHolderGenerator constructor(
        private val binding: AttrGeneratorItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener
    ) :
        RecyclerView.ViewHolder(binding.root), InnerGroupListener {
        lateinit var attrElement: AttrElement
        fun bind(item: AttrElement) {
//            Timber.tag("elementName").i("bind: %s", item.attrElementName)
            attrElement = item
            binding.data = item
            binding.local = local
            binding.executePendingBindings()

            if (item.data?.value == null)
                item.data?.value = "0"



            if (item.group != null) {
                val adapter = InnerGroupAdapter(item.group!!, local, this)
                binding.RVGroups.adapter = adapter
                binding.RVGroups.visibility = View.VISIBLE
            } else {
                binding.RVGroups.visibility = View.GONE
            }
            binding.tvAttrNumber.text = item.data?.value
            binding.btnAddButton.setOnClickListener {
                if (item.data?.value != "0") {
                    binding.btnAddButton.setOnClickListener(null)
                    binding.btnRemoveButton.setOnClickListener(null)
                }
                clickListener.onEvent(item, null, position = adapterPosition, Generate)
            }

            binding.btnRemoveButton.setOnClickListener {
                if (item.data?.value != "0") {
                    binding.btnRemoveButton.setOnClickListener(null)
                    binding.btnAddButton.setOnClickListener(null)
                }
                clickListener.onEvent(item, null, position = adapterPosition, Remove)
            }
        }


        override fun onClick(innerGroup: Group, position: Int) {
            clickListener.onEvent(
                attrElement = attrElement,
                position,
                adapterPosition,
                OpenInnerGroup
            )
        }


    }


    class ViewHolderSwitchGenerator constructor(
        private val lifeCycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: AttrSwitchItemviewBinding,
        private val local: String,
        private val clickListener: AttributeListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var attrElement: AttrElement


        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: AttrElement) {
            Log.i(TAG, "ViewHolderSwitchGenerator bind: " + item.data)
            if (item.data?.value == null)
                item.data?.value = "0"


            attrElement = item
            binding.element = item
            binding.local = local
            binding.data = item.data
            binding.executePendingBindings()


            binding.linSwitch.setAnimationDelayedClick(
                lifeCycleCoroutineScope,
                R.anim.button_click,
                200
            ) {
                if (item.data?.value == "0") {
                    item.data?.value = "1"
                    binding.switchView.isChecked = true
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(200)
                        clickListener.onEvent(
                            attrElement,
                            null,
                            position = adapterPosition,
                            SwitchOn
                        )
                    }

                } else {

                    item.data?.value = "0"
                    binding.switchView.isChecked = false
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(200)
                        clickListener.onEvent(
                            attrElement,
                            null,
                            position = adapterPosition,
                            SwitchOff
                        )
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemAt(position).type.id
    }

    override fun getItemCount(): Int {
        return elements.size
    }

}


interface AttributeListener {
    fun onEvent(attrElement: AttrElement, innerGroupPosition: Int?, position: Int, action: Int)

}

