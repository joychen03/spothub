package com.itb.dam.jiafuchen.spothub.ui.util

import android.app.AlertDialog
import android.content.Context
import android.icu.text.CaseMap.Title
import android.view.Gravity
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
    }
}