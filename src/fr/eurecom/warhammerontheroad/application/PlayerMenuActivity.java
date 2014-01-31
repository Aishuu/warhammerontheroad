package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayerMenuActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_menu);
		this.mService.getGame().addGameServiceListener(this);
	}
	
	public void accessChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
	    startActivity(intent);
	}
	
	public void accessPlay(View view) {
		
	}
	
	public void accessStory(View view) {
		
	}
	
	public void createCharaDetails(View view) {
		Intent intent = new Intent(this, CharaCreationDetailsActivity.class);
	    startActivity(intent);
	}

	@Override
	public void onStateChanged(Game game) {
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
		Intent intent = new Intent(this, CombatActivity.class);
	    startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}
}
