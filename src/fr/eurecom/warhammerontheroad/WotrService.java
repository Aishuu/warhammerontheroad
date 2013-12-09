package fr.eurecom.warhammerontheroad;

import java.io.IOException;

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
import static fr.eurecom.warhammerontheroad.Cmds.*;

public class WotrService extends Service {
	private static final String TAG					= "WotrService";

	private boolean connected;

	private BroadcastReceiver myBroadcastReceiver =
			new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWifi.isConnected()) {
				if(!connected)
					Log.e(TAG, "connected...");
				connected = true;
			} else {
				if(connected)
					Log.e(TAG, "disconnected...");
				connected = false;
			}
		}
	};

	private NetworkParser np;
	private Player player;
	private Chat chat;
	private Game game;

	private final IBinder mBinder = new LocalBinder();


	public class LocalBinder extends Binder {
		WotrService getService() {
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
		this.player.setName("George");
		this.chat = new Chat();
		this.game = new Game();
		this.np = new NetworkParser(this);
		try {
			Log.d(TAG, "Creating the socket..");
			this.np.connect(NetworkParser.SERVER_ADDR, NetworkParser.SERVER_PORT);
			this.np.start();
		} catch (IOException e) {
			Log.e(TAG, "Server is unreachable...");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcastReceiver);
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public void receiveMessage(String name, String message) {
		this.chat.receiveMessage(name,  message);
	}
	
	public void userDisconnected(String name) {
		this.chat.userDisconnected(name);
		this.game.userDisconnected(name);
	}
	
	public void userConnected(String name) {
		this.chat.userConnected(name);
		this.game.userConnected(name);
	}
	
	public void sendMessage(String message) {
		try {
			this.np.sendCommand(CMD_MESSAGE, message);
		} catch (IOException e) {
			Log.e(TAG, "Failed to send the message...");
		}
		
	}
	
	public void bind(int id) {
		try {
			if(this.game.getState() != Game.STATE_NO_GAME && this.game.getState() != Game.STATE_GAME_CREATE_WAIT)
				return;
			if(id < 100000) {
				this.np.sendCommand(CMD_BIND, String.format("%05d", id), this.getName());
				this.game.change_state(Game.STATE_GAME_CREATED);
			}
			else
				Log.e(TAG, "bind : number "+id+" is too damn big !");
		} catch (IOException e) {
			Log.e(TAG, "Could not bind to server...");
		}
	}
	
	public void create() {
		try {
			this.np.sendCommand(CMD_CREATE);
			this.game.change_state(Game.STATE_GAME_CREATE_WAIT);
		} catch (IOException e) {
			Log.e(TAG, "Unable to create a game...");
		}
	}
	
	private String getName() {
		String name = "anonymous";
		if(this.player != null && this.player.getName() != null)
			name = this.player.getName();
		return name;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void listAvailableGames() {
		try {
			this.np.sendCommand(CMD_LIST);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't send list command...");
		}
	}
	
	public void addChatListener(ChatListener l) {
		this.chat.addChatListener(l);
	}
	
	public void removeChatListener(ChatListener l) {
		this.chat.removeChatListener(l);
	}
	
	public void addGameServiceListener(GameServiceListener l) {
		this.game.addGameServiceListener(l);
	}
	
	public void removeGameServiceListener(GameServiceListener l) {
		this.game.removeGameServiceListener(l);
	}
	
}
