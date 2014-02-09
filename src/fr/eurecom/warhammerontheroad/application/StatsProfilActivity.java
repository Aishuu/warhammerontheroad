package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.R.layout;
import fr.eurecom.warhammerontheroad.R.menu;
import fr.eurecom.warhammerontheroad.model.ActualStats;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class StatsProfilActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_profil);
		this.mService.getGame().addGameServiceListener(this);
		Intent i=getIntent();
		String s=i.getStringExtra("chara id");
		Player player=(Player) this.mService.getGame().getHero(s);
		ArrayList<String> stats=((ActualStats) player.getStats()).getFullStats();
		ArrayList<String> prim=((ActualStats) player.getStats()).getPrimaryStats();
		ArrayList<String> secon=((ActualStats) player.getStats()).getSecondaryStats();
		
		//First table
		//First line
		TextView cc, ct, f, e, ag, intel, fm, soc;
		cc=(TextView) findViewById(R.id.seeStatsBaseCC);
		ct=(TextView) findViewById(R.id.seeStatsBaseCT);
		f=(TextView) findViewById(R.id.seeStatsBaseF);
		e=(TextView) findViewById(R.id.seeStatsBaseE);
		ag=(TextView) findViewById(R.id.seeStatsBaseAg);
		intel=(TextView) findViewById(R.id.seeStatsBaseInt);
		fm=(TextView) findViewById(R.id.seeStatsBaseFM);
		soc=(TextView) findViewById(R.id.seeStatsBaseSoc);
		
		cc.setText(prim.get(0));
		ct.setText(prim.get(1));
		f.setText(prim.get(2));
		e.setText(prim.get(3));
		ag.setText(prim.get(4));
		intel.setText(prim.get(5));
		fm.setText(prim.get(6));
		soc.setText(prim.get(7));
		
		//Second line
		cc=(TextView) findViewById(R.id.seeStatsAvCC);
		ct=(TextView) findViewById(R.id.seeStatsAvCT);
		f=(TextView) findViewById(R.id.seeStatsAvF);
		e=(TextView) findViewById(R.id.seeStatsAvE);
		ag=(TextView) findViewById(R.id.seeStatsAvAg);
		intel=(TextView) findViewById(R.id.seeStatsAvInt);
		fm=(TextView) findViewById(R.id.seeStatsAvFM);
		soc=(TextView) findViewById(R.id.seeStatsAvSoc);
		
		cc.setText(secon.get(0));
		ct.setText(secon.get(1));
		f.setText(secon.get(2));
		e.setText(secon.get(3));
		ag.setText(secon.get(4));
		intel.setText(secon.get(5));
		fm.setText(secon.get(6));
		soc.setText(secon.get(7));
		
		//Third line
		cc=(TextView) findViewById(R.id.seeStatsTotCC);
		ct=(TextView) findViewById(R.id.seeStatsTotCT);
		f=(TextView) findViewById(R.id.seeStatsTotF);
		e=(TextView) findViewById(R.id.seeStatsTotE);
		ag=(TextView) findViewById(R.id.seeStatsTotAg);
		intel=(TextView) findViewById(R.id.seeStatsTotInt);
		fm=(TextView) findViewById(R.id.seeStatsTotFM);
		soc=(TextView) findViewById(R.id.seeStatsTotSoc);
		
		cc.setText(stats.get(0));
		ct.setText(stats.get(1));
		f.setText(stats.get(2));
		e.setText(stats.get(3));
		ag.setText(stats.get(4));
		intel.setText(stats.get(5));
		fm.setText(stats.get(6));
		soc.setText(stats.get(7));
		
		//Second table
		TextView a, b, bf, be, m, mag, pf, pd;
		//First line
		a=(TextView) findViewById(R.id.seeStatsBaseA);
		b=(TextView) findViewById(R.id.seeStatsBaseB);
		bf=(TextView) findViewById(R.id.seeStatsBaseBF);
		be=(TextView) findViewById(R.id.seeStatsBaseBE);
		m=(TextView) findViewById(R.id.seeStatsBaseM);
		mag=(TextView) findViewById(R.id.seeStatsBaseMag);
		pf=(TextView) findViewById(R.id.seeStatsBasePF);
		pd=(TextView) findViewById(R.id.seeStatsBasePD);
		
		a.setText(prim.get(8));
		b.setText(prim.get(9));
		bf.setText(prim.get(10));
		be.setText(prim.get(11));
		m.setText(prim.get(12));
		mag.setText(prim.get(13));
		pf.setText(prim.get(14));
		pd.setText(prim.get(15));
		
		//Second line
		a=(TextView) findViewById(R.id.seeStatsAvA);
		b=(TextView) findViewById(R.id.seeStatsAvB);
		bf=(TextView) findViewById(R.id.seeStatsAvBF);
		be=(TextView) findViewById(R.id.seeStatsAvBE);
		m=(TextView) findViewById(R.id.seeStatsAvM);
		mag=(TextView) findViewById(R.id.seeStatsAvMag);
		pf=(TextView) findViewById(R.id.seeStatsAvPF);
		pd=(TextView) findViewById(R.id.seeStatsAvPD);
		
		a.setText(secon.get(8));
		b.setText(secon.get(9));
		bf.setText(secon.get(10));
		be.setText(secon.get(11));
		m.setText(secon.get(12));
		mag.setText(secon.get(13));
		pf.setText(secon.get(14));
		pd.setText(secon.get(15));
		
		//Third line
		a=(TextView) findViewById(R.id.seeStatsTotA);
		b=(TextView) findViewById(R.id.seeStatsTotB);
		bf=(TextView) findViewById(R.id.seeStatsTotBF);
		be=(TextView) findViewById(R.id.seeStatsTotBE);
		m=(TextView) findViewById(R.id.seeStatsTotM);
		mag=(TextView) findViewById(R.id.seeStatsTotMag);
		pf=(TextView) findViewById(R.id.seeStatsTotPF);
		pd=(TextView) findViewById(R.id.seeStatsTotPD);
		
		a.setText(stats.get(8));
		b.setText(stats.get(9));
		bf.setText(stats.get(10));
		be.setText(stats.get(11));
		m.setText(stats.get(12));
		mag.setText(stats.get(13));
		pf.setText(stats.get(14));
		pd.setText(stats.get(15));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats_profil, menu);
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
				Toast toast = Toast.makeText(StatsProfilActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}

}
