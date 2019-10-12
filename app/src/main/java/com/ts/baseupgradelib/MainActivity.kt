package com.ts.baseupgradelib

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.ts.upgrade.UpgradeUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUpgradeBtn.setOnClickListener {
            UpgradeUtils.upgrade(this,
                showProgress = true,
                progressCancelable = true,
                showUpgradeDialog = true)
                .setOnUpgradeListener(object : UpgradeUtils.OnUpgradeListener {
                    override fun onNoUpgrade() {
                        Toast.makeText(this@MainActivity, "暂无更新", Toast.LENGTH_SHORT).show()
                    }
                    override fun onStart() {}
                    override fun onSuccess(result: String) {}
                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                    override fun onFinish() {}
                })
        }
    }

}