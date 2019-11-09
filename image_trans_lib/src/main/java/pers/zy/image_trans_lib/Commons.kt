@file: JvmName("Commons")
package pers.zy.image_trans_lib

import android.content.Context

/**
 * date: 2019-11-09   time: 17:31
 * author zy
 * Have a nice day :)
 **/

fun getScreenWidth(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
}

fun getScreenHeight(context: Context): Int {
    return context.resources.displayMetrics.heightPixels
}