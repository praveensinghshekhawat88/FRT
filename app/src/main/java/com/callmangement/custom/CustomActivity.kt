package com.callmangement.custom

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.callmangement.R
import com.google.gson.Gson

open class CustomActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
    @JvmField
    var mContext: CustomActivity? = null
    private var mDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
        Log.e("startedActivity", this.javaClass.simpleName)
    }

    override fun setContentView(id: Int) {
        super.setContentView(id)
    }

    fun setupActionBar(title: String?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (!TextUtils.isEmpty(title)) {
            val bar = checkNotNull(supportActionBar)
            bar.setDisplayUseLogoEnabled(false)
            bar.setDisplayShowTitleEnabled(true)
            bar.setDisplayShowHomeEnabled(true)
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setHomeButtonEnabled(true)
            bar.title = title
        }
    }

    fun setupActionBarWithSubtitle(title: String, subtitle: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (!TextUtils.isEmpty(title)) {
            val bar = checkNotNull(supportActionBar)
            bar.setDisplayUseLogoEnabled(false)
            bar.setDisplayShowTitleEnabled(true)
            bar.setDisplayShowHomeEnabled(true)
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setHomeButtonEnabled(true)
            bar.title = Html.fromHtml("<small>$title</small>")
            bar.subtitle = Html.fromHtml("<small>$subtitle</small>")
        }
    }

    fun setupActionBar(title: String?, showBack: Boolean) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val bar = checkNotNull(supportActionBar)
        if (showBack) {
            bar.setDisplayUseLogoEnabled(false)
            bar.setDisplayShowTitleEnabled(true)
            bar.setDisplayShowHomeEnabled(true)
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setHomeButtonEnabled(true)
        }
        bar.title = title
    }

    fun restartActivity(cls: Class<*>?) {
        finish()
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun restartActivity(intent: Intent) {
        finish()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun startActivity(cls: Class<*>?) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    open fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show()
    }

    fun makeToastLong(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mContext, string, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    fun showProgress(message: String?) {
        hideProgress()
        mDialog = ProgressDialog(mContext)
        mDialog!!.setMessage(message)
        mDialog!!.setCancelable(false)
        mDialog!!.show()
    }

    fun showProgress() {
        hideProgress()
        mDialog = ProgressDialog(mContext)
        mDialog!!.setMessage("Loading...")
        mDialog!!.setCancelable(false)
        mDialog!!.show()
    }

    fun hideProgress() {
        try {
            if (mDialog != null) mDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    protected fun checkString(value: String, simpleMessage: String) {
        if (TextUtils.isEmpty(value) || "0" == value) {
            makeToast("Please input $simpleMessage")
            throw Exception()
        }
    }

    @Throws(Exception::class)
    protected fun checkStringInInput(value: String, simpleMessage: String?, editText: EditText) {
        if (TextUtils.isEmpty(value) || "0" == value) {
            //makeToast("Please input " + simpleMessage);
            editText.error = simpleMessage
            throw Exception()
        }
    }

    fun htmlString(key: String?): Spanned {
        return Html.fromHtml(key)
    }

    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun isValidEmail(target: CharSequence?): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }

        fun hideDialogKeyboard(context: Activity, editText: EditText) {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        @JvmStatic
        fun getObject(name: String?, aClass: Class<*>?): Any {
            val gson = Gson()
            return gson.fromJson(name, aClass)
        }

        @JvmStatic
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @JvmStatic
        fun showAlertDialogWithSingleButton(context: Context, message: String?) {
            if (message != null) {
                val spannableString = SpannableString(message)
                spannableString.setSpan(RelativeSizeSpan(1.2f), 0, spannableString.length, 0)
                spannableString.setSpan(
                    ForegroundColorSpan(context.resources.getColor(R.color.black)),
                    0,
                    spannableString.length,
                    0
                )

                AlertDialog.Builder(context)
                    .setTitle(context.resources.getString(R.string.app_name))
                    .setCancelable(false)
                    .setMessage(spannableString)
                    .setPositiveButton("OK") { dialog: DialogInterface?, which: Int -> }
                    .show()
            }
        }

        @JvmStatic
        fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }
}
