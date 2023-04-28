package com.itb.dam.jiafuchen.spothub.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.itb.dam.jiafuchen.spothub.R
import io.realm.kotlin.types.RealmInstant
import java.io.ByteArrayOutputStream
import java.io.File
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
                setPositiveButton("Ok"){_, _ ->

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

        fun byteArrayToImage(byteArray: ByteArray): Bitmap? {
            val options = BitmapFactory.Options()
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        }

        fun getByteArrayFromDrawable(context: Context ,drawableID: Int): ByteArray {
            val drawable = ContextCompat.getDrawable(context, drawableID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }

        fun getOutputDirectory(requireContext: Context): File {
            val mediaDir = requireContext.externalMediaDirs.firstOrNull()?.let {
                File(it, requireContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else requireContext.filesDir
        }

        fun uriToByteArray(requireContext: Context, value: Uri): ByteArray {
            val inputStream = requireContext.contentResolver.openInputStream(value)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }

        fun formatTime(time: Long): String {
            val date = Date(time)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            return "$year-$month-$day $hour:$minute:$second"
        }

    }
}