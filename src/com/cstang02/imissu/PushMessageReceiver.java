package com.cstang02.imissu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
 
/**
 *
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 **/
public class PushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = PushMessageReceiver.class
            .getSimpleName();

    private long [] pattern = {0, 200, 100, 200, 100, 200};
   
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        //Log.d(TAG, responseString);

        if (errorCode == 0) {
            Utils.setBind(context, true);
            Utils.UserId = userId;
        }
        
        updateContent(context, responseString);
    }

    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "push message=\"" + message
                + "\" customContentString=" + customContentString;
        //Log.d(TAG, messageString);
    
        Utils.Message = message;
        updateContent(context, messageString);
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        //Log.d(TAG, responseString);

        if (errorCode == 0) {
            Utils.setBind(context, false);
        }
        
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
        //Log.d(TAG, "updateContent");
    	if (Utils.UserId.isEmpty() || Utils.TargetId.isEmpty())
    	{
    		String logText = "" + Utils.logStringCache;

            if (logText.isEmpty()) {
                logText += "\n";
            }

            SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss");
            logText += sDateFormat.format(new Date()) + ": ";
            logText += content;

            Utils.logStringCache = logText;
            
            Intent intent = new Intent(Utils.INTERNAL_ACTION);
            intent.setClass(context.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
    	}
    	else
    	{
    		try
        	{
        		String [] strs = Utils.Message.split(" ");
        		Utils.Message = "";
        		
        		if (Utils.TargetId.equals(strs[0]))
	        	{	
        			if (strs[1].equals("0"))
        			{
        				Utils.Toast("Success!", 0);
        			}
        			else
        			{
        				new Thread(new Pusher(Utils.TargetId, Utils.UserId + " 0", false)).start();
        				Utils.Toast("I missed you " + strs[1] + " times today!", 1);
        				((Vibrator)Utils.App.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, -1);
        			}
	        	}
        	}
        	catch (Exception e) {}
    	}
    }

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

}
