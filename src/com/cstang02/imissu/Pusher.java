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
	private BaiduChannelClient channelClient = new BaiduChannelClient(new ChannelKeyPair(Utils.API_KEY, Utils.API_SECRET));
	
	private String userId = null;
	private String message = null;
	private boolean feedback = true; 
	
    public Pusher(String id, String msg) {
    	userId = id;
    	message = msg;
    }
    
    public Pusher(String id, String msg, boolean fb) {
    	userId = id;
    	message = msg;
    	feedback = fb;
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
            
            if (feedback && response.getSuccessAmount() > 0)
            {
            	Message msg = new Message();
            	msg.what = 1;
            	Utils.Handler.sendMessage(msg);
            }
        } 
		catch (ChannelClientException e) {}
		catch (ChannelServerException e) {}
	}
}