package com.irancell.nwg.ios.util.toast

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.irancell.nwg.ios.R



fun  showToast(context: Context ,resId : Int) {
    val toast = Toast(context)
    val view: View = LayoutInflater.from(context)
        .inflate(R.layout.toast_layout, null)
    val tvMessage: TextView = view.findViewById(R.id.txt_limit_text)
    tvMessage.text = context.resources.getString(resId)
    toast.view = view
    toast.show()
}
fun  showToast(context: Context ,message : String? = "") {
    val toast = Toast(context)
    val view: View = LayoutInflater.from(context)
        .inflate(R.layout.toast_layout, null)
    val tvMessage: TextView = view.findViewById(R.id.txt_limit_text)
    tvMessage.text = message
    toast.view = view
    toast.show()
}