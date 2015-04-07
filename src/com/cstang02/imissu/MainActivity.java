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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    Button copyBtn = null;
    Button missBtn = null;
    TextView logText = null;
    ScrollView scrollView = null;
    TextView idText = null;
    EditText inputText = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        //Utils.logStringCache = Utils.getLogText(getApplicationContext());
        copyBtn = (Button) findViewById(R.id.copyBtn);
        missBtn = (Button) findViewById(R.id.missBtn);
        //logText = (TextView) findViewById(R.id.logText);
        //scrollView = (ScrollView) findViewById(R.id.scrollView);
        idText = (TextView) findViewById(R.id.idText);
        inputText = (EditText) findViewById(R.id.inputText);
        
        copyBtn.setOnClickListener(this);
        missBtn.setOnClickListener(this);
        
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        
        Utils.App = getApplication();
        Utils.Handler = new Handler(){
        	public void handleMessage(Message msg)
        	{
        		super.handleMessage(msg);
        		
        		if (msg.what > 0)
        		{
        			Utils.Toast("Sent!", 0);
        		}
        	}
        };
        
        Utils.getDay(getApplicationContext());
        if (!Utils.getUserId(getApplicationContext()).isEmpty() && !Utils.getTargetId(getApplicationContext()).isEmpty())
        {
        	if (getIntent().getAction().equals("android.intent.action.MAIN"))
        	{
    			push(Utils.TargetId, Utils.UserId + " " + Utils.increaseCount(getApplicationContext()));        
        	}
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
    	if (v.getId() == R.id.copyBtn)
    	{
    		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);  
    		clipboardManager.setPrimaryClip(ClipData.newPlainText("User ID", idText.getText().toString()));
            Utils.Toast("Copied to clipboard!", 0);
    	}
    	else if (v.getId() == R.id.missBtn)
    	{  		
    		push(inputText.getText().toString(), Utils.UserId + " " + Utils.increaseCount(getApplicationContext()));
		}
    }

    public void push(String id, String msg)
    {
    	if (Utils.Day != Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        {
        	Utils.setDay(getApplicationContext(), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        	Utils.clearCount(getApplicationContext());
        }
    	
    	new Thread(new Pusher(id, msg)).start();
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
			push(Utils.TargetId, Utils.UserId + " " + Utils.increaseCount(getApplicationContext()));        
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
              
        if (!Utils.UserId.isEmpty() && idText != null && idText.getText().length() == 0)
        {  		
    		idText.setText(Utils.UserId);
    		Utils.setUserId(getApplicationContext(), Utils.UserId); 
        }
        
        if (logText != null) {
            //logText.setText(Utils.logStringCache);
        }
        
        if (scrollView != null) {
            //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
        		
        if (Utils.Result > 0)
        {
        	Utils.Result = 0;
        	
        	if (Utils.TargetId.isEmpty() && inputText != null)
        	{
        		Utils.setTargetId(getApplicationContext(), inputText.getText().toString());
        		moveTaskToBack(true);
        	}
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
        }  
    }
}
