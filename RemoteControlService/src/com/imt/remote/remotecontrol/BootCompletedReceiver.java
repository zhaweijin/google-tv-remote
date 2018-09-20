package com.imt.remote.remotecontrol;

import com.reveive.protocol.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver  
{  
	private SharedPreferences settingPreferences;
    private String packagename = "com.android.settings";
    private String filename = "com.android.setting.remoteconrol";
    private String TAG = "HelloBroadcast";
    @Override  
    public void onReceive(Context context, Intent intent)  
    {  
        Log.i("MainActivity22", "系统启动完毕");  
        
        try {
			 
			Context otherAppsContext = null;
            otherAppsContext = context.createPackageContext(packagename, Context.CONTEXT_IGNORE_SECURITY);
            Utils.print(TAG, "get broadcast");
            settingPreferences = otherAppsContext.getSharedPreferences(filename, Context.MODE_PRIVATE);
            boolean flag = settingPreferences.getBoolean("remote_control_service",true);
            Utils.print(TAG, "flag =="+flag);
			if (flag) {
				Intent service = new Intent(context, RemoteControlServices.class);
				service.putExtra("boot", "start");
				context.startService(service);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Utils.print(TAG, "boot failed");
		}

    }  
}  