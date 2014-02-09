package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;

public class SeeEnemyStatsActivity extends WotrActivity {
	private Hero hero;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_enemy_stats);
		Intent intent=getIntent();
		int id=intent.getIntExtra("chara id",-1);
		hero=this.mService.getGame().getHero(id);
		if(hero==null)
			this.finish();
		
		WebView s, p, w, a;
		s=(WebView) findViewById(R.id.seeEnemyStats);
		p=(WebView) findViewById(R.id.seeEnemyProfil);
		w=(WebView) findViewById(R.id.seeEnemyWeapons);
		a=(WebView) findViewById(R.id.seeEnemyArmor);
		String header, hs,hp,hw,ha;
		header="<html><header><style>@font-face{font-family:'WashingtonText'; src:url('file:///android_asset/WashingtonText.ttf')} " +
				"body{color:#8B0000; background-color:rgba(#,#,#,0); font-family:WashingtonText } " +
				"td {padding:5px;} table{width:100%;text-align:center;} </style></header>";

		//Stats
		int m=hero.getStats().getStats(12);
		hs=header;
		hs +="<body><table><tr><td>Mouvement</td><td>Charge</td><td>Running</td><td>Injuries</td></tr>";
		hs+="<tr><td>"+Integer.toString(m)+"</td><td>"+Integer.toString(m*2)+"</td><td>"+Integer.toString(m*3)+"</td><td>"+Integer.toString(hero.getB())+"/"+Integer.toString(hero.getStats().getStats(9))+"</td></tr>";
		hs+="</table></body></html>";
		s.loadDataWithBaseURL(null, hs, "text/html","utf-8", null);
		s.setBackgroundColor(0x00000000);
		s.getSettings().setDefaultFontSize(25);

		//Profil
		ArrayList<String> profil=hero.getStats().getFullStats();

		hp=header;
		hp +="<body><table><tr><td>CC</td><td>CT</td><td>F</td><td>E</td><td>Ag</td><td>Int</td><td>FM</td><td>Soc</td></tr><tr>";
		int i;
		for(i=0; i<profil.size();i++){
			hp+="<td>"+profil.get(i)+"</td>";
			if(i==7)
				hp +="</tr><tr><td>A</td><td>B</td><td>BF</td><td>BE</td><td>M</td><td>Mag</td><td>PF</td><td>PD</td></tr><tr>";
		}
		hp+="</tr></table></body></html>";
		p.loadDataWithBaseURL(null, hp, "text/html","utf-8", null);
		p.setBackgroundColor(0x00000000);
		p.getSettings().setDefaultFontSize(25);

		//Weapon
		ArrayList<String[]> weapons=hero.getWeapon();

		hw=header;
		hw +="<body><table><tr><td>Name</td><td>Damage</td><td>Range</td><td>Reload</td><td>Attributes</td></tr>";
		for(String [] weapon: weapons)
			hw+="<tr><td>"+weapon[0]+"</td><td>"+weapon[1]+"</td><td>"+weapon[2]+"</td><td>"+weapon[3]+"</td><td>"+weapon[4]+"</td></tr>";
		hw+="</table></body></html>";
		w.loadDataWithBaseURL(null, hw, "text/html","utf-8", null);
		w.setBackgroundColor(0x00000000);
		w.getSettings().setDefaultFontSize(25);

		//Armor
		ArrayList<String[]> armor=hero.getArmor();

		ha=header;
		ha+="<body><table><tr><td>Type</td><td>Protected parts</td><td>Armor points</td></tr>";
		for(String [] ar: armor)
			ha+="<tr><td>"+ar[0]+"</td><td>"+ar[1]+"</td><td>"+ar[2]+"</td></tr>";
		ha+="</table></body></html>";
		a.loadDataWithBaseURL(null, ha, "text/html","utf-8", null);
		a.setBackgroundColor(0x00000000);
		a.getSettings().setDefaultFontSize(25);
		
		//Armor points
		ImageView im=(ImageView) findViewById(R.id.imSeeEnemy);
		im.setImageDrawable(hero.getResource());
		String[] points=hero.getArmorRecap();
		TextView head, arm, body, leg;
		head=(TextView) findViewById(R.id.txtSeeEnemyHead);
		arm=(TextView) findViewById(R.id.txtSeeEnemyArm);
		body=(TextView) findViewById(R.id.txtSeeEnemyBody);
		leg=(TextView) findViewById(R.id.txtSeeEnemyLeg);
		
		head.setText(points[0]);
		arm.setText(points[1]);
		body.setText(points[2]);
		leg.setText(points[3]);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_enemy_stats, menu);
		return true;
	}
	
	public void suppressChara(View view){
		this.mService.getGame().removeHero(hero);
		this.mService.getNetworkParser().removeHero(hero);
		this.finish();
	}

}
