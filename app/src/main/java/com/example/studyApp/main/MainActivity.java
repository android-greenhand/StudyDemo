package com.example.studyApp.main;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.studyApp.R;
import com.example.studyApp.StatusBar.StatusBarUtils;
import com.example.studyApp.utils.DensityUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO gzp  1.全屏 2.去掉状态栏
public class MainActivity extends AppCompatActivity {

    public final static String INTENT_PATH_KEY = "com.example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //根据Intent得到到那一级目录展示
        String path = getIntent().getStringExtra(INTENT_PATH_KEY);
        ((TextView) findViewById(R.id.main_activity_title_text)).setText(TextUtils.isEmpty(path) ? "主界面" : path);
        //    getSupportActionBar().setTitle(TextUtils.isEmpty(path)? "主界面":path) ;


        RecycleViewDelegate recycleViewDelegate = new RecycleViewDelegate(findViewById(R.id.main_recycle_view));

        recycleViewDelegate.recycleViewUpdate(getData(path));

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

    }

    private List<ActivityBean> getData(String path) {
        List<ActivityBean> activityBeans = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_SAMPLE_CODE);
        intent.setPackage(getPackageName());

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);


        //将这一级目录划分后的数组
        String prefixPath[] = null;
        //目录加/
        String prefixWithSlash = null;
        if (!TextUtils.isEmpty(path)) {
            prefixPath = path.split("/");
            prefixWithSlash = path + "/";
        }
        // 过滤重复的目录
        Set<String> stringSet = new HashSet<>();

        for (int i = 0; i < list.size(); i++) {
            ResolveInfo resolveInfo = list.get(i);
            String label = resolveInfo.activityInfo.loadLabel(this.getPackageManager()).toString();
            //进度App主界面时，intent没有储存Label
            if (path == null || label.startsWith(prefixWithSlash)) {
                //androidManifest 的label
                String labelPath[] = label.split("/");
                //下一级目录在labelPath 的位置
                int len = prefixPath == null ? 0 : prefixPath.length;

                //如果androidManifest 的label 和 prefixPath 一样，证明没有子目录
                if (labelPath.length - 1 == len) {
                    //最后跳转的Activity的名称
                    String ActivityName = labelPath[len];
                    Intent intent1 = new Intent();
                    intent1.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    activityBeans.add(new ActivityBean(ActivityName, intent1, false));
                    stringSet.add(ActivityName);

                    /**
                     * 过滤重复目录  MAIN/Binder/AIDL  MAIN/Binder/AIDL2
                     * 当 path = MAIN  过滤掉 MAIN/Binder/AIDL2
                     * 下一级目录 会显示 Binder
                     */
                } else if (!stringSet.contains(labelPath[len])) {
                    //还在原地进行跳转
                    Intent intent1 = new Intent(this, MainActivity.class);
                    //下一级路径
                    String s = TextUtils.isEmpty(path) ? labelPath[0] : prefixWithSlash + labelPath[len];
                    // 储存在intent
                    intent1.putExtra(INTENT_PATH_KEY, s);
                    activityBeans.add(new ActivityBean(labelPath[len], intent1, true));
                    stringSet.add(labelPath[len]);
                }
            }
        }
        return activityBeans;
    }
}