package com.itb.dam.jiafuchen.spothub.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.itb.dam.jiafuchen.spothub.R
import java.io.ByteArrayOutputStream
import java.util.*

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

        fun getRandomUsername() : String {
            val uuid = UUID.randomUUID().toString()
            return "User_$uuid"
        }

        suspend fun getByteArrayFromDrawable(context: Context ,drawable: Int): ByteArray {
            val drawable = ContextCompat.getDrawable(context, drawable)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }

    }
}