package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Race;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NewGameIntroActivity extends WotrActivity implements GameServiceListener {
	private final static String GAME_ID = "gameID";
	private Hero a,b,c,d,e,r;
	private Player f,g,h,i,j,rr;
	private String t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game_intro);
		this.mService.getGame().addGameServiceListener(this);
		if (savedInstanceState != null) {
			TextView text = (TextView) findViewById(R.id.new_game_number);
			text.setText(Integer.toString(savedInstanceState.getInt(GAME_ID)));
		}
		else if(this.mService.getGame().getState() == Game.STATE_NO_GAME)
			this.mService.getNetworkParser().create();
		a = new Hero(this, Race.BANDIT);
		a.show();
		b = new Hero(this, Race.ORC);
		b.show();
		c = new Hero(this, Race.GUARD);
		c.show();
		d = new Hero(this, Race.SKELETON);
		d.show();
		e = new Hero(this, Race.GOBLIN);
		e.show();
		r = new Hero(this);
		t = a.describeAsString();
		r.constructFromString(mService, t);
		r.show();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if(this.mService.getGame().getState() == Game.STATE_GAME_CREATED) {
			try {
				int id = Integer.parseInt(((TextView) findViewById(R.id.new_game_number)).getText().toString());
				savedInstanceState.putInt(GAME_ID, id);
			} catch(NumberFormatException e) {
			}
		}

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	public void accessGMMenu(View view) {
		Intent intent = new Intent(this, GMMenuActivity.class);
		startActivity(intent);
		this.finish();
	}

	public void cancelGameCreation(View view) {
		this.mService.reinit();
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.mService.reinit();
		this.finish();
	}

	@Override
	public void onStateChanged(Game game, int exState) {
		final int id = game.getIdGame();
		if(game.getState() == Game.STATE_GAME_CREATED) {
			runOnUiThread(new Runnable() {
				public void run() {
					TextView text = (TextView) findViewById(R.id.new_game_number);
					text.setText(Integer.toString(id));

				}
			});
		}
	}

	@Override
	public void prepareFight() {
	}
}
