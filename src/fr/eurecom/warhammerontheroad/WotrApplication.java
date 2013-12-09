package fr.eurecom.warhammerontheroad;

import fr.eurecom.warhammerontheroad.WotrService.LocalBinder;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class WotrApplication extends Application {
	WotrService mService;
	boolean mBound;


	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		mBound = false;

		// Bind to LocalService
		Intent intent = new Intent(this, WotrService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public WotrService getService() {
		return this.mService;
	}

}
