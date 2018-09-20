package com.imt.remote.remotecontrol;

 
 

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent = new
				 Intent(MainActivity.this,RemoteControlServices.class);
				 startService(intent);
//				KeyStoreManager keyStoreManager = new KeyStoreManager(
//						MainActivity.this);
//				new KeystoreInitializerTask(getUniqueId())
//						.execute(keyStoreManager);
			}
		});
	}

 

	private class KeystoreInitializerTask extends
			AsyncTask<KeyStoreManager, Void, Void> {
		private final String id;

		public KeystoreInitializerTask(String id) {
			this.id = id;
		}

		@Override
		protected Void doInBackground(KeyStoreManager... keyStoreManagers) {
			if (keyStoreManagers.length != 1) {
				throw new IllegalStateException(
						"Only one key store manager expected");
			}
			keyStoreManagers[0].initializeKeyStore(id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}

	private String getUniqueId() {
		String id = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
		// null ANDROID_ID is possible on emulator
		return id != null ? id : "emulator";
	}

}
