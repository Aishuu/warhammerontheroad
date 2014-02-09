package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;

public class StatsObjectActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_object);
		this.mService.getGame().addGameServiceListener(this);
		Intent i=getIntent();
		String s=i.getStringExtra("chara id");
		Player player=(Player) this.mService.getGame().getHero(s);
		ArrayList<String[]> weapons=player.getWeapon();
		WebView w=(WebView) findViewById(R.id.statsObjectWeapons);

		String header="<html><header><style>@font-face{font-family:'WashingtonText'; src:url('file:///android_asset/WashingtonText.ttf')} " +
				"body{color:#8B0000; background-color:rgba(#,#,#,0); font-family:WashingtonText } " +
				"td {padding:5px;} table{width:100%;text-align:center;} </style></header>";
		String html=header;
		html +="<body><table><tr><td>Name</td><td>Damage</td><td>Range</td><td>Reload</td><td>Attributes</td></tr>";
		for(String [] weapon: weapons)
			html+="<tr><td>"+weapon[0]+"</td><td>"+weapon[1]+"</td><td>"+weapon[2]+"</td><td>"+weapon[3]+"</td><td>"+weapon[4]+"</td></tr>";
		html+="</table></body></html>";
		w.loadDataWithBaseURL(null, html, "text/html","utf-8", null);
		w.setBackgroundColor(0x00000000);
		w.getSettings().setDefaultFontSize(25);

		ArrayList<String[]> armor=player.getArmor();
		WebView a=(WebView) findViewById(R.id.statsObjectArmor);

		html=header;
		html+="<body><table><tr><td>Type</td><td>Protected parts</td><td>Armor points</td></tr>";
		for(String [] ar: armor)
			html+="<tr><td>"+ar[0]+"</td><td>"+ar[1]+"</td><td>"+ar[2]+"</td></tr>";
		html+="</table></body></html>";
		a.loadDataWithBaseURL(null, html, "text/html","utf-8", null);
		a.setBackgroundColor(0x00000000);
		a.getSettings().setDefaultFontSize(25);

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
		getMenuInflater().inflate(R.menu.stats_object, menu);
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
				Toast toast = Toast.makeText(StatsObjectActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}

}
