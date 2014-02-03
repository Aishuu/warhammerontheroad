package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.model.WotrService;
import android.app.Activity;
import android.os.Bundle;

public abstract class WotrActivity extends Activity {
	protected WotrService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mService = ((WotrApplication) getApplication()).getService();
		if(this.mService != null && this.mService.getGame() != null && !this.mService.getGame().validateActivity(this))
			this.finish();
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
