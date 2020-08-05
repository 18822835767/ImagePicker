package com.example.matisse.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 运行时权限的请求.
 */
public class PermissionHelper {

    public static void requestPermissions(Activity activity, String[] permissions, final int requestCode) {
        List<String> permissionList = new ArrayList<>();

        //筛选出需要处理的权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        //权限请求
        if (permissionList.size() != 0) {
            String[] requests = new String[permissionList.size()];
            for(int i=0;i<permissionList.size();i++){
                requests[i] = permissionList.get(i);
            }
            ActivityCompat.requestPermissions(activity, requests, requestCode);
        }
    }

    /**
     * 判断权限有没有全部申请成功
     */
    public static boolean permissionAllow(Context context, String[] permissions){
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context,permissions[i]) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
