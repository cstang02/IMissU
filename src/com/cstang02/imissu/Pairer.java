package com.cstang02.imissu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.os.Message;

public class Pairer implements Runnable {
	
	public static final String url = "http://imissuservice.sinaapp.com/?date=%1$s&id=%2$s";
	public boolean running = true;
	
	public String date = null;
	public String id = null;
	
	public Pairer(String date, String id) {
    	this.date = date;
    	this.id = id;
    }

	@Override
	public void run() {
		
		do
		{
			Message msg = new Message();
        	msg.what = 0;
        	Utils.Handler.sendMessage(msg);
        	  	
			BufferedReader in = null;
			String result = "";
			
			try
			{
				HttpURLConnection connection = (HttpURLConnection)new URL(String.format(url, date, id)).openConnection();
	
	            connection.setRequestMethod("GET");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            
	            connection.connect();
	
	            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            
	            result = in.readLine();
	            
	            if (result == null) result = "";
			}
			catch (Exception e){}
			finally
			{
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (Exception e) {}
				}
			}
			
			boolean valid = false;
			
			if (!result.isEmpty() && !result.equals(Utils.UserId))
			{
				try
				{
					Long.parseLong(result);
					valid = true;
				}
				catch (Exception e) 
				{
					Utils.Result = "";	
				}
			}
			
			if (valid)
			{
				Utils.Result = result;
				running = false;
				
				if (Utils.TargetId.isEmpty())
		    	{
					Intent intent = new Intent(Utils.INTERNAL_ACTION);
			        intent.setClass(Utils.App.getApplicationContext(), MainActivity.class);
			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        Utils.App.getApplicationContext().startActivity(intent);
		    	}
				break;
			}
			else if (running)
			{
				Utils.Result = "";
				
				try
				{
					Thread.sleep(3000);
				}
				catch (Exception e) {}
			}
		} while (running && Utils.TargetId.isEmpty());
	}
}
