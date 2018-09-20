package com.imt.remote.remotecontrol;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class OutputDialog extends Activity{

	private TextView displayView;
	private TextView displaytitle;
	
	private String DISMISS_ACTIVITY = "com.dismiss_output_activity";
	private IntentFilter mIntentFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);
		
		displaytitle = (TextView)findViewById(R.id.out_title);
		displayView = (TextView)findViewById(R.id.output_code);
		
		Bundle bundle = getIntent().getExtras();
		String code = bundle.getString("vercify_code");
		String name = bundle.getString("client_name");
		
		
		displaytitle.setText(getResources().getString(R.string.please_input_code_message1) + "  "+ name + "  "+
				            getResources().getString(R.string.please_input_code_message2));
		displayView.setText(code);
		
		
		mIntentFilter = new IntentFilter(DISMISS_ACTIVITY);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(DISMISS_ACTIVITY)){
                finish();
            }
        }
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	 unregisterReceiver(mReceiver);
    };
}
