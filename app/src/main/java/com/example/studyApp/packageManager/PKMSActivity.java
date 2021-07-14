package com.example.studyApp.packageManager;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studyApp.R;


public class PKMSActivity extends AppCompatActivity {

    final static String TAG = "gzpPKMSActivity";
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_pkmsactivity);
        findViewById(R.id.request_permission_btn).setOnClickListener(v -> requestPermission());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 手机震动 还需要在AndroidManifest声明权限
     *
     */
    private void phoneVibrates(){
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
    }

    private void requestPermission() {
        Log.i(TAG, "requestPermission");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkSelfPermission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                Log.i(TAG, "shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                Log.i(TAG, "requestPermissions");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
// 第三步:重写onRequestPermissionsResult方法根据用户的不同选择做出响应。


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.i(TAG, "onRequestPermissionsResult denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showWaringDialog();
                    return;
                }
            }


        }
    }


    // other 'case' lines to check for other
// permissions this app might request
// 如果点击 拒绝，就会弹出这个
    private void showWaringDialog() {
        new AlertDialog.Builder(this)
                .setTitle("警告!")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则 功能无法正常运行!")

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();

                    }
                }).show();
    }
}