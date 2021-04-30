package com.example.myapplication;

import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.MyLogUtil;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.PrivacyPolicy;
import com.tencent.mmkv.MMKV;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        MMKV.initialize(this);
        BaseApp.get();
        Locale locale = null;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = getApplicationContext().getResources().getConfiguration().getLocales();
            if (localeList != null && !localeList.isEmpty()) {
                locale = localeList.get(0);
            }
        } else {
            locale = getApplicationContext().getResources().getConfiguration().locale;
        }
        // 同步方法查询隐私,locale可以为null或不设置，默认使用当前系统语言
//        PrivacyPolicy policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL, locale);
//        String url = policyUrl.getContent();

// 异步方法查询隐私,locale可以为null或不设置，默认使用当前系统语言
        MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_TXT, new PrivacyPolicy.OnPolicyListener() {
            @Override
            public void onComplete(PrivacyPolicy data) {
                if (data != null) {
                    // 富文本内容
                    String text = data.getContent();
                    MyLogUtil.e("MobPush", text);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                // 请求失败
                Log.d("MobPush", t.toString());
            }
        });
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.e("MobPush", "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MobPush", "隐私协议授权结果提交：失败");
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}