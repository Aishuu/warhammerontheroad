package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SeeStatsActivity extends WotrActivity implements GameServiceListener {
	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_stats);
		this.mService.getGame().addGameServiceListener(this);
		Intent i=getIntent();
		String s=i.getStringExtra("chara id");
		player=(Player) this.mService.getGame().getHero(s);

		//Info principales
		TextView name, rC, mvt, charge, running, inj;
		name=(TextView) findViewById(R.id.txtSeeStatsName);
		rC=(TextView) findViewById(R.id.txtSeeStatsRaceCarrier);
		mvt=(TextView) findViewById(R.id.seeStatsMvt);
		charge=(TextView) findViewById(R.id.seeStatsCharge);
		running=(TextView) findViewById(R.id.seeStatsRunning);
		inj=(TextView) findViewById(R.id.seeStatsInj);

		name.setText(player.getName());
		Log.d("chara", "Race "+player.getRace().toString()+"; Carrier "+player.getJob().getName());
		rC.setText("Race "+player.getRace().toString()+"; Carrier "+player.getJob().getName());
		mvt.setText(Integer.toString(player.getStats().getStats(12)));
		charge.setText(Integer.toString(player.getStats().getStats(12)*2));
		running.setText(Integer.toString(player.getStats().getStats(12)*3));
		inj.setText(Integer.toString(player.getB())+"/"+Integer.toString(player.getStats().getStats(9)));
		
		//Armor points
		ImageView im=(ImageView) findViewById(R.id.imSeeStatsPlayer);
		im.setImageDrawable(player.getResource());
		String[] points=player.getArmorRecap();
		TextView head, arm, body, leg;
		head=(TextView) findViewById(R.id.txtSeeStatsHead);
		arm=(TextView) findViewById(R.id.txtSeeStatsArm);
		body=(TextView) findViewById(R.id.txtSeeStatsBody);
		leg=(TextView) findViewById(R.id.txtSeeStatsLeg);
		
		head.setText(points[0]);
		arm.setText(points[1]);
		body.setText(points[2]);
		leg.setText(points[3]);
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

	public void changePage(View view){
		Intent intent;
		switch(view.getId()){
		case R.id.btnSeeStatsProfil:
			intent=new Intent(this, StatsProfilActivity.class);
			break;
		case R.id.btnSeeStatsObjects:
			intent=new Intent(this, StatsObjectActivity.class);
			break;
		case R.id.btnSeeStatsSkills:
			intent=new Intent(this, StatsSkillActivity.class);
			break;
		default:
			return;
		}

		intent.putExtra("chara id", player.getName());
		startActivity(intent);

	}
}
