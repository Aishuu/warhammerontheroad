package fr.eurecom.warhammerontheroad.model;

import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author aishuu
 *
 */
public class WotrService extends Service {
	private static final String TAG					= "WotrService";

	private boolean connected;

	private BroadcastReceiver myBroadcastReceiver =
			new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			WotrService.this.setConnected(mWifi.isConnected());
		}
	};

	private NetworkParser np;
	private Player player;
	private Chat chat;
	private Game game;

	private final IBinder mBinder = new LocalBinder();


	public class LocalBinder extends Binder {
		public WotrService getService() {
			// Return this instance of LocalService so clients can call public methods
			return WotrService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		this.connected = mWifi.isConnected();
		registerReceiver(myBroadcastReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
		this.player = new Player();
		this.chat = new Chat();
		this.game = new Game();
		this.np = new NetworkParser(this);
		Log.d(TAG, "Creating the socket..");
		new Thread(this.np).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcastReceiver);
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public Chat getChat() {
		return this.chat;
	}

	public String getName() {
		String name = "anonymous";
		if(this.player != null && this.player.getName() != null)
			name = this.player.getName();
		return name;
	}

	public Game getGame() {
		return this.game;
	}

	public void setConnected(boolean connected) {
		if(connected && !this.connected) {
			Log.i(TAG, "connected...");
			WotrService.this.np = new NetworkParser(WotrService.this);
			if(this.game.getState() == Game.STATE_GAME_CREATED || this.game.getState() == Game.STATE_GAME_LAUNCHED)
				WotrService.this.np.setAutobind(true);
			new Thread(WotrService.this.np).start();
		}
		else if(!connected && this.connected) {
			Log.w(TAG, "disconnected...");
		}
		connected = true;
	}
	
	public NetworkParser getNetworkParser() {
		return this.np;
	}
}
