package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

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
	private ArrayList<GeneralErrorListener> generalErrorListeners;

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
		this.game = new Game(this);
		this.chat = new Chat(this.game);
		this.generalErrorListeners = new ArrayList<GeneralErrorListener>();
		this.np = new NetworkParser(this);
		Log.d(TAG, "Creating the socket..");
		new Thread(this.np).start();
	}
	
	public void reinit() {
		this.np.stop();
		this.np = new NetworkParser(this);
		this.game = new Game(this);
		this.generalErrorListeners = new ArrayList<GeneralErrorListener>();
		this.chat = new Chat(this.game);
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
	
	public void addGeneralErrorListener(GeneralErrorListener l) {
		this.generalErrorListeners.add(l);
	}
	
	public void removeGeneralErrorListener(GeneralErrorListener l) {
		this.generalErrorListeners.remove(l);
	}
	
	public void error(String s) {
		for(GeneralErrorListener l: this.generalErrorListeners)
			l.error(s);
	}
}
