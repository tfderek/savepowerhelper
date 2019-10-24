package com.mfox.savepower.bean;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import android.os.UserManager;

import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;

import android.os.BatteryStats;

import com.mfox.savepower.R;

import android.os.Bundle;
//import android.os.BatteryStats;
import android.os.Debug;
import android.os.SystemProperties; 
/**
 * Created by zhangyuanlu on 2017/5/8.
 */

public class MAppInfo {
    private Context mContext;
    private String packageName;
    private String packageLabel;
    private List<String> serviceList;
    private boolean isChecked;
    private Drawable icon;
    private String buttery="";
    private String memory="";
    private int countProcess=0;
    private int countService=0;

    public MAppInfo(Context mContext) {
        this.mContext = mContext;
    }

    public int getCountProcess() {
		return countProcess;
	}

	public void setCountProcess() {
		
	}

	public int getCountService() {
		return countService;
	}

	public void setCountService() {
		this.countService =getCountService(this.packageName);
	}

	public String getButtery() {
        return buttery;
    }

    public void setButtery() {
        try {
            this.buttery = getButteryInfo(this.packageName,mContext);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory() {
        try {
            this.memory = getMemoryInfo(this.packageName,mContext);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MAppInfo() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageLabel() {
        return packageLabel;
    }

    public void setPackageLabel(String packageLabel) {
        this.packageLabel = packageLabel;
    }

    public boolean isChecked() {
        return getPackageStatus(this.packageName);
    }

    public void setChecked(boolean checked) {
        this.isChecked=checked;
        updateAppStatus(this.packageName,checked);
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int updateAppStatus(String packageName,boolean isChecked){
        Uri uri = Uri.parse("content://cn.com.mfox.mpackageInfoProvider/packageinfo");
        ContentResolver contentResolver=mContext.getContentResolver();
        ContentValues values=new ContentValues();
        values.put("checked", isChecked);
        return contentResolver.update(uri, values, "packageName=?", new String[]{packageName});
    }

    public  boolean getPackageStatus(String packageName){
        ContentResolver contentResolver=mContext.getContentResolver();
        Uri uri=Uri.parse("content://cn.com.mfox.mpackageInfoProvider/packageinfo");
        Cursor cursor=contentResolver.query(uri, null, "packageName=?", new String[]{packageName}, null);
        boolean status;
        if(cursor.moveToFirst()){
            String pkgName=cursor.getString(cursor.getColumnIndex("packageName"));
            status=cursor.getString(cursor.getColumnIndex("checked")).equals("1") ? true : false;
        }else{
        	boolean defaultChecked=false;
			if(SystemProperties.get("ro.build.product").indexOf("a_15_yq")>=0){
				SharedPreferences sharedPreferences=mContext.getSharedPreferences("shared", Context.MODE_PRIVATE);
		    	boolean isFirst=sharedPreferences.getBoolean("ISFIRST", true);
			//	if(isFirst)
					if(!packageName.equals("com.tencent.mobileqq")&&
							!packageName.equals("com.tencent.mm")&&
							!packageName.equals("checkallsb"))
						defaultChecked=true;
			}
            ContentValues values=new ContentValues();
            values.put("packageName", packageName);
            values.put("checked", defaultChecked);
            contentResolver.insert(uri, values);
            status=defaultChecked;
        }
        cursor.close();
        return status;
    }

    public int getCountService(String packageName){
    	int count=0;
    	List<ActivityManager.RunningServiceInfo> listService;
    	ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
    	listService=am.getRunningServices(40);
    	for(ActivityManager.RunningServiceInfo info:listService){
    		if(info.service.getPackageName().equals(packageName))
    			count++;
    	}
    	return count;
    }
    public static String getButteryInfo(String packageName, Context context)
            throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageInfo(packageName, 0);
        BatteryStatsHelper mBatteryHelper = new BatteryStatsHelper(context,
                true);
        mBatteryHelper.create((Bundle) null);
        UserManager mUserManager = (UserManager) context
                .getSystemService(Context.USER_SERVICE);
        BatterySipper mSipper = null;
        mBatteryHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED,
                mUserManager.getUserProfiles());
        List<BatterySipper> usageList = mBatteryHelper.getUsageList();
        final int N = usageList.size();
        for (int i = 0; i < N; i++) {
            BatterySipper sipper = usageList.get(i);
            if (sipper.getUid() == mPackageInfo.applicationInfo.uid) {
                mSipper = sipper;
                break;
            }
        }
        if (mSipper != null) {
            int dischargeAmount = mBatteryHelper.getStats().getDischargeAmount(
                    BatteryStats.STATS_SINCE_CHARGED);
            final int percentOfMax = (int) ((mSipper.totalPowerMah)
                    / mBatteryHelper.getTotalPower() * dischargeAmount + .5f);
            return percentOfMax + "%";

        } else {
            return context.getResources().getString(R.string.tv_info_unknow);
        }
    }

    public String getMemoryInfo(String packageName, Context context)
            throws PackageManager.NameNotFoundException {
        int memory;
        List<Integer> pidsList = new ArrayList<Integer>();
        PackageManager pm = context.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageInfo(packageName, 0);
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            String[] pkgNames = runningAppProcessInfo.pkgList;
            for (int i = 0; i < pkgNames.length; i++) {
                if (pkgNames[i].equals(packageName)){
                    pidsList.add(runningAppProcessInfo.pid);
                    countProcess++;
                }
            }
        }
        int size = pidsList.size();
        if (size > 0) {
            int[] pids = new int[size];
            for (int i = 0; i < size; i++) {
                pids[i] = pidsList.get(i);
            }
            Debug.MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo(pids);
            Debug.MemoryInfo pidMemoryInfo = memoryInfoArray[0];
            memory = pidMemoryInfo.getTotalPrivateDirty();
            if (memory > 1024)
                return memory / 1024 + "MB";
            else
                return memory + "KB";
        } else {
            return 0 + "";
        }
    }
}
