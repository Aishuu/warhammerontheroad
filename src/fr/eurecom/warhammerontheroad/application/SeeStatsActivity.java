package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class SeeStatsActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_stats);
		this.mService.getGame().addGameServiceListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_stats, menu);
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
				Toast toast = Toast.makeText(SeeStatsActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}
}
