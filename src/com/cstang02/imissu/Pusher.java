package com.cstang02.imissu;

import android.content.Intent;
import android.os.Looper;
import android.os.Message;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;

public class Pusher implements Runnable {
	
	private static final String API_KEY = "XwkXMdBDHbccgGRiooyj45HG";
	private static final String API_SECRET = "";
    
	private BaiduChannelClient channelClient = new BaiduChannelClient(new ChannelKeyPair(API_KEY, API_SECRET));
	
	private String userId = null;
	private String message = null;
	
    public Pusher(String id, String msg) {
    	userId = id;
    	message = msg;
    }
 
	@Override
	public void run() {	
		try {
            PushUnicastMessageRequest request = new PushUnicastMessageRequest();
            request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
                                      // 4:ios 5:wp

            request.setUserId(userId);
            request.setMessage(message);

            PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
            
            if (response.getSuccessAmount() > 0)
            {
            	Utils.Result = 1;
            	Message msg = new Message();
            	msg.what = Utils.Result;
            	Utils.Handler.sendMessage(msg);
            }
        } 
		catch (ChannelClientException e) 
        {
			Utils.Result = -1;
        } 
		catch (ChannelServerException e) {
            System.out.println(String.format(
                    "request_id: %d, error_code: %d, error_message: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            Utils.Result = -1;
        }
		
		if (Utils.TargetId.isEmpty())
    	{
			Intent intent = new Intent(Utils.INTERNAL_ACTION);
	        intent.setClass(Utils.App.getApplicationContext(), MainActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        Utils.App.getApplicationContext().startActivity(intent);
    	}
	}
}