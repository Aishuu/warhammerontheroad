package fr.eurecom.warhammerontheroad.application;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Chat;
import fr.eurecom.warhammerontheroad.model.ChatMessage;
import fr.eurecom.warhammerontheroad.model.Game;

/**
 * Activity used for testing purpose as a chat room
 * 
 * @author aishuu
 *
 */
public class ChatRoomActivity extends WotrActivity implements ChatListener, GameServiceListener {
	private TextView chatRoom;
	private ScrollView scroll;
	private Chat chat;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chat_room);

		this.chatRoom = (TextView) findViewById(R.id.chatRoom);
		this.mService.getChat().addChatListener(this);
		this.mService.getGame().addGameServiceListener(this);
		this.scroll=(ScrollView)findViewById(R.id.scrollChat);
		this.chat = this.mService.getChat();
		this.chatRoom.setText(Html.fromHtml(this.chat.describeAsHTML()));
	}

	@Override
	protected void onDestroy() {
		this.mService.getChat().removeChatListener(this);
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
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
			ChatMessage cm = new ChatMessage(this.mService.getGame(), message, false);
			this.chat.registerMessage(cm);
			this.chatRoom.append(Html.fromHtml(cm.describeAsHTML()+"<br />"));
			scroll.fullScroll(View.FOCUS_DOWN);
		}
		editMessage.setText("");
	}

	@Override
	public void messageReceived(final ChatMessage cm) {
		runOnUiThread(new Runnable() {
			public void run() {
				ChatRoomActivity.this.chatRoom.append(Html.fromHtml(cm.describeAsHTML()+"<br />"));
			}
		});
	}

	@Override
	public void fileTransferStatusChanged(String name, int file_status) {
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

}
