package com.irancell.nwg.ios.util

import android.annotation.SuppressLint
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
fun View.setAnimationClick(lifecycleCoroutineScope: LifecycleCoroutineScope,anim: Int, onClickListener: View.OnClickListener) {
    this.setOnClickListener {
        this.isClickable = false
        lifecycleCoroutineScope.launch {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, anim));
            onClickListener.onClick(it)
            delay(1000)
            this@setAnimationClick.isClickable = true
        }
    }
}

fun View.setDelayedClick(delay: Long, onClickListener: OnClickListener) {
    this.setOnClickListener {
        this.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            delay(delay)
            onClickListener.onClick(it)
            this@setDelayedClick.isEnabled = true
        }
    }
}


fun View.setAnimationDelayedClick(lifecycleCoroutineScope: LifecycleCoroutineScope,anim: Int, delay: Long, onClickListener: OnClickListener) {
    this.setOnClickListener {
        this.isEnabled = false

        lifecycleCoroutineScope.launch {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, anim));
            delay(delay)
            onClickListener.onClick(it)
            this@setAnimationDelayedClick.isEnabled = true

        }
    }
}

fun View.setAnimationDelayedParentClick(
    view: View,
    anim: Int,
    delay: Long,
    onClickListener: OnClickListener
) {
    this.setOnClickListener {
        this.isEnabled = false


        CoroutineScope(Dispatchers.Main).launch {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, anim));
            delay(delay)
            onClickListener.onClick(it)
            this@setAnimationDelayedParentClick.isEnabled = true

        }
    }
}

fun enableDisableView(view: View, enabled: Boolean) {
    view.isEnabled = enabled
    if (view is ViewGroup) {
        val group = view
        for (idx in 0 until group.childCount) {
            enableDisableView(group.getChildAt(idx), enabled)
        }
    }
}

data class State(val siteId: Int , var subGroupIndex : Int,var elementIndex : Int ,var innerGroupIndex : Int){
    override fun toString(): String {
        return "State(siteId=$siteId, subGroupIndex=$subGroupIndex, elementIndex=$elementIndex, innerGroupIndex=$innerGroupIndex)"
    }
}

private var stateList = arrayListOf<State>()
fun addState(state: State) {
    stateList.add(state)
}

fun removeState() {
    if (stateList.size > 0)
        stateList.removeAt(stateList.lastIndex)
}

fun getState() = stateList