package com.callmangement.custom

import android.app.ProgressDialog
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class BaseFragment : Fragment() {
    var mContext: FragmentActivity? = null
    private var mDialog: ProgressDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext != null) {
            mContext = context as FragmentActivity
        }
    }

    fun showProgress(message: String?) {
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
}
