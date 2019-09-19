#### 应用内更新库

```groovy
implementation "com.android.support:appcompat-v7:${support_library_version}"

implementation 'com.lzy.net:okgo:3.0.4' // 网络访问
implementation 'com.yanzhenjie.permission:support:2.0.1' // 权限处理
implementation 'com.ts.lib_dimens:lib_dimens:1.0.0' // dimens资源
```

> 在manifest中配置升级信息 <meta-data android:name="upgrade_message" android:value="升级信息" />
