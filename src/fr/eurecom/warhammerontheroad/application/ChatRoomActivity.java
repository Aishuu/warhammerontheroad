package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity used for testing purpose as a chat room
 * 
 * @author aishuu
 *
 */
public class ChatRoomActivity extends WotrActivity implements ChatListener {
	private TextView chatRoom;
	private String newline;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		this.chatRoom = (TextView) findViewById(R.id.chatRoom);
		this.mService.getChat().addChatListener(this);
		this.newline = "";
		if(savedInstanceState != null)
			this.chatRoom.setText(savedInstanceState.getString("chatContent"));
		
	}

	@Override
	protected void onDestroy() {
		this.mService.getChat().removeChatListener(this);
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
			this.chatRoom.append("You : "+message+"\n");
		}
		editMessage.setText("");
	}
	
	public void sendFile(View view) {
		EditText editFile = (EditText) findViewById(R.id.editFile);
		if(editFile.getText().length() > 0) {
			String filename = editFile.getText().toString();
			this.mService.getNetworkParser().sendFile(filename);
		}
		editFile.setText("");
		
	}

	@Override
	public void messageReceived(String name, String message) {
		this.newline = this.newline + name + " : " + message + "\n";
		runOnUiThread(new Runnable() {
			public void run() {

				ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
				ChatRoomActivity.this.newline = "";

			}
		});
	}

	@Override
	public void userDisconnected(String name) {
		this.newline = this.newline + name + " disconnected...\n";
		runOnUiThread(new Runnable() {
			public void run() {

				ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
				ChatRoomActivity.this.newline = "";

			}
		});
	}

	@Override
	public void userConnected(String name) {
		this.newline = this.newline + name + " is now connected...\n";
		runOnUiThread(new Runnable() {
			public void run() {
				ChatRoomActivity.this.chatRoom.append(ChatRoomActivity.this.newline);
				ChatRoomActivity.this.newline = "";

			}
		});
	}

}
