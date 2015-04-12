package com.cstang02.imissu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Utils {
    public static final String TAG = "PushDemoActivity";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";
    public static final String INTERNAL_ACTION = "com.cstang02.imissu.action.internal";
    
    public static final String API_KEY = "XwkXMdBDHbccgGRiooyj45HG";
    public static final String API_SECRET = "7WhGYmhyA3S2kbORjXq9nby5RhzhKXER";
	
    public static String logStringCache = "";
    public static String UserId = "";
    public static String TargetId = "";
    public static String Message = "";
    public static String Result = "";
    public static int Count = 0;
    public static int Day = 0;
    public static Application App = null;
    public static Handler Handler = null;
    
    private static Toast toast = null;    

    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }

    public static String getTargetId(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        TargetId = sp.getString("targetId", "");
        return TargetId;
    }

    public static void setTargetId(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("targetId", text);
        editor.commit();
        TargetId = text;
    }
    
    public static String getUserId(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        UserId = sp.getString("userId", "");
        return UserId;
    }

    public static void setUserId(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("userId", text);
        editor.commit();
        UserId = text;
    }
    
    public static void clearCount(Context context) {
    	SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("count", "0");
        editor.commit();
        Count = 0;
    }
    
    public static int increaseCount(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (Count == 0)
        {
        	Count = Integer.parseInt(sp.getString("count", "0"));
        }
        Editor editor = sp.edit();
        editor.putString("count", "" + ++Count);
        editor.commit();
        return Count;
    }
    
    public static int getDay(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Day = Integer.parseInt(sp.getString("day", "0"));
        return Day == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Day;
    }

    public static void setDay(Context context, int d) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("day", "" + d);
        editor.commit();
        Day = d;
    }
    
    public static void Toast(String msg, int flag)
    {
    	if (toast != null)
    	{
    		toast.cancel();
    	}
    	
    	toast = Toast.makeText(App.getApplicationContext(), msg, flag);
    	toast.show();
    }
}
