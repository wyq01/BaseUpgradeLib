package com.ts.upgrade

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.io.File

object UpgradeUtils {

    private const val UPGRADE_URL = "http://appmng.techservice.com.cn/api/app-version"

    interface OnUpgradeListener {
        fun onStart()
        fun onSuccess(result: String)
        fun onError(error: String)
        fun onFinish()
    }

    private var onUpgradeListener: OnUpgradeListener? = null
    @SuppressLint("StaticFieldLeak")
    private var progressDialog: UpgradeProgressDialog? = null
    @SuppressLint("StaticFieldLeak")
    private var upgradeDialog: UpgradeDialog? = null
    private var apkFile: File? = null

    fun upgrade(context: Context): UpgradeUtils {
        return upgrade(context,
            showProgress = false,
            progressCancelable = true,
            showUpgradeDialog = true
        )
    }

    fun upgrade(context: Context, showProgress: Boolean, progressCancelable: Boolean, showUpgradeDialog: Boolean): UpgradeUtils {
        OkGo.get<String>(UPGRADE_URL)
            .tag(context)
            .params("packageName", context.packageName)
            .execute(object : StringCallback() {
                override fun onStart(request: Request<String, out Request<Any, Request<*, *>>>?) {
                    super.onStart(request)
                    onUpgradeListener?.onStart()
                    if (showProgress) {
                        progressDialog = UpgradeProgressDialog.showDialog(context, "", progressCancelable)
                        if (progressDialog != null && progressCancelable) {
                            progressDialog?.setOnDismissListener {
                                cancel(context)
                            }
                        }
                    }
                }
                override fun onSuccess(response: Response<String>?) {
                    response?.let {
                        val result = it.body()
                        if (showUpgradeDialog) {
                            val code = UpgradeJsonUtil.getInt(result, "code")
                            if (code == 0) {
                                onUpgradeListener?.onSuccess(it.body())

                                val data = UpgradeJsonUtil.getString(result, "data")
                                val title = UpgradeJsonUtil.getString(data, "title")
                                val message = UpgradeJsonUtil.getString(data, "message")
                                val versionCode = UpgradeJsonUtil.getInt(data, "versionCode")
                                val versionName = UpgradeJsonUtil.getString(data, "versionName")
                                val forceUpgrade = UpgradeJsonUtil.getBoolean(data, "forceUpgrade")
                                val downloadUrl = UpgradeJsonUtil.getString(data, "downloadUrl")
                                val fileSize = UpgradeJsonUtil.getString(data, "fileSize")
                                val publishTime = UpgradeJsonUtil.getLong(data, "publishTime")

                                val currentVersionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
                                if (currentVersionCode < versionCode) {
                                    upgradeDialog = UpgradeDialog(context)
                                        .setTitle(title)
                                        .setVersion(versionName)
                                        .setFileSize(fileSize)
                                        .setUpgradeTime(UpgradeDateFormatUtil.format(UpgradeDateFormatUtil.yyyy_MM_dd_HH_mm_ss, publishTime))
                                        .setUpgradeMsg(message)
                                        .setCancelable(!forceUpgrade)
                                        .setNeutralButton("去浏览器") {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                            context.startActivity(intent)
                                        }
                                        .setPositiveButton("确定") {
                                            download(context, downloadUrl)
                                        }
                                    if (!forceUpgrade) {
                                        upgradeDialog?.setNegativeButton("取消") { dialog ->
                                            if (!forceUpgrade) {
                                                dialog.cancel()
                                            }
                                        }
                                    }
                                    upgradeDialog?.show()
                                }
                            } else {
                                onUpgradeListener?.onError(UpgradeJsonUtil.getString(result, "msg"))
                            }
                        }
                    } ?: let {
                        onUpgradeListener?.onError("")
                    }
                }
                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    response?.let {
                        onUpgradeListener?.onError(it.message())
                    }
                }
                override fun onFinish() {
                    super.onFinish()
                    onUpgradeListener?.onFinish()
                    progressDialog?.setOnDismissListener(null)
                    UpgradeProgressDialog.hideDialog()
                }
            })
        return this
    }

    fun setOnUpgradeListener(l: OnUpgradeListener?): UpgradeUtils {
        this.onUpgradeListener = l
        return this
    }

    fun cancel(context: Context) {
        OkGo.getInstance().cancelTag(context)
    }

    private fun download(context: Context, url: String) {
        var apkExists = false
        apkFile?.let {
            apkExists = it.exists()
        }
        if (apkExists) {
            AndPermission.with(context).install().file(apkFile).start()
        } else {
            AndPermission.with(context)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted {
                    var selfPermission = true
                    for (item in it) {
                        if (!isGranted(context, item)) {
                            selfPermission = false
                            break
                        }
                    }
                    if (selfPermission) {
                        OkGo.get<File>(url)
                            .tag(this)
                            .execute(object : FileCallback() {
                                override fun onSuccess(response: Response<File>?) {
                                    response?.let { r ->
                                        apkFile = r.body()
                                        AndPermission.with(context).install().file(apkFile).start()
                                    }
                                }
                                override fun downloadProgress(progress: Progress?) {
                                    super.downloadProgress(progress)
                                    progress?.let { p ->
                                        upgradeDialog?.setProgress((p.currentSize * 100L / p.totalSize).toInt())
                                    }
                                }
                                override fun onError(response: Response<File>?) {
                                    super.onError(response)
                                    Toast.makeText(context, "下载出错，请重试", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "应用内更新需要授予所需权限，如不授予可使用浏览器下载安装", Toast.LENGTH_SHORT).show()
                    }
                }
                .rationale { c, _, executor ->
                    AlertDialog.Builder(c)
                        .setMessage("下载安装包，需要读写存储权限")
                        .setPositiveButton("允许") { _, _ ->
                            executor.execute()
                        }
                        .setNegativeButton("拒绝") { _, _ ->
                            executor.cancel()
                        }
                        .create()
                        .show()
                }
                .onDenied {
                    Toast.makeText(context, "应用内更新需要授予所需权限，如不授予可使用浏览器下载安装", Toast.LENGTH_SHORT).show()
                }
                .start()
        }
    }

    private fun isGranted(context: Context, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        }
    }

}