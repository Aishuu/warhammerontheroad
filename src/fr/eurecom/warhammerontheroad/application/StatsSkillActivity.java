package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.R.layout;
import fr.eurecom.warhammerontheroad.R.menu;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Toast;

public class StatsSkillActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_skill);
		this.mService.getGame().addGameServiceListener(this);
		Intent i=getIntent();
		String s=i.getStringExtra("chara id");
		Player player=(Player) this.mService.getGame().getHero(s);
		WebView w=(WebView) findViewById(R.id.statsSkillWeb);
		ArrayList<String[]> skills=player.getskills();
		
		String header="<html><header><style>@font-face{font-family:'WashingtonText'; src:url('file:///android_asset/WashingtonText.ttf')} " +
				"body{color:#8B0000; background-color:transparent; font-family:WashingtonText } " +
				"td {padding:5px;} table{width:100%; } </style></header>";
		String html=header;
		html+="<body><table>";
		for(String [] skill: skills)
			html+="<tr><td>"+skill[0]+"</td><td>"+skill[1]+"</td><td>"+skill[2]+"</td><td>"+skill[3]+"</td></tr>";
		html+="</table></body></html>";
		w.loadDataWithBaseURL(null, html, "text/html","utf-8", null);
		w.setBackgroundColor(0x00000000);
		w.getSettings().setDefaultFontSize(25);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats_skill, menu);
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
				Toast toast = Toast.makeText(StatsSkillActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}

}
