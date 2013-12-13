package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class JoinGameActivity extends WotrActivity implements GameServiceListener {

	private TextView availGames;
	private ArrayList<String> avail;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		this.availGames = (TextView) findViewById(R.id.availGames);
		this.mService.getGame().addGameServiceListener(this);
		if(savedInstanceState != null)
			this.availGames.setText(savedInstanceState.getString("availGames"));
	}

	@Override
	protected void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putString("availGames", this.availGames.getText().toString());
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

	public void bind(View view) {
		EditText editName = (EditText) findViewById(R.id.editName);
		EditText editId = (EditText) findViewById(R.id.editId);
		if(editName.getText().length() > 0 && editId.getText().length() > 0) {
			this.mService.getPlayer().setName(editName.getText().toString());
			this.mService.getNetworkParser().bind(Integer.parseInt(editId.getText().toString()));
		}
	}
	
	public void list(View view) {
		this.mService.getNetworkParser().listAvailableGames();
	}

	@Override
	public void userDisconnected(String name) {
	}
	
	@Override
	public void userConnected(String name) {
	}

	@Override
	public void onStateChanged(Game game) {
		switch(game.getState()) {
		case Game.STATE_GAME_CREATED:
			Intent intent = new Intent(this, ChatRoomActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void listAvailableGames(ArrayList<String> avail) {
		this.avail = avail;
		runOnUiThread(new Runnable() {
			public void run() {
				JoinGameActivity.this.availGames.setText("");
				for(String l: JoinGameActivity.this.avail) {
					JoinGameActivity.this.availGames.append(l+"\n");
				}

			}
		});
	}

}
