package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NewGameIntroActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game_intro);
		this.mService.getGame().addGameServiceListener(this);
		this.mService.getNetworkParser().create();
	}
	
	public void accessGMMenu(View view) {
		Intent intent = new Intent(this, GMMenuActivity.class);
	    startActivity(intent);
	}

	public void cancelGameCreation(View view) {
		this.finish();
	}

	@Override
	public void onStateChanged(Game game) {
		if(game.getState() == Game.STATE_GAME_CREATED) {
			TextView text = (TextView) findViewById(R.id.new_game_number);
			text.setText(Integer.toString(game.getIdGame()));
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

	@Override
	public void beginFight() {
	}
}
