package com.cstang02.imissu;

import java.util.Calendar;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class MainActivity extends Activity implements View.OnClickListener {

	private DatePicker datePicker = null;
	private Button pairBtn = null;
	private Pairer pairer = null;
	//private String mode = "remove";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        //Utils.logStringCache = Utils.getLogText(getApplicationContext());
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);
        
        pairBtn = (Button) findViewById(R.id.pairBtn);
        pairBtn.setOnClickListener(this);
        
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        
        Utils.App = getApplication();
        Utils.Handler = new Handler(){
        	public void handleMessage(Message msg)
        	{
        		super.handleMessage(msg);
        		
        		switch (msg.what)
        		{
        		case 0:
        			if (Utils.TargetId.isEmpty())
        			{
        				Utils.Toast("Pairing...", 0);
        			}
        			break;
        			
        		case 1:
        			Utils.Toast("Sent!", 0);
        			break;	
        		}
        	}
        };
        
        Utils.getDay(getApplicationContext());
        if (!Utils.getUserId(getApplicationContext()).isEmpty() && !Utils.getTargetId(getApplicationContext()).isEmpty())
        {
        	if (getIntent().getAction().equals("android.intent.action.MAIN"))
        	{
    			push();        
        	}
        }
        
        if (!Utils.UserId.isEmpty())
        {
        	pairBtn.setText("Pair");
        }
        
        new Thread() {
        	public void run()
        	{
	        	while (true)
	        	{
	        		try
	        		{
	        			Thread.sleep(3600 * 1000);
        	    		checkPushService();
	        		}
	        		catch (Exception e) {}
	        	}
        	}
        }.start();
    }

    @Override
    public void onClick(View v) {
    	String test = pairBtn.getText().toString().toLowerCase();
    	if (pairBtn.getText().toString().toLowerCase().equals("pair"))
    	{ 		
    		String date = datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth();
    		pairer = new Pairer(date, Utils.UserId);
    		new Thread(pairer).start();
    		pairBtn.setText("Stop");
		}
    	else if (pairBtn.getText().toString().toLowerCase().equals("stop"))
    	{
    		pairer.running = false;
    		//pairer = new Pairer(pairer.date, mode);
    		//pairer.running = false;
    		//new Thread(pairer).start();
    		pairBtn.setText("Pair");
    	}
    	else
    	{
    		PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(MainActivity.this, "api_key"));
    	}
    }
    
    public void push()
    {
    	if (Utils.Day != Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        {
        	Utils.setDay(getApplicationContext(), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        	Utils.clearCount(getApplicationContext());
        }
    	
    	new Thread(new Pusher(Utils.TargetId, Utils.UserId + " " + Utils.increaseCount(getApplicationContext()))).start();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        if (!Utils.TargetId.isEmpty() && !Utils.UserId.isEmpty())
        {
        	moveTaskToBack(true);
        }
    }

    @Override
    public void onResume() {
    	super.onResume();
    	
    	if (!Utils.TargetId.isEmpty() && !Utils.UserId.isEmpty())
    	{
    		moveTaskToBack(true);
    	}
    }

    @Override
    protected void onNewIntent(Intent intent) {
    	if (Utils.UserId.isEmpty() || Utils.TargetId.isEmpty())
    	{
    		updateDisplay();
    	}
    	else if (intent.getAction().equals("android.intent.action.MAIN"))
    	{
    		checkPushService();
			push();        
    	}
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //Utils.setLogText(getApplicationContext(), Utils.logStringCache);
        super.onDestroy();
    }
    
    private void checkPushService()
    {
    	if (!PushManager.isConnected(getApplicationContext()))
    	{
    		PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(MainActivity.this, "api_key"));
    	}
    }

    private void updateDisplay() {
        //Log.d(TAG, "updateDisplay, logText:" + logText + " cache: " + Utils.logStringCache);
              
        if (!Utils.UserId.isEmpty())
        {
    		Utils.setUserId(getApplicationContext(), Utils.UserId); 
    		
    		if (pairBtn.getText().toString().toLowerCase().equals("wait..."))
    		{
    			pairBtn.setText("Pair");
    		}
        }
        		
        if (!Utils.Result.isEmpty() && !Utils.Result.equals(Utils.UserId))
        {	
        	if (Utils.TargetId.isEmpty())
        	{
        		Utils.setTargetId(getApplicationContext(), Utils.Result);
        		moveTaskToBack(true);
        		Utils.Toast("Paired!", 0);
        	}
        	
        	/*
        	if (pairer != null || pairBtn.getText().toString().toLowerCase().equals("stop"))
    		{
        		mode = "pending";
    			pairBtn.performClick();
    		}
    		*/
        	
        	Utils.Result = "";
        }
        
        if (!Utils.Message.isEmpty())
        {
        	String [] strs = Utils.Message.split(" ");
    		Utils.Message = "";
    		
    		if (Utils.TargetId.isEmpty())
    		{
    			Utils.setTargetId(getApplicationContext(), strs[0]);
    			moveTaskToBack(true);
    			Utils.Toast("Paired!", 0);
    		}
    		
    		/*
    		if (pairer != null || pairBtn.getText().toString().toLowerCase().equals("stop"))
    		{
    			mode = "pending";
    			pairBtn.performClick();
    		}
    		*/
        }  
    }
}
