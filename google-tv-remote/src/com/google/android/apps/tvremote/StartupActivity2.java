/*
 * Copyright (C) 2010 Google Inc.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.tvremote;



import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;

/**
 * Startup activity that checks if certificates are generated, and if not
 * begins async generation of certificates, and displays remote logo.
 *
 */
public class StartupActivity2 extends Activity {

  private boolean keystoreAvailable;
  private Button connectButton;

  @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		KeyStoreManager keyStoreManager = new KeyStoreManager(this);
		new KeystoreInitializerTask(getUniqueId()).execute(keyStoreManager);
	}
  
   

  private class KeystoreInitializerTask extends AsyncTask<
      KeyStoreManager, Void, Void> {
    private final String id;

    public KeystoreInitializerTask(String id) {
      this.id = id;
    }

    @Override
    protected Void doInBackground(KeyStoreManager... keyStoreManagers) {
      if (keyStoreManagers.length != 1) {
        throw new IllegalStateException("Only one key store manager expected");
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
