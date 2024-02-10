package com.irancell.nwg.ios.util

import android.widget.EditText


fun EditText.calculateLengthNoSpace(): Int {
    this.text.toString().replace(" ", "")
    return this.text.toString().length
}

