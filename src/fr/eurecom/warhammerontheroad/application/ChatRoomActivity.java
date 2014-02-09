package fr.eurecom.warhammerontheroad.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.WotrService;

/**
 * Activity used for testing purpose as a chat room
 * 
 * @author aishuu
 *
 */
public class ChatRoomActivity extends WotrActivity implements ChatListener, GameServiceListener {
	private TextView chatRoom;
	private String newline;
	private ScrollView scroll;
	private final String font="<font color=", end="</font>";
	private String color;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chat_room);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		this.chatRoom = (TextView) findViewById(R.id.chatRoom);
		this.mService.getChat().addChatListener(this);
		this.mService.getGame().addGameServiceListener(this);
		this.newline = "";
		this.scroll=(ScrollView)findViewById(R.id.scrollChat);
		if(savedInstanceState != null){
			this.chatRoom.setText(savedInstanceState.getString("chatContent"));
			scroll.fullScroll(View.FOCUS_DOWN);
		}
		
	}

	@Override
	protected void onDestroy() {
		this.mService.getChat().removeChatListener(this);
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putString("chatContent", this.chatRoom.getText().toString());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Performed when the send message button is pressed
	 * 
	 * @param view the button
	 */
	public void sendMessage(View view) {
		EditText editMessage = (EditText) findViewById(R.id.editMessage);
		if(editMessage.getText().length() > 0) {
			String message = editMessage.getText().toString();
			this.mService.getNetworkParser().sendMessage(message);
			String chat=font;
			String name;
			if(this.mService.getGame().isGM()){
				color="#8B0000";
				name=WotrService.GM_NAME;
			}else{
				color=this.mService.getGame().getMe().getColor().getValue();
				name=this.mService.getName();
			}
			chat+=color+">"+name+": "+message+"\n"+end;
			this.chatRoom.append(Html.fromHtml(chat));
			scroll.fullScroll(View.FOCUS_DOWN);
		}
		editMessage.setText("");
	}
	
	/*public void sendFile(View view) {
		EditText editFile = (EditText) findViewById(R.id.editFile);
		if(editFile.getText().length() > 0) {
			String filename = editFile.getText().toString();
			this.mService.getNetworkParser().sendFile(filename);
		}
		editFile.setText("");
		
	}*/

	@Override
	public void messageReceived(String name, String message) {
		if(name.equals(WotrService.GM_NAME))
			color="#8B0000";
		else
			color=this.mService.getGame().getPlayer(name).getColor().getValue();
		this.newline =font+color+">"+this.newline + name + " : " + message + "\n"+end;
		runOnUiThread(new Runnable() {
			public void run() {

				ChatRoomActivity.this.chatRoom.append(Html.fromHtml(ChatRoomActivity.this.newline));
				ChatRoomActivity.this.newline = "";
				ChatRoomActivity.this.scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void fileTransferStatusChanged(String name, int file_status) {
	}

	@Override
	public void privateMessageReceived(String name, String message) {
		this.newline = this.newline + "[w] " + name + " : " + message + "\n";
		runOnUiThread(new Runnable() {
			public void run() {

				ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
				ChatRoomActivity.this.newline = "";

			}
		});
	}

	@Override
	public void onStateChanged(Game game, int exState) {
		int newState = game.getState();
		if(exState == Game.STATE_GAME_LAUNCHED && (newState == Game.STATE_GAME_TURN || newState == Game.STATE_GAME_WAIT_TURN)) {
			Intent intent = new Intent(this, CombatActivity.class);
		    startActivity(intent);
		    this.finish();
		}
	}

	@Override
	public void prepareFight() {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(ChatRoomActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}

	@Override
	public void userConnectionChanged(String name, boolean isNowConnected) {
		if(isNowConnected) {
			this.newline = this.newline + name + " is now connected...\n";
			runOnUiThread(new Runnable() {
				public void run() {
					ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
					ChatRoomActivity.this.newline = "";

				}
			});
		}
		else {

			this.newline = this.newline + name + " disconnected...\n";
			runOnUiThread(new Runnable() {
				public void run() {

					ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
					ChatRoomActivity.this.newline = "";

				}
			});
		}
	}

}
