package com.irancell.nwg.ios.util

interface AdapterListener<T> {
     fun onClick(t: T,position : Int,action : Int)

}