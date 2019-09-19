package com.ts.upgrade

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import android.widget.TextView

class UpgradeProgressDialog private constructor(context: Context?) :
    Dialog(context, R.style.upgradeProgressDialog) {

    private var msgTv: TextView

    init {
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.upgrade_progress_dialog)

        msgTv = findViewById(R.id.msgTv)
        if (TextUtils.isEmpty(message)) {
            msgTv.visibility = View.GONE
        } else {
            msgTv.visibility = View.VISIBLE
            msgTv.text = message
        }
    }

    override fun show() {
        if (!this.isShowing) {
            super.show()
        }
    }

    override fun dismiss() {
        if (this.isShowing) {
            super.dismiss()
        }
    }

    companion object {
        private var progressDialog: UpgradeProgressDialog? = null
        private var message: String? = null

        fun showDialog(context: Context, cancelable: Boolean): UpgradeProgressDialog? {
            return showDialog(
                context,
                "",
                cancelable,
                null
            )
        }

        @JvmOverloads
        fun showDialog(
            context: Context?,
            msg: String?,
            cancelable: Boolean = false,
            l: DialogInterface.OnDismissListener? = null
        ): UpgradeProgressDialog? {
            message = msg
            try {
                hideDialog()
                progressDialog = UpgradeProgressDialog(context)
                progressDialog?.setCancelable(cancelable)
                if (l != null) {
                    progressDialog?.setOnDismissListener(l)
                }
                if (context is Activity && context.isFinishing) {
                    progressDialog = null
                } else {
                    progressDialog?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return progressDialog
        }

        fun hideDialog() {
            try {
                if (progressDialog != null && progressDialog!!.isShowing) {
                    progressDialog?.setOnDismissListener(null)
                    progressDialog?.dismiss()
                    progressDialog = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}