package fr.eurecom.warhammerontheroad.model;

import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.app.Service;
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
		this.player = null;
		this.chat = new Chat();
		this.game = new Game();
		this.np = new NetworkParser(this);
		Log.d(TAG, "Creating the socket..");
		new Thread(this.np).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
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
	
	public NetworkParser getNetworkParser() {
		return this.np;
	}
}
