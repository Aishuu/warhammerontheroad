package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class WriteAndReadStoryActivity extends WotrActivity implements GameServiceListener {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_and_read_story);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && this.mService.getGame().isGM()) {
			getActionBar().setTitle(R.string.write_story);
		}
		this.mService.getGame().addGameServiceListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write_and_read_story, menu);
		return true;
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
				Toast toast = Toast.makeText(WriteAndReadStoryActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}
	
	@Override
	public void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}
}
