package fr.eurecom.warhammerontheroad.model;

import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
	public static final String GM_NAME				= "GM";

	private NetworkParser np;
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
		this.chat = new Chat();
		this.game = new Game(this);
		this.np = new NetworkParser(this);
		Log.d(TAG, "Creating the socket..");
		new Thread(this.np).start();
	}
	
	public void reinit() {
		this.np.stop();
		this.np = new NetworkParser(this);
		this.game = new Game(this);
		this.chat = new Chat();
		Log.d(TAG, "Creating the socket..");
		new Thread(this.np).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public Chat getChat() {
		return this.chat;
	}

	public String getName() {
		if(this.game.isGM())
			return WotrService.GM_NAME;
		String name = "anonymous";
		if(this.game != null && this.game.getMe() != null && this.game.getMe().getName() != null)
			name = this.game.getMe().getName();
		return name;
	}

	public Game getGame() {
		return this.game;
	}
	
	public NetworkParser getNetworkParser() {
		return this.np;
	}
	
	public Context getContext() {
		return getApplicationContext();
	}
}
