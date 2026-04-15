package com.dingding.daka.service;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * 透明Activity - 用于在后台启动钉钉，绕过华为系统限制
 */
public class LaunchDingTalkActivity extends Activity {

    private static final String TAG = "LaunchDingTalk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 启动钉钉
        launchDingTalk();

        // 立即关闭自己
        finish();
    }

    private void launchDingTalk() {
        // 钉钉可能的包名列表
        String[] dingtalkPackages = {
            "com.alibaba.android.rimet",
            "com.dingtalk",
            "com.alibaba.android.rimet.biz",
        };

        for (String packageName : dingtalkPackages) {
            try {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(launchIntent);
                    return;
                }
            } catch (Exception e) {
                // 继续尝试下一个包名
            }
        }

        // 尝试Uri方式
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("dingtalk://"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // 失败
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).post(this::finish);
        }
    }
}
