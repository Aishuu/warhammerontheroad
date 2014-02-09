package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.model.GeneralErrorListener;
import fr.eurecom.warhammerontheroad.model.WotrService;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public abstract class WotrActivity extends Activity implements GeneralErrorListener {
	protected WotrService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mService = ((WotrApplication) getApplication()).getService();
		if(this.mService != null)
			this.mService.addGeneralErrorListener(this);
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
	
	@Override
	protected void onDestroy() {
		if(this.mService != null)
			this.mService.removeGeneralErrorListener(this);
		super.onDestroy();
	}
	
	@Override
	public void error(final String s) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(WotrActivity.this, s, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}
}
