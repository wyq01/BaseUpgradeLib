package com.ts.baseupgradelib

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.ts.upgrade.UpgradeUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            UpgradeUtils.upgrade(this,
                showProgress = false,
                progressCancelable = false,
                showUpgradeDialog = true)
        }, 1000)

        checkUpgradeBtn.setOnClickListener {
            UpgradeUtils.upgrade(this,
                showProgress = true,
                progressCancelable = false,
                showUpgradeDialog = true)
        }
    }

}