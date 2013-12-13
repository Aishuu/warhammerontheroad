package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class CreateGameActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		this.mService.getGame().addGameServiceListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_game, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	public void create(View view) {
		EditText editName = (EditText) findViewById(R.id.editName);
		if(editName.getText().length() > 0) {
			this.mService.getPlayer().setName(editName.getText().toString());
			this.mService.getNetworkParser().create();
		}
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
	public void userDisconnected(String name) {
	}

	@Override
	public void userConnected(String name) {
	}

	@Override
	public void listAvailableGames(ArrayList<String> avail) {
	}

}
