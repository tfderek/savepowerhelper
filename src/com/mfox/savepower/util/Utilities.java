package com.mfox.savepower.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyuanlu on 2017/5/9.
 */

public class Utilities {
    /**
     * 获得本机所有第三方应用的ResolveInfo列表
     * @param context
     * @return
     */
    public static List<ResolveInfo> getResolveInfos(Context context){
        List<ResolveInfo> apps;
        List<ResolveInfo> thrdApps=new ArrayList<>();
        PackageManager pm=context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps=pm.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for(ResolveInfo info:apps){
            String packageName = info.activityInfo.packageName;
            if (packageName != null) {
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                    boolean isSystemApp=(info != null)
                            && (packageInfo.applicationInfo != null)
                            && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                    if(!isSystemApp)
                        thrdApps.add(info);

                } catch (PackageManager.NameNotFoundException e) {
                }
            }
        }
        return thrdApps;
    }

    /**
     * 从数据库读取将要加入省电模式的packageName列表
     * @param context
     * @return
     */
    public static List<String> getAppsList(Context context){
        List<String> appList=new ArrayList<String>();
        ContentResolver contentResolver=context.getContentResolver();
        Uri uri=Uri.parse("content://cn.com.mfox.mpackageInfoProvider/packageinfo");
        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        while (cursor.moveToNext()){
            String pkgName=cursor.getString(cursor.getColumnIndex("packageName"));
            boolean status=cursor.getString(cursor.getColumnIndex("checked")).equals("1") ? true : false;
            if(status)
                appList.add(pkgName);
        }
        cursor.close();
        return appList;
    }

    /**
     * 根据应用包名从数据库移除相关数据
     * @param context
     * @param packageName
     * @return
     */
    public static boolean deleteApp(Context context,String packageName){
        ContentResolver contentResolver=context.getContentResolver();
        Uri uri=Uri.parse("content://cn.com.mfox.mpackageInfoProvider/packageinfo");
        int count=contentResolver.delete(uri,"packageName=?",new String[]{packageName});
        if(count>0)
            return true;
        else
            return false;
    }
    /**
     * 从数据库读取标记位，确定是否打开了省电模式
     * @param context
     * @return
     */
    public static boolean isSaveFlag(Context context){
        boolean flag;
        ContentResolver contentResolver=context.getContentResolver();
        Uri uri=Uri.parse("content://cn.com.mfox.mpackageInfoProvider/packageinfo");
        Cursor cursor=contentResolver.query(uri,null,"packageName=?",new String[]{"checkallsb"},null);
        if(cursor.moveToFirst())
            flag=cursor.getString(cursor.getColumnIndex("checked")).equals("1") ? true : false;
        else
            flag=false;
        cursor.close();
        return flag;
    }

    /**
     * 获取加入了省电模式的ResolveInfo程序列表
     * @param context
     * @return
     */
    public static List<ResolveInfo> get3rdResolveInfos(Context context){
        List<String> packageNames=getAppsList(context);
        List<ResolveInfo> resolveInfos=getResolveInfos(context);
        List<ResolveInfo> list=new ArrayList<>();
        for(ResolveInfo info:resolveInfos)
            for(String pkgName:packageNames)
                if(info.activityInfo.packageName.equals(pkgName)){
                    list.add(info);
					continue;
				}
        return list;
    }

    /**
     * 开启省电模式
     * @param context
     */
    public static void startSavePower(Context context){
        if(isSaveFlag(context)) {
            List<String> pkgList = getAppsList(context);
			for(String packageName:pkgList){
				if(!packageName.equals("checkallsb")){
					setApplicationEnable(context,packageName,false);
				}
			}
        }
    }

    /**
     * 关闭省电模式
     * @param context
     */
    public static void stopSavePower(Context context){
        if(isSaveFlag(context)) {
			List<String> pkgList = getAppsList(context); 
			for(String packageName:pkgList){
				if(!packageName.equals("checkallsb")){
					setApplicationEnable(context,packageName,true);
				}
			}
        }
    }
    /**
     * 改变指定App的Enable状态
     * @param context
     * @param packageName
     * @param enable true表示改为可用状态，false为不可用状态
     */
    public static void setApplicationEnable(Context context,String packageName,boolean enable){
    	PackageManager pm=context.getPackageManager();
    	if(enable)
    		pm.setApplicationEnabledSetting(packageName,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,0);
    	else
			pm.setApplicationEnabledSetting(packageName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,0);
    }
}