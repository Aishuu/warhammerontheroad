package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class SeeStatsActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_stats);
		android.util.Log.d("seeStats","entered class");
		this.mService.getGame().addGameServiceListener(this);
		Intent i=getIntent();
		String s=i.getStringExtra("chara id");
		Player player=(Player) this.mService.getGame().getHero(s);
		
		//Info principales
		TextView name, race, carrier, mvt, charge, running, inj;
		name=(TextView) findViewById(R.id.txtSeeStatsName);
		race=(TextView) findViewById(R.id.txtSeeStatsRace);
		carrier=(TextView) findViewById(R.id.txtSeeStatsCarrier);
		mvt=(TextView) findViewById(R.id.seeStatsMvt);
		charge=(TextView) findViewById(R.id.seeStatsCharge);
		running=(TextView) findViewById(R.id.seeStatsRunning);
		inj=(TextView) findViewById(R.id.seeStatsInj);
		
		name.setText(player.getName());
		race.setText(player.getRace().toString());
		carrier.setText(player.getJob().getName());
		mvt.setText(Integer.toString(player.getStats().getStats(12)));
		charge.setText(Integer.toString(player.getStats().getStats(12)*2));
		running.setText(Integer.toString(player.getStats().getStats(12)*3));
		inj.setText(Integer.toString(player.getB())+"/"+Integer.toString(player.getStats().getStats(9)));
		//Profil principal
		
		//Profil secondaire
		
		//Details
		TextView age, gender, eye, size, hair, weight, birth, siblings;
		age=(TextView) findViewById(R.id.txtSeeStatsAge);
		gender=(TextView) findViewById(R.id.txtSeeStatsGender);
		eye=(TextView) findViewById(R.id.txtSeeStatsEyeColor);
		hair=(TextView) findViewById(R.id.txtSeeStatsHairColor);
		size=(TextView) findViewById(R.id.txtSeeStatsSize);
		weight=(TextView) findViewById(R.id.txtSeeStatsWeight);
		birth=(TextView) findViewById(R.id.txtSeeStatsBirthPlace);
		siblings=(TextView) findViewById(R.id.txtSeeStatsSiblingsNumber);
		
		age.setText(Integer.toString(player.getAge()));
		gender.setText(player.getGender().getName());
		eye.setText(player.getEyeColor());
		hair.setText(player.getHairColor());
		size.setText("1,"+Integer.toString(player.getSize())+" m");
		weight.setText(Integer.toString(player.getWeight())+" kg");
		birth.setText(player.getBirthPlace().getName());
		siblings.setText(Integer.toString(player.getSiblings()));
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
	
	public void terminate(){
		this.finish();
	}
}
