package fr.eurecom.warhammerontheroad;

import android.app.Activity;
import android.os.Bundle;

public abstract class WotrActivity extends Activity {
	WotrService mService;
	boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mService = ((WotrApplication) getApplication()).getService();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
