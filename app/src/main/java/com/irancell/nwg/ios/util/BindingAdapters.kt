package com.irancell.nwg.ios.util

import android.graphics.Color
import android.text.BoringLayout
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.util.constants.*
import com.irancell.nwg.ios.util.constants.ENGLISH
import com.irancell.nwg.ios.util.constants.PERSIAN
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context).load(url).into(view)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, image: ByteArray?) {
    if (image?.size!! > 0) {
        Glide.with(view.context).load(image).into(view)
    }
}


@BindingAdapter("inputText")
fun EditText.inputText(attrElement: AttrElement) {
    attrElement.data?.let {
        this.setText(it.value)
    }

}

@BindingAdapter("semicolonHint", "local", "mandatory")
fun EditText.semicolonHint(name: Name, type: String, mandatory: Boolean) {
    when (type) {
        ENGLISH -> {
            if (mandatory)
                this.hint = name.english?.trim().plus("*").plus(":")
            else
                this.hint = name.english?.trim().plus(":")
        }
        PERSIAN -> {
            if (mandatory)
                this.hint = name.persian?.trim().plus("*").plus(":")
            else
                this.hint = name.persian?.trim().plus(":")
        }
    }
}

@BindingAdapter("semicolonHint", "local", "mandatory")
fun TextView.semicolonHint(name: Name, type: String, mandatory: Boolean) {
    when (type) {
        ENGLISH -> {
            if (mandatory)
                this.hint = name.english?.trim().plus("*").plus(":")
            else
                this.hint = name.english?.trim().plus(":")

        }
        PERSIAN -> {
            if (mandatory)
                this.hint = name.persian?.trim().plus("*").plus(":")
            else
                this.hint = name.persian?.trim().plus(":")
        }
    }
}


@BindingAdapter("textLocal", "local", "mandatory")
fun TextView.textLocal(name: Name?, type: String, mandatory: Boolean) {
    when (type) {
        ENGLISH -> {
            if (mandatory)
                this.text = name?.english?.trim().plus("*")
            else
                this.text = name?.english?.trim()
        }
        PERSIAN -> {
            if (mandatory)
                this.text = name?.persian?.trim().plus("*")
            else
                this.text = name?.persian?.trim()
        }
    }
}

@BindingAdapter("textLocal", "local")
fun TextView.textLocal(name: Name?, type: String) {
    when (type) {
        ENGLISH -> {
            this.text = name?.english?.trim()
        }
        PERSIAN -> {
            this.text = name?.persian?.trim()
        }
    }
}


@BindingAdapter("direction")
fun View.direction(type: String) {
    when (type) {
        ENGLISH -> {
            this.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        PERSIAN -> {
            this.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}

@BindingAdapter("cardColor")
fun CardView.setCardColor(hexColor: String) {
    this.setCardBackgroundColor(Color.parseColor(hexColor))
}


@BindingAdapter("status")
fun TextView.status(state: Int) {
    when (state) {
        Scheduled -> {
            this.text = context.getString(R.string.Scheduled)
            this.setTextColor(Color.parseColor("#5686D6"));
        }
        InProgress -> {
            this.text = context.getString(R.string.InProgress)
            this.setTextColor(Color.parseColor("#DB4A1D"));

        }
        Visited -> {
            this.text = context.getString(R.string.Visited)
            this.setTextColor(Color.parseColor("#4CAF50"));

        }
        Problematic -> {
            this.text = context.getString(R.string.Problematic)
            this.setTextColor(Color.parseColor("#EF1515"));
        }
        Reported -> {
            this.text = context.getString(R.string.Reported)
            this.setTextColor(Color.parseColor("#9512C5"));
        }
    }
    this.gravity = Gravity.END


}

@BindingAdapter("rotation")
fun ImageView.setRotationBaseOnLocal(local: String) {
    when (local) {
        ENGLISH -> {
            this.rotation = 0f
        }
        PERSIAN -> {
            this.rotation = 180f
        }
    }
}

@BindingAdapter("statusColor")
fun TextView.statusColor(state: Int) {
    when (state) {
        InProgress -> {
            this.setTextColor(Color.parseColor("#DB4A1D"));
        }
        Visited -> {
            this.setTextColor(Color.parseColor("#4CAF50"));
        }
        Problematic -> {
            this.setTextColor(Color.parseColor("#EF1515"));
        }
        Reported -> {
            this.text = context.getString(R.string.Reported)
            this.setTextColor(Color.parseColor("#9512C5"));
        }
    }
}

@BindingAdapter("textColorTransition")
fun MaterialCheckBox.textColorTransition(value: Int) {
    when (value) {
        0 -> this.setTextColor(Color.parseColor("#5BFFBE00"))
        1 -> this.setTextColor(Color.parseColor("#FFBE00"))
    }
}


@BindingAdapter("latitude")
fun EditText.latitude(element: AttrElement) {

    element.data?.let {
        if (it.details != null) {
            it.details?.let {
                if (it.equals(""))
                    this.hint = resources.getString(R.string.latitude)
                else
                    this.setText(it.latitude, TextView.BufferType.EDITABLE)
            }

        } else {
            this.hint = resources.getString(R.string.latitude)
        }
    }


}

@BindingAdapter("longitude")
fun EditText.longitude(element: AttrElement) {
    element.data?.let { data ->


        if (data.details?.longitude != null) {
            data.details?.let {
                if (it.equals(""))
                    this.hint = resources.getString(R.string.longitude)
                else
                    this.setText(it.longitude, TextView.BufferType.EDITABLE)
            }

        } else {
            this.hint = resources.getString(R.string.longitude)
        }

    }
}

@BindingAdapter("local_gravity")
fun TextView.setGravityBaseOnLanguage(language: String) {
    when (language) {
        ENGLISH -> {
            this.gravity = Gravity.END
        }
        PERSIAN -> {
            this.gravity = Gravity.START
        }
    }
}

@BindingAdapter("reverse_gravity")
fun TextView.setRevGravityBaseOnLanguage(language: String) {
    when (language) {
        ENGLISH -> {
            this.gravity = Gravity.START
        }
        PERSIAN -> {
            this.gravity = Gravity.END
        }
    }
}

@BindingAdapter("local_rotation")
fun ImageView.setRotationBaseOnLanguage(language: String) {
    when (language) {
        ENGLISH -> {
            this.rotation = 0f
        }
        PERSIAN -> {
            this.rotation = 180f
        }
    }
}

@BindingAdapter("vectorColor")
fun ImageView.vectorColor(state: Int) {
    when (state) {
        InProgress -> {
            this.setColorFilter(
                Color.parseColor("#DB4A1D"),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
        }
        Visited -> {
            this.setColorFilter(
                Color.parseColor("#4CAF50"),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
        }
        Problematic -> {
            this.setColorFilter(
                Color.parseColor("#EF1515"),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
        }
        Reported -> {
            this.setColorFilter(
                Color.parseColor("#9512C5"),
                android.graphics.PorterDuff.Mode.SRC_IN
            );

        }
    }
}

@BindingAdapter("siteTitle")
fun TextView.setSiteTitle(siteDomain: SiteDomain) {
    this.text = String.format("%1s (%2s)", siteDomain.site_name.trim(), siteDomain.province.trim())
}

@BindingAdapter("checkToggle")
fun FlexibleSwitch.checkToggle(value: String) {
    this.isChecked = value.trim() == "1"
}

@BindingAdapter("dateFormatter")
fun TextView.dateFormatter(string: String?) {
    if (string?.isNotEmpty() == true) {
        val date = SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(string)
        date?.let {
            val format = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
            this.text = format.format(it)
        }

    }


}

@BindingAdapter("rotationOnOpen")
fun ShapeableImageView.rotationOnOpen(isOpen: Boolean) {
    if (isOpen) {
        this.rotation = 180f
    } else {
        this.rotation = 0f
    }
}

@BindingAdapter("visibilityOnOpen")
fun LinearLayout.visibilityOnOpen(isOpen: Boolean) {
    if (isOpen) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}


