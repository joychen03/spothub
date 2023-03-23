package com.itb.dam.jiafuchen.spothub.ui.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.icu.text.CaseMap.Title
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity

class Utils {
    companion object{
        fun makeToast(context : Context, message : String){
            val toast = Toast.makeText(context, message , Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 250)
            toast.show()
        }

        fun makeSimpleAlert(context: Context, message: String){
            val alert = AlertDialog.Builder(context)
            alert.apply {
                setMessage(message)
                setCancelable(true)
                setPositiveButton("Ok"){dialog, it ->

                }
            }
            alert.show()
        }

        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    0
                )
            }
        }
    }
}